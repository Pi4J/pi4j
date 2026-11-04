package com.pi4j.test.smoketest;

import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalInput;
import com.pi4j.io.gpio.digital.DigitalOutput;
import com.pi4j.io.gpio.digital.DigitalState;
import com.pi4j.io.gpio.digital.PullResistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.simple.SimpleLogger;

import java.util.Scanner;

public abstract class TestCase {

    protected static final int ID_VALUE_MSK_BMP = 0x58;   // expected chpId value BMP28
    protected static final int ID_VALUE_MSK_BME = 0x60;   // expected chpId value BME280

    public abstract TestResult run(ProviderContext providerContext);

    public static void run(Class<? extends TestCase> testCaseClass, String[] args) {
        System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");
        System.setProperty(SimpleLogger.SHOW_DATE_TIME_KEY, "true");
        Logger logger = LoggerFactory.getLogger(testCaseClass);
        String help = "params: -p <newautocontext,ffm>, -h help";
        ProviderContext.TestProvider provider = ProviderContext.TestProvider.NEWAUTOCONTEXT;
        boolean showHelp = false;

        for (int i = 0; i < args.length; i++) {
            if ("-h".equals(args[i])) {
                showHelp = true;
            } else if ("-p".equals(args[i]) && i + 1 < args.length) {
                String name = args[++i];
                if ("newautocontext".equalsIgnoreCase(name)) {
                    provider = ProviderContext.TestProvider.NEWAUTOCONTEXT;
                } else if ("ffm".equalsIgnoreCase(name)) {
                    provider = ProviderContext.TestProvider.FFM;
                } else {
                    logger.error("Invalid provider: {}. {}", name, help);
                    System.exit(1);
                    return;
                }
            } else {
                logger.error("Invalid argument: {}. {}", args[i], help);
                System.exit(1);
                return;
            }
        }
        if (showHelp) {
            logger.info(help);
            return;
        }

        ProviderContext providerContext = null;
        boolean success = false;
        try {
            providerContext = new ProviderContext(provider);
            TestCase testCase = testCaseClass.getDeclaredConstructor().newInstance();
            TestResult result = testCase.run(providerContext);
            result.log(logger);
            success = result.success();
        } catch (Exception | LinkageError e) {
            logger.error("Unable to run test", e);
        } finally {
            if (providerContext != null) {
                try {
                    providerContext.getContext().shutdown();
                } catch (Exception e) {
                    logger.error("Unable to shut down context", e);
                    success = false;
                }
            }
        }
        if (!success) {
            System.exit(1);
        }
    }

    protected static DigitalInput createDigitalInput(Context pi4j, int bcm, PullResistance pull, long debounceTime) {
        var inputConfig3 = DigitalInput.newConfigBuilder(pi4j)
            .bcm(bcm)
            .pull(pull)
            .debounce(debounceTime);
        return pi4j.create(inputConfig3);
    }

    protected static DigitalOutput createDigitalOutput(Context pi4j, int bcm, DigitalState initial, DigitalState shutDown) {
        var outputConfig3 = DigitalOutput.newConfigBuilder()
            .bcm(bcm)
            .initial(initial)
            .shutdown(shutDown);
        return pi4j.create(outputConfig3);
    }

    protected static int waitForInput(Logger logger, String msg) {
        int rval = 0;
        Scanner scan = new Scanner(System.in);

        logger.info(msg);
        rval = Integer.parseInt(scan.next());

        return (rval);
    }
}
