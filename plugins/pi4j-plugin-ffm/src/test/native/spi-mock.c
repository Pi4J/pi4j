    #include <linux/init.h>
    #include <linux/printk.h>
    #include <linux/kernel.h>
    #include <linux/module.h>
    #include <linux/slab.h>
    #include <linux/errno.h>
    #include <linux/uaccess.h>
    #include <linux/spi/spi.h>
    
    #include <linux/platform_device.h>
    
    static struct spi_master *master;
    static struct spi_device *spi_dev;
    
    static int myspi_transfer_one(struct spi_controller *ctlr, struct spi_device *spi,
                    struct spi_transfer *transfer)
    {
        if (transfer->rx_buf && transfer->tx_buf) {
            // if we send & receive in the same transfer, simply copy tx buffer to rx buffer (echo)
            unsigned char * rx_buf = transfer->rx_buf;
            const unsigned char * tx_buf = transfer->tx_buf;
            int i;
    
            for (i = 0; i < transfer->len; i++)
                rx_buf[i] = tx_buf[i];
        } else if (transfer->rx_buf) {
            // if we are just reading, fill the buffer with '1'
            unsigned char * rx_buf = transfer->rx_buf;
            int i;
            for (i = 0; i < transfer->len; i++)
                rx_buf[i] = 1;
        }
        // if are writing, silently accept bytes


        spi_finalize_current_transfer(ctlr);
    
        return 0;
    }
    
    struct spi_board_info chip = {
        .modalias = "spi-mock",
    };
    
    static int plat_probe(struct platform_device *pdev)
    {
        int err = 0;
    
        pr_info("%s()\n", __func__);
    
        master = spi_alloc_master(&pdev->dev, 0);
    
        if (master == NULL) {
            pr_err("spi_alloc_master failed\n");
            return -ENOMEM;
        }
    
        master->num_chipselect = 1;
    
        master->transfer_one = myspi_transfer_one;
    
        err = spi_register_master(master);
    
        if (err) {
            pr_err("spi_register_master failed\n");
            spi_master_put(master);
            return err;
        }
    
        spi_dev = spi_new_device(master, &chip);
    
        if (!spi_dev)
            /* TODO do we need to do anything else? */
            err = -ENOMEM;
    
        if (err)
            pr_err("spi driver error\n");
        else
            pr_info("spi driver ok\n");
    
        return err;
    }
    
    static int plat_remove(struct platform_device *pdev)
    {
        pr_info("%s()\n", __func__);
    
        spi_unregister_master(master);
    
        return 0;
    }
    
    static struct platform_device * plat_device;
    
    
    static struct platform_driver plat_driver = {
        .driver = {
            .name   = "spi-mock",
            .owner  = THIS_MODULE,
        },
        .probe      = plat_probe,
        .remove     = plat_remove,
    };
    
    static int __init myspi_init(void)
    {
        int err = -ENODEV;
    
        plat_device =  platform_device_register_simple("spi-mock",
            PLATFORM_DEVID_NONE, NULL, 0);
    
        if (IS_ERR(plat_device))
            return PTR_ERR(plat_device);
    
        err =  platform_driver_register(&plat_driver);
    
        if (err)
            return err;
    
        if (!err)
            pr_info("spi driver loaded\n");
    
        return err;
    }
    
    static void __exit myspi_exit(void) {
    
        platform_driver_unregister(&plat_driver);
        platform_device_unregister(plat_device);
    
        pr_info("spi driver unloaded\n");
    }
    
    
    module_init(myspi_init);
    module_exit(myspi_exit);
    
    MODULE_LICENSE("GPL");
