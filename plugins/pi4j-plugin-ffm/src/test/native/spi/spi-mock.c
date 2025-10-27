/*
 * Mock SPI driver, that holds it's data in memory
 *
 * Copyright (C) 2025 Nick Gritsenko
 */

#include <linux/init.h>
#include <linux/printk.h>
#include <linux/kernel.h>
#include <linux/module.h>
#include <linux/slab.h>
#include <linux/errno.h>
#include <linux/uaccess.h>
#include <linux/spi/spi.h>

#include <linux/platform_device.h>

#define MODULE_NAME "spi-mock"

/*
* SPI Mock driver provides basic functionality to integration test of Pi4j project and regression.
* It simulates real SPI Bus by echoing all incoming transfers.
* Limitations:
* - underlying buffer is limited to 1024 chars
*/

static struct spi_controller *master;
static struct spi_device *spi_dev;

static unsigned char * internal_buf;

static int spi_mock_transfer_one(struct spi_controller *ctlr, struct spi_device *spi,
                struct spi_transfer *transfer)
{
    if (transfer->rx_buf && transfer->tx_buf) {
        // if we send & receive in the same transfer, simply copy tx buffer to rx buffer (echo)
        unsigned char * rx_buf = transfer->rx_buf;
        const unsigned char * tx_buf = transfer->tx_buf;
        int i;

        kfree(internal_buf);
        internal_buf = (char *)kmalloc(transfer->len * sizeof(char), GFP_KERNEL);
        for (i = 0; i < transfer->len; i++) {
            rx_buf[i] = tx_buf[i];
            internal_buf[i] = tx_buf[i];
        }

        dev_info(&spi_dev->dev, "Transfer tx and rx (echo): %s", rx_buf);
    } else if (transfer->rx_buf) {
        // if we are reading, simply return what is in the buffer
        unsigned char * rx_buf = transfer->rx_buf;
        int i;
        for (i = 0; i < transfer->len; i++)
            rx_buf[i] = internal_buf[i];
        dev_info(&spi_dev->dev, "Reading, rx_buf: %s", rx_buf);
    } else if (transfer->tx_buf) {
        const unsigned char * tx_buf = transfer->tx_buf;
        int i;

        kfree(internal_buf);
        internal_buf = (char *)kmalloc(transfer->len * sizeof(char), GFP_KERNEL);
        // default is to save incoming buffer
        for (i = 0; i < transfer->len; i++)
            internal_buf[i] = tx_buf[i];
        dev_info(&spi_dev->dev, "Writing, tx_buf: %s", tx_buf);
    }
    
    spi_finalize_current_transfer(ctlr);

    return 0;
}

struct spi_board_info chip = {
    // dirty hack to make spidev autloaded and binded to this driver
    .modalias = "bk4",
    .bus_num = 0,
    .chip_select = 0,
};

static int spi_mock_probe(struct platform_device *pdev)
{
    int err = 0;

    master = spi_alloc_host(&pdev->dev, 0);

    if (master == NULL) {
        dev_err(&pdev->dev, "Cannot allocate SPI device");
        return -ENOMEM;
    }

    master->bus_num = 0;
    master->num_chipselect = 1;
    master->transfer_one = spi_mock_transfer_one;

    err = spi_register_controller(master);

    if (err) {
        dev_err(&pdev->dev, "Cannot register SPI controller");
        return err;
    }

    spi_dev = spi_new_device(master, &chip);

    if (!spi_dev) {
        dev_err(&pdev->dev, "Cannot create new SPI device");
        return -ENOMEM;
    }

    internal_buf = (char *)kmalloc(1024 * sizeof(char), GFP_KERNEL);

    dev_info(&pdev->dev, "Created new SPI Mock bus");

    return 0;
}

static void spi_mock_remove(struct platform_device *pdev)
{
    dev_info(&pdev->dev, "Removing SPI bus");
    kfree(internal_buf);
    spi_unregister_controller(master);

}

static struct platform_device * spi_mock_device;


static struct platform_driver spi_mock_driver = {
    .driver = {
        .name   = MODULE_NAME
    },
    .probe      = spi_mock_probe,
    .remove     = spi_mock_remove,
};

static int __init spi_mock_init(void)
{
    int err = 0;

    err = platform_driver_register(&spi_mock_driver);
    if (err) {
        pr_err("spi-mock: could not register driver");
        return err;
    }

    spi_mock_device =  platform_device_register_simple(MODULE_NAME, PLATFORM_DEVID_NONE, NULL, 0);
    if (IS_ERR(spi_mock_device)) {
        pr_err("spi-mock: error registring device");
        platform_driver_unregister(&spi_mock_driver);
        if (spi_mock_device) {
            platform_device_unregister(spi_mock_device);
        }
        return PTR_ERR(spi_mock_device);
    }

    return 0;
}

static void __exit spi_mock_exit(void) {

    dev_info(&spi_dev->dev, "Removing SPI Mock kernel driver");

    platform_driver_unregister(&spi_mock_driver);
    platform_device_unregister(spi_mock_device);
}


module_init(spi_mock_init);
module_exit(spi_mock_exit);

MODULE_LICENSE("GPL");
