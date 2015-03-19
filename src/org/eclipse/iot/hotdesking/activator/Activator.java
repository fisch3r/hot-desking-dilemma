package org.eclipse.iot.hotdesking.activator;

import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Activator {

    private GpioController gpioController;

    private GpioPinDigitalMultipurpose motionSensor;

    private Logger logger = LoggerFactory.getLogger(Activator.class);

    private static final String APP_ID = "com.exxeta.motion.hello_world";

    protected void activate(ComponentContext componentContext) {
        logger.info("Bundle " + APP_ID + " tries to start...");
        gpioController = GpioFactory.getInstance();

        motionSensor = gpioController.provisionDigitalMultipurposePin(RaspiPin.GPIO_01, PinMode.DIGITAL_INPUT);
        motionSensor.setShutdownOptions(true);

        motionSensor.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                logger.info("Pin state: {}", event.getState());
            }
        });

        logger.info("Bundle " + APP_ID + " has started!");
        logger.debug(APP_ID + ": This is a debug message.");
    }

    protected void deactivate(ComponentContext componentContext) {
        if (gpioController != null) {
            gpioController.unexportAll();
            gpioController.shutdown();
        }

        logger.info("Bundle " + APP_ID + " has stopped!");
    }

}
