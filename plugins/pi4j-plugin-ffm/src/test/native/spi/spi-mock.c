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
#include <linux/string.h>
#include <linux/errno.h>
#include <linux/uaccess.h>
#include <linux/spi/spi.h>

#include <linux/platform_device.h>

#define MODULE_NAME "spi-mock"
#define BUFFER_SIZE 1024

/*
* SPI Mock driver provides basic functionality to integration test of Pi4j project and regression.
* It simulates real SPI Bus by echoing all incoming transfers.
* Behaviour:
* - full duplex transfer (tx + rx): the tx buffer is echoed back into rx and remembered
* - write only transfer (tx): the tx buffer is stored
* - read only transfer (rx): the previously stored buffer is returned (zero padded if shorter)
* Limitations:
* - the stored buffer is limited to BUFFER_SIZE bytes
*/

static struct spi_controller *master;
static struct spi_device *spi_dev;

// stored data of the last transfer and the number of valid bytes in it
static unsigned char *internal_buf;
static unsigned int internal_len;

static int spi_mock_transfer_one(struct spi_controller *ctlr, struct spi_device *spi,
                struct spi_transfer *transfer)
{
    // how many bytes we can keep in our fixed size buffer
    unsigned int store_len = min_t(unsigned int, transfer->len, BUFFER_SIZE);

    if (transfer->tx_buf && transfer->rx_buf) {
        // full duplex: echo tx straight back into rx and remember it
        memcpy(transfer->rx_buf, transfer->tx_buf, transfer->len);
        memcpy(internal_buf, transfer->tx_buf, store_len);
        internal_len = store_len;
        dev_info(&spi->dev, "Transfer tx and rx (echo): %*ph", store_len, internal_buf);
    } else if (transfer->tx_buf) {
        // write only: store the incoming buffer
        memcpy(internal_buf, transfer->tx_buf, store_len);
        internal_len = store_len;
        dev_info(&spi->dev, "Writing, tx_buf: %*ph", store_len, internal_buf);
    } else if (transfer->rx_buf) {
        // read only: return what we stored, zero pad anything beyond it
        unsigned int copy_len = min_t(unsigned int, transfer->len, internal_len);

        memcpy(transfer->rx_buf, internal_buf, copy_len);
        if (transfer->len > copy_len)
            memset((unsigned char *)transfer->rx_buf + copy_len, 0, transfer->len - copy_len);
        dev_info(&spi->dev, "Reading, rx_buf: %*ph", copy_len, (unsigned char *)transfer->rx_buf);
    }

    // the transfer completed synchronously, so report it as finished (return 0).
    // spi_finalize_current_transfer() must NOT be called here - that is only for
    // drivers that return 1 and complete the transfer asynchronously.
    return 0;
}

static struct spi_board_info chip = {
    // dirty hack to make spidev autloaded and binded to this driver
    .modalias = "bk4",
    .bus_num = 0,
    .chip_select = 0,
};

static int spi_mock_probe(struct platform_device *pdev)
{
    int err = 0;

    // allocated through devm, so it is unregistered and freed automatically on unbind
    master = devm_spi_alloc_host(&pdev->dev, 0);
    if (master == NULL) {
        dev_err(&pdev->dev, "Cannot allocate SPI host");
        return -ENOMEM;
    }

    master->bus_num = 0;
    master->num_chipselect = 1;
    // support all four SPI modes and both bit orders that pi4j may request
    master->mode_bits = SPI_CPHA | SPI_CPOL | SPI_LSB_FIRST;
    master->transfer_one = spi_mock_transfer_one;

    internal_buf = devm_kzalloc(&pdev->dev, BUFFER_SIZE, GFP_KERNEL);
    if (!internal_buf)
        return -ENOMEM;
    internal_len = 0;

    err = devm_spi_register_controller(&pdev->dev, master);
    if (err) {
        dev_err(&pdev->dev, "Cannot register SPI controller");
        return err;
    }

    spi_dev = spi_new_device(master, &chip);
    if (!spi_dev) {
        dev_err(&pdev->dev, "Cannot create new SPI device");
        return -ENOMEM;
    }

    dev_info(&pdev->dev, "Created new SPI Mock bus");

    return 0;
}

static struct platform_device * spi_mock_device;


static struct platform_driver spi_mock_driver = {
    .driver = {
        .name   = MODULE_NAME
    },
    .probe      = spi_mock_probe,
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
        return PTR_ERR(spi_mock_device);
    }

    return 0;
}

static void __exit spi_mock_exit(void) {

    pr_info("spi-mock: Removing SPI Mock kernel driver");

    // unregistering the device triggers devm cleanup of the controller (and its children)
    platform_device_unregister(spi_mock_device);
    platform_driver_unregister(&spi_mock_driver);
}


module_init(spi_mock_init);
module_exit(spi_mock_exit);

MODULE_LICENSE("GPL");
MODULE_DESCRIPTION("SPI Mock Device");
MODULE_AUTHOR("Nick Gritsenko");
