/*
 * Mock PWM driver, that holds it's state in memory
 *
 * Copyright (C) 2025 Nick Gritsenko
 */

#include <linux/init.h>
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/platform_device.h>
#include <linux/pwm.h>

#define MODULE_NAME "pwm-mock"

/*
* PWM Mock driver provides basic functionality to integration test of Pi4j project and regression.
* It simulates real PWM Chip by storing data into internal state and send it back on request.
* Limitations:
* - underlying buffer is limited to 1024 chars
*/

// Number of channels in PWM Chip, default value
static int channels = 3;

static struct platform_device *pwm_mock_device;
struct pwm_chip* chip;
static struct pwm_state internal_pwm_state;

// helper method to print messages
static const char* getPolarityName(enum pwm_polarity polarity) {
    switch (polarity) {
        case PWM_POLARITY_NORMAL:   return "NORMAL";
        case PWM_POLARITY_INVERSED: return "INVERSED";
        default:                    return "UNKNOWN";
    }
}

// gets initial state of pwmchip
static int pwm_mock_get_state(struct pwm_chip *chip, struct pwm_device *pwm_dev,
                           		     struct pwm_state *state)
{
    internal_pwm_state = *state;
    dev_info(&chip->dev, "Get inital state of pwm%d: period=%llu, duty_cycle=%llu, polarity=%s, enabled=%d",
        pwm_dev->hwpwm,
        state->period,
        state->duty_cycle,
        getPolarityName(state->polarity),
        state->enabled);
    return 0;
}

// applies new state to pwmchip
static int pwm_mock_apply(struct pwm_chip *chip, struct pwm_device *pwm_dev,
			      const struct pwm_state *state)
{   
    if (internal_pwm_state.period != state->period) {
        dev_info(&chip->dev, "Set period of pwm%d: %llu",
            pwm_dev->hwpwm,
            state->period);
    }

    if (internal_pwm_state.duty_cycle != state->duty_cycle) {
        dev_info(&chip->dev, "Set duty_cycle of pwm%d: %llu",
            pwm_dev->hwpwm,
            state->duty_cycle);
    }

    if (internal_pwm_state.polarity != state->polarity) {
        dev_info(&chip->dev, "Set polarity of pwm%d: %s",
            pwm_dev->hwpwm,
            getPolarityName(state->polarity));
    }

    if (internal_pwm_state.enabled != state->enabled) {
        dev_info(&chip->dev, "Set enabled pwm%d: %s",
            pwm_dev->hwpwm,
            state->enabled ? "true" : "false");
    }

    // save the state
    internal_pwm_state = *state;    

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
    int ret;

    chip = pwmchip_alloc(&pdev->dev, channels, sizeof(*chip));
    if (!chip) {
        dev_err(&pdev->dev, "failed to add allocate PWM chip\n");
        return -ENOMEM;
    }

    chip->ops = &pwm_mock_ops;
    chip->npwm = channels;

    ret = pwmchip_add(chip);
    if (ret < 0) {
        dev_err(&pdev->dev, "failed to add PWM chip: %d\n", ret);
        return ret;
    }

    dev_info(&chip->dev, "Created new mock pwmchip");
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
        if (pwm_mock_device) {
            platform_device_unregister(pwm_mock_device);
        }
        return PTR_ERR(pwm_mock_device);
    }

    return 0;
}

// Removing driver
static void __exit pwm_mock_exit(void)
{
    dev_info(&chip->dev, "Removing PWM Mock kernel driver");

    platform_driver_unregister(&mock_pwm_driver);
    platform_device_unregister(pwm_mock_device);

    pwmchip_remove(chip);
}

module_init(pwm_mock_init);
module_exit(pwm_mock_exit);

MODULE_LICENSE("Dual MIT/GPL");
MODULE_DESCRIPTION("PWM Mock Device");
MODULE_AUTHOR("Nick Gritsenko");
