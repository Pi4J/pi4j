/*
 * Mock PWM driver, that holds it's state in memory
 *
 * Copyright (C) 2025 Nick Gritsenko
 */

#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/slab.h>
#include <linux/platform_device.h>
#include <linux/pwm.h>

#define MODULE_NAME "pwm-mock"

/*
* PWM Mock driver provides basic functionality to integration test of Pi4j project and regression.
* It simulates real PWM Chip by storing the applied state per channel and sending it back on request.
* Limitations:
* - a single mock chip is created, with a configurable number of channels
*/

// Number of channels in the PWM chip
static int channels = 3;
module_param(channels, int, 0444);
MODULE_PARM_DESC(channels, "Number of channels on the mock PWM chip (default 3)");

static struct platform_device *pwm_mock_device;

// per-channel stored state, indexed by hwpwm; allocated for 'channels' entries
static struct pwm_state *channel_states;

// helper method to print messages
static const char* getPolarityName(enum pwm_polarity polarity) {
    switch (polarity) {
        case PWM_POLARITY_NORMAL:   return "NORMAL";
        case PWM_POLARITY_INVERSED: return "INVERSED";
        default:                    return "UNKNOWN";
    }
}

// returns the current (stored) state of a channel back to the PWM core
static int pwm_mock_get_state(struct pwm_chip *chip, struct pwm_device *pwm_dev,
                           		     struct pwm_state *state)
{
    *state = channel_states[pwm_dev->hwpwm];
    dev_info(&chip->dev, "Get state of pwm%d: period=%llu, duty_cycle=%llu, polarity=%s, enabled=%d",
        pwm_dev->hwpwm,
        state->period,
        state->duty_cycle,
        getPolarityName(state->polarity),
        state->enabled);
    return 0;
}

// applies (stores) a new state for a channel
static int pwm_mock_apply(struct pwm_chip *chip, struct pwm_device *pwm_dev,
			      const struct pwm_state *state)
{
    struct pwm_state *current_state = &channel_states[pwm_dev->hwpwm];

    if (current_state->period != state->period) {
        dev_info(&chip->dev, "Set period of pwm%d: %llu",
            pwm_dev->hwpwm,
            state->period);
    }

    if (current_state->duty_cycle != state->duty_cycle) {
        dev_info(&chip->dev, "Set duty_cycle of pwm%d: %llu",
            pwm_dev->hwpwm,
            state->duty_cycle);
    }

    if (current_state->polarity != state->polarity) {
        dev_info(&chip->dev, "Set polarity of pwm%d: %s",
            pwm_dev->hwpwm,
            getPolarityName(state->polarity));
    }

    if (current_state->enabled != state->enabled) {
        dev_info(&chip->dev, "Set enabled pwm%d: %s",
            pwm_dev->hwpwm,
            state->enabled ? "true" : "false");
    }

    // save the state for this channel
    *current_state = *state;

    return 0;
}

// Export PWM channel
static int pwm_mock_request(struct pwm_chip *chip, struct pwm_device *pwm_dev)
{
    dev_info(&chip->dev, "Export channel %d", pwm_dev->hwpwm);
    return 0;
}

// Unexport PWM channel
static void pwm_mock_free(struct pwm_chip *chip, struct pwm_device *pwm_dev)
{
    dev_info(&chip->dev, "Unexport channel %d", pwm_dev->hwpwm);
}

static const struct pwm_ops pwm_mock_ops = {
    .request = pwm_mock_request,
    .free = pwm_mock_free,
    .apply = pwm_mock_apply,
    .get_state =  pwm_mock_get_state
};

// Probe PWM device
static int pwm_mockup_pwm_probe(struct platform_device *pdev)
{
    struct pwm_chip *chip;
    int ret;

    if (channels <= 0) {
        dev_err(&pdev->dev, "invalid channel count %d\n", channels);
        return -EINVAL;
    }

    // per-channel state storage, freed automatically with the platform device
    channel_states = devm_kcalloc(&pdev->dev, channels, sizeof(*channel_states), GFP_KERNEL);
    if (!channel_states)
        return -ENOMEM;

    // chip is allocated and added through devm, so it is removed and freed on unbind
    chip = devm_pwmchip_alloc(&pdev->dev, channels, 0);
    if (IS_ERR(chip)) {
        dev_err(&pdev->dev, "failed to allocate PWM chip\n");
        return PTR_ERR(chip);
    }

    chip->ops = &pwm_mock_ops;

    ret = devm_pwmchip_add(&pdev->dev, chip);
    if (ret < 0) {
        dev_err(&pdev->dev, "failed to add PWM chip: %d\n", ret);
        return ret;
    }

    dev_info(&chip->dev, "Created new mock pwmchip with %d channels", channels);
    return 0;
}

static struct platform_driver mock_pwm_driver = {
    .probe = pwm_mockup_pwm_probe,
    .driver = {
        .name = MODULE_NAME,
    },
};

// Entrypoint
static int __init pwm_mock_init(void)
{
    int err = 0;

    err = platform_driver_register(&mock_pwm_driver);
    if (err) {
        pr_err("pwm-mock: could not register driver");
        return err;
    }
    pwm_mock_device = platform_device_register_simple(MODULE_NAME, 0, NULL, 0);
    if (IS_ERR(pwm_mock_device)) {
        pr_err("pwm-mock: error registring device");
        platform_driver_unregister(&mock_pwm_driver);
        return PTR_ERR(pwm_mock_device);
    }

    return 0;
}

// Removing driver
static void __exit pwm_mock_exit(void)
{
    pr_info("pwm-mock: Removing PWM Mock kernel driver");

    // unregistering the device triggers devm cleanup of the pwm chip
    platform_device_unregister(pwm_mock_device);
    platform_driver_unregister(&mock_pwm_driver);
}

module_init(pwm_mock_init);
module_exit(pwm_mock_exit);

MODULE_LICENSE("Dual MIT/GPL");
MODULE_DESCRIPTION("PWM Mock Device");
MODULE_AUTHOR("Nick Gritsenko");
