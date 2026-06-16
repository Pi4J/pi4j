/*
 * Mock GPIO driver, that holds its line state in memory
 *
 * Copyright (C) 2025 Nick Gritsenko
 */

#include <linux/init.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/slab.h>
#include <linux/debugfs.h>
#include <linux/platform_device.h>
#include <linux/interrupt.h>
#include <linux/irq.h>
#include <linux/irqdomain.h>
#include <linux/irq_sim.h>
#include <linux/gpio/driver.h>
#include <linux/gpio/consumer.h>
#include <linux/gpio/machine.h>

#define MODULE_NAME "gpio-mock"
#define GPIO_MOCK_MAX_CHIPS 8

/*
* GPIO Mock driver provides basic functionality to integration test of Pi4j project and regression.
* It registers real GPIO character devices ('/dev/gpiochipN') backed by an in-memory line state,
* so the FFM plugin can talk to them through the regular gpiod / character device ABI.
* Capabilities:
* - output lines: 'set' stores the driven value, 'get' echoes it back
* - input lines: 'get' returns the simulated input level, which a test can change at runtime
*   through debugfs at '/sys/kernel/debug/gpio-mock/<label>/lineN'
* - edge events: changing an input level through debugfs raises a simulated interrupt, so a
*   consumer waiting on a line request file descriptor with poll() is woken up and can read
*   the new state (rising/falling edge detection through the character device ABI)
*
* The layout is fully customizable from the insmod command line (see gpio-setup.sh):
* - ngpios=8,1            -> two chips with 8 and 1 lines (the number of values = number of chips)
* - labels=accessible,... -> optional human readable label per chip
* - hog_lines=2,-1        -> optional line to keep busy ('occupied') per chip, -1 for none
* When no parameters are given it defaults to the historical layout:
* - chip 0 ('accessible')   -> '/dev/gpiochip0', 8 lines, line 2 hogged and reported as occupied
* - chip 1 ('inaccessible') -> '/dev/gpiochip1', 1 line
*/

/* Optional verbose debug logging, toggled by the 'debug' module parameter (set from gpio-setup.sh) */
static int debug;
module_param(debug, int, 0644);
MODULE_PARM_DESC(debug, "Enable verbose debug logging (default 0)");

