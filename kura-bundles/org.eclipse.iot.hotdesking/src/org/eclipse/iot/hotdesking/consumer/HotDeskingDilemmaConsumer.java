package org.eclipse.iot.hotdesking.consumer;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;

public class HotDeskingDilemmaConsumer {

    private GpioController gpioController;

    private GpioPinDigitalMultipurpose motionSensor;

    private GpioPinDigitalMultipurpose occupancyLed;

    private GpioPinDigitalMultipurpose motionStatusLed;

    private volatile boolean motionTriggered = false;

    private ScheduledThreadPoolExecutor threadPoolExecutor;

    private ScheduledFuture<?> handle;

    public HotDeskingDilemmaConsumer() {

        gpioController = GpioFactory.getInstance();

        initializeMotionSensor();
        initializeLEDs();

        motionSensor.addTrigger(new GpioSyncStateTrigger(motionStatusLed));

        threadPoolExecutor = new ScheduledThreadPoolExecutor(1);

        handle = threadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (motionTriggered) {
                    occupancyLed.setState(PinState.HIGH);
                } else {
                    occupancyLed.setState(PinState.LOW);
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
    }

    public void stopConsuming() {
        if (gpioController != null) {
            gpioController.unexportAll();
            gpioController.shutdown();
        }
        if (handle != null) {
            handle.cancel(true);
        }
        if (motionSensor != null) {
            motionSensor.removeAllListeners();
        }
    }

    private void initializeLEDs() {
        occupancyLed = gpioController.provisionDigitalMultipurposePin(RaspiPin.GPIO_04, PinMode.DIGITAL_OUTPUT);
        motionStatusLed = gpioController.provisionDigitalMultipurposePin(RaspiPin.GPIO_05, PinMode.DIGITAL_OUTPUT);
    }

    private void initializeMotionSensor() {
        motionSensor = gpioController.provisionDigitalMultipurposePin(RaspiPin.GPIO_01, PinMode.DIGITAL_INPUT);
        motionSensor.setShutdownOptions(true);
    }

    private void setMotionTriggeredState(boolean newState) {
        motionTriggered = newState;
    }
}
