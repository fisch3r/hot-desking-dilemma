package org.eclipse.iot.hotdesking.activator;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;

public class Activator {

    private GpioController gpioController;

    private GpioPinDigitalMultipurpose motionSensor;

    private volatile boolean motionTriggered = false;

    private ScheduledThreadPoolExecutor threadPoolExecutor;

    private ScheduledFuture<?> handle;

    private Logger logger = LoggerFactory.getLogger(Activator.class);

    private static final String APP_ID = "org.eclipse.iot.hotdesking";

    protected void activate(ComponentContext componentContext) {
        logger.info("Bundle {} tries to start...", APP_ID);
        gpioController = GpioFactory.getInstance();

        motionSensor = gpioController.provisionDigitalMultipurposePin(RaspiPin.GPIO_01, PinMode.DIGITAL_INPUT);
        motionSensor.setShutdownOptions(true);

        GpioPinDigitalMultipurpose led = gpioController.provisionDigitalMultipurposePin(RaspiPin.GPIO_04, PinMode.DIGITAL_OUTPUT);

        motionSensor.addTrigger(new GpioSyncStateTrigger(led));

        threadPoolExecutor = new ScheduledThreadPoolExecutor(1);

        handle = threadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (motionTriggered) {
                    logger.info("table is occupied for now");
                } else {
                    logger.info("table is free");
                }
                setMotionTriggeredState(false);
            }
        }, 0, 30, TimeUnit.SECONDS);

        motionSensor.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                if (event.getState() == PinState.HIGH) {
                    setMotionTriggeredState(true);
                }
            }
        });

        logger.info("Bundle {} has started!", APP_ID);
    }

    private void setMotionTriggeredState(boolean newState) {
        motionTriggered = newState;
    }

    protected void deactivate(ComponentContext componentContext) {
        if (gpioController != null) {
            gpioController.unexportAll();
            gpioController.shutdown();
        }
        if (handle != null) {
            handle.cancel(true);
        }

        logger.info("Bundle {} has stopped!", APP_ID);
    }

}