#define mock_dbg(dev, fmt, ...) \
	do { if (debug) dev_info(dev, fmt, ##__VA_ARGS__); } while (0)

/* Number of lines per chip; the count of values also defines how many chips we create */
static int ngpios[GPIO_MOCK_MAX_CHIPS];
static int num_chips;
module_param_array(ngpios, int, &num_chips, 0444);
MODULE_PARM_DESC(ngpios, "Number of lines for each mock chip, e.g. ngpios=8,1 (default 8,1)");

/* Optional label per chip */
static char *labels[GPIO_MOCK_MAX_CHIPS];
static int num_labels;
module_param_array(labels, charp, &num_labels, 0444);
MODULE_PARM_DESC(labels, "Optional label for each mock chip, e.g. labels=accessible,inaccessible");

/* Optional line index to keep busy ('occupied') per chip, -1 for none */
static int hog_lines[GPIO_MOCK_MAX_CHIPS];
static int num_hogs;
module_param_array(hog_lines, int, &num_hogs, 0444);
MODULE_PARM_DESC(hog_lines, "Line index to mark occupied per chip, -1 for none (default 2,-1)");

/* per-line state */
struct gpio_mock_line {
	int direction;	/* GPIO_LINE_DIRECTION_IN or GPIO_LINE_DIRECTION_OUT */
	int value;	/* driven value for outputs, simulated level for inputs */
};

/* per-chip state */
struct gpio_mock_chip {
	struct gpio_chip gc;
	struct gpio_mock_line *lines;
	struct irq_domain *irq_sim_domain;	/* simulated interrupts for edge events */
	struct dentry *debugfs_dir;
	struct gpio_desc *hogged;		/* line we keep busy to look 'occupied', may be NULL */
};

static struct platform_device *gpio_mock_device;
static struct gpio_mock_chip *mock_chips;
static int mock_chips_count;
static struct dentry *debugfs_root;

/* returns the current direction of a line, 0 = out, 1 = in */
static int gpio_mock_get_direction(struct gpio_chip *gc, unsigned int offset)
{
	struct gpio_mock_chip *chip = gpiochip_get_data(gc);

	return chip->lines[offset].direction;
}

/* switches a line to input */
static int gpio_mock_direction_input(struct gpio_chip *gc, unsigned int offset)
{
	struct gpio_mock_chip *chip = gpiochip_get_data(gc);

	chip->lines[offset].direction = GPIO_LINE_DIRECTION_IN;
	mock_dbg(gc->parent, "%s: set line %u as input", gc->label, offset);
	return 0;
}

/* switches a line to output and drives the given value */
static int gpio_mock_direction_output(struct gpio_chip *gc, unsigned int offset, int value)
{
	struct gpio_mock_chip *chip = gpiochip_get_data(gc);

	chip->lines[offset].direction = GPIO_LINE_DIRECTION_OUT;
	chip->lines[offset].value = !!value;
	mock_dbg(gc->parent, "%s: set line %u as output with value %d", gc->label, offset, !!value);
	return 0;
}

/* reads a line: driven value for outputs, simulated level for inputs */
static int gpio_mock_get(struct gpio_chip *gc, unsigned int offset)
{
	struct gpio_mock_chip *chip = gpiochip_get_data(gc);
	int value = chip->lines[offset].value;

	mock_dbg(gc->parent, "%s: get line %u value %d", gc->label, offset, value);
	return value;
}

/* stores the driven value of an output line */
static int gpio_mock_set(struct gpio_chip *gc, unsigned int offset, int value)
{
	struct gpio_mock_chip *chip = gpiochip_get_data(gc);

	chip->lines[offset].value = !!value;
	mock_dbg(gc->parent, "%s: set line %u value %d", gc->label, offset, !!value);
	return 0;
}

/* maps a line to a simulated interrupt so consumers can poll() for edge events */
static int gpio_mock_to_irq(struct gpio_chip *gc, unsigned int offset)
{
	struct gpio_mock_chip *chip = gpiochip_get_data(gc);

	return irq_create_mapping(chip->irq_sim_domain, offset);
}

static const struct gpio_chip gpio_mock_template = {
	.label			= MODULE_NAME,
	.owner			= THIS_MODULE,
	.base			= -1,	/* let the core pick the base */
	.can_sleep		= false,
	.get_direction		= gpio_mock_get_direction,
	.direction_input	= gpio_mock_direction_input,
	.direction_output	= gpio_mock_direction_output,
	.get			= gpio_mock_get,
	.set			= gpio_mock_set,
	.to_irq			= gpio_mock_to_irq,
};

/*
 * Raises a simulated edge interrupt for a line if a consumer has requested edge
 * detection whose trigger type matches the new level. This is what makes a
 * blocking poll() on a line request file descriptor return when the input changes.
 */
static void gpio_mock_fire_edge(struct gpio_mock_chip *chip, unsigned int offset, int value)
{
	int irq = irq_find_mapping(chip->irq_sim_domain, offset);
	int irq_type;

	if (!irq)
		/* nobody mapped an interrupt for this line, nothing to report */
		return;

	irq_type = irq_get_trigger_type(irq);
	if ((value && (irq_type & IRQ_TYPE_EDGE_RISING)) ||
	    (!value && (irq_type & IRQ_TYPE_EDGE_FALLING)))
		irq_set_irqchip_state(irq, IRQCHIP_STATE_PENDING, true);
}

/*
 * debugfs hook so a test can read/write the raw in-memory value of a line.
 * For input lines this is the 'change value support' - writing here changes
 * what a subsequent 'get' returns and raises a matching edge event. For output
 * lines it lets a test inspect the value the consumer has driven.
 */
struct gpio_mock_dbg {
	struct gpio_mock_chip *chip;
	unsigned int offset;
};

static int gpio_mock_dbg_get(void *data, u64 *val)
{
	struct gpio_mock_dbg *dbg = data;

	*val = dbg->chip->lines[dbg->offset].value;
	return 0;
}

static int gpio_mock_dbg_set(void *data, u64 val)
{
	struct gpio_mock_dbg *dbg = data;
	struct gpio_mock_chip *chip = dbg->chip;
	unsigned int offset = dbg->offset;
	int value = val ? 1 : 0;

	/* report an edge before updating, but only for lines acting as inputs */
	if (chip->lines[offset].value != value &&
	    chip->lines[offset].direction == GPIO_LINE_DIRECTION_IN)
		gpio_mock_fire_edge(chip, offset, value);

	chip->lines[offset].value = value;
	mock_dbg(chip->gc.parent, "%s: debugfs set line %u value %d", chip->gc.label, offset, value);
	return 0;
}
DEFINE_DEBUGFS_ATTRIBUTE(gpio_mock_dbg_fops, gpio_mock_dbg_get, gpio_mock_dbg_set, "%llu\n");

/* tears down the irq mappings created on demand by gpio_mock_to_irq */
static void gpio_mock_dispose_mappings(void *data)
{
	struct gpio_mock_chip *chip = data;
	int i, irq;

	for (i = 0; i < chip->gc.ngpio; i++) {
		irq = irq_find_mapping(chip->irq_sim_domain, i);
		if (irq)
			irq_dispose_mapping(irq);
	}
}

/* registers one mock gpio chip, its simulated irqs, debugfs files and an optional hogged line */
static int gpio_mock_add_chip(struct device *parent, struct gpio_mock_chip *chip,
			      const char *label, int ngpio, int hog_line)
{
	int ret, i;

	chip->lines = devm_kcalloc(parent, ngpio, sizeof(*chip->lines), GFP_KERNEL);
	if (!chip->lines)
		return -ENOMEM;

	/* power-on default: every line is an input reading low */
	for (i = 0; i < ngpio; i++) {
		chip->lines[i].direction = GPIO_LINE_DIRECTION_IN;
		chip->lines[i].value = 0;
	}

	chip->gc = gpio_mock_template;
	chip->gc.parent = parent;
	chip->gc.label = label;
	chip->gc.ngpio = ngpio;

	/* a per-chip pool of simulated interrupts, one possible mapping per line */
	chip->irq_sim_domain = devm_irq_domain_create_sim(parent, NULL, ngpio);
	if (IS_ERR(chip->irq_sim_domain)) {
		dev_err(parent, "failed to create simulated irq domain for '%s'", label);
		return PTR_ERR(chip->irq_sim_domain);
	}
	ret = devm_add_action_or_reset(parent, gpio_mock_dispose_mappings, chip);
	if (ret)
		return ret;

	ret = devm_gpiochip_add_data(parent, &chip->gc, chip);
	if (ret) {
		dev_err(parent, "failed to add mock gpio chip '%s': %d", label, ret);
		return ret;
	}

	/* expose each line through debugfs for runtime input value changes */
	chip->debugfs_dir = debugfs_create_dir(label, debugfs_root);
	for (i = 0; i < ngpio; i++) {
		struct gpio_mock_dbg *dbg;
		char name[16];

		dbg = devm_kzalloc(parent, sizeof(*dbg), GFP_KERNEL);
		if (!dbg)
			return -ENOMEM;
		dbg->chip = chip;
		dbg->offset = i;
		scnprintf(name, sizeof(name), "line%d", i);
		debugfs_create_file(name, 0644, chip->debugfs_dir, dbg, &gpio_mock_dbg_fops);
	}

	/* keep one line busy so userspace sees it as occupied (mirrors a gpio hog) */
	if (hog_line >= 0 && hog_line < ngpio) {
		chip->hogged = gpiochip_request_own_desc(&chip->gc, hog_line, "occupied",
							 GPIO_LOOKUP_FLAGS_DEFAULT, GPIOD_OUT_HIGH);
		if (IS_ERR(chip->hogged)) {
			dev_warn(parent, "could not hog line %d on '%s': %ld",
				 hog_line, label, PTR_ERR(chip->hogged));
			chip->hogged = NULL;
		} else {
			mock_dbg(parent, "%s: line %d hogged as 'occupied'", label, hog_line);
		}
	}

	mock_dbg(parent, "Created mock gpio chip '%s' with %d lines", label, ngpio);
	return 0;
}

static int gpio_mock_probe(struct platform_device *pdev)
{
	int ret, i;

	/* fall back to the historical layout when nothing was passed on insmod */
	if (num_chips == 0) {
		ngpios[0] = 8;
		ngpios[1] = 1;
		num_chips = 2;
		if (num_labels == 0) {
			labels[0] = "accessible";
			labels[1] = "inaccessible";
			num_labels = 2;
		}
		if (num_hogs == 0) {
			hog_lines[0] = 2;
			hog_lines[1] = -1;
			num_hogs = 2;
		}
	}

	debugfs_root = debugfs_create_dir(MODULE_NAME, NULL);

	mock_chips_count = num_chips;
	mock_chips = devm_kcalloc(&pdev->dev, mock_chips_count, sizeof(*mock_chips), GFP_KERNEL);
	if (!mock_chips) {
		ret = -ENOMEM;
		goto err;
	}

	for (i = 0; i < mock_chips_count; i++) {
		const char *label;
		int hog = (i < num_hogs) ? hog_lines[i] : -1;

		if (ngpios[i] <= 0) {
			dev_err(&pdev->dev, "chip %d has an invalid line count %d", i, ngpios[i]);
			ret = -EINVAL;
			goto err;
		}

		if (i < num_labels && labels[i])
			label = labels[i];
		else
			label = devm_kasprintf(&pdev->dev, GFP_KERNEL, "gpio-mock-%d", i);
		if (!label) {
			ret = -ENOMEM;
			goto err;
		}

		ret = gpio_mock_add_chip(&pdev->dev, &mock_chips[i], label, ngpios[i], hog);
		if (ret)
			goto err;
	}

	return 0;

err:
	debugfs_remove_recursive(debugfs_root);
	return ret;
}

static void gpio_mock_remove(struct platform_device *pdev)
{
	int i;

	mock_dbg(&pdev->dev, "Removing GPIO Mock kernel driver");
	for (i = 0; i < mock_chips_count; i++) {
		if (mock_chips[i].hogged)
			gpiochip_free_own_desc(mock_chips[i].hogged);
	}
	debugfs_remove_recursive(debugfs_root);
}

static struct platform_driver gpio_mock_driver = {
	.driver = {
		.name = MODULE_NAME,
	},
	.probe = gpio_mock_probe,
	.remove = gpio_mock_remove,
};

static int __init gpio_mock_init(void)
{
	int err;

	err = platform_driver_register(&gpio_mock_driver);
	if (err) {
		pr_err("gpio-mock: could not register driver");
		return err;
	}

	gpio_mock_device = platform_device_register_simple(MODULE_NAME, PLATFORM_DEVID_NONE, NULL, 0);
	if (IS_ERR(gpio_mock_device)) {
		pr_err("gpio-mock: error registering device");
		platform_driver_unregister(&gpio_mock_driver);
		return PTR_ERR(gpio_mock_device);
	}

	return 0;
}

static void __exit gpio_mock_exit(void)
{
	platform_device_unregister(gpio_mock_device);
	platform_driver_unregister(&gpio_mock_driver);
}

module_init(gpio_mock_init);
module_exit(gpio_mock_exit);

MODULE_LICENSE("Dual MIT/GPL");
MODULE_DESCRIPTION("GPIO Mock Device");
MODULE_AUTHOR("Nick Gritsenko");
