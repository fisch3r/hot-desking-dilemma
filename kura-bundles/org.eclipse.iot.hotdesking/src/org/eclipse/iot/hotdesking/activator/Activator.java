package org.eclipse.iot.hotdesking.activator;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.eclipse.iot.hotdesking.actions.DeskStateChangedListener;
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

    private GpioPinDigitalMultipurpose occupancyLed;

    private GpioPinDigitalMultipurpose motionStatusLed;

    private volatile boolean motionTriggered = false;

    private ScheduledThreadPoolExecutor threadPoolExecutor;

    private ScheduledFuture<?> handle;

    private Logger logger = LoggerFactory.getLogger(Activator.class);

    private List<DeskStateChangedListener> listeners = new CopyOnWriteArrayList<DeskStateChangedListener>();

    private static final String APP_ID = "org.eclipse.iot.hotdesking";
    
    public void activate(ComponentContext componentContext) {
        logger.info("Bundle {} is starting...", APP_ID);

        gpioController = GpioFactory.getInstance();

        initializeMotionSensor();
        initializeLEDs();

        motionSensor.addTrigger(new GpioSyncStateTrigger(motionStatusLed));

        threadPoolExecutor = new ScheduledThreadPoolExecutor(1);

        handle = threadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                logger.info("checking for motion triggered...");
                if (motionTriggered) {
                    occupancyLed.setState(PinState.HIGH);
                    notifyListeners(true);
                } else {
                    occupancyLed.setState(PinState.LOW);
                    notifyListeners(false);
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

    public void deactivate(ComponentContext componentContext) {
        logger.info("Bundle {} is stopping...", APP_ID);

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

        logger.info("Bundle {} has stopped!", APP_ID);
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
    
    public void addDeskStateChangedListener(DeskStateChangedListener listener) {
        listeners.add(listener);
    }

    public void removeDeskStateChangedListener(DeskStateChangedListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(boolean occupied) {
        for (DeskStateChangedListener listener : listeners) {
            listener.stateChanged(occupied);
        }
    }

}
