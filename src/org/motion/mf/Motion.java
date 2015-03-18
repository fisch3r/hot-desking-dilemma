package org.motion.mf;

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

public class Motion {

	private GpioController gpioController;

	private GpioPinDigitalMultipurpose motionSensor;

	private static final Logger s_logger = LoggerFactory
			.getLogger(Motion.class);

	private static final String APP_ID = "com.exxeta.motion.hello_world";

	protected void activate(ComponentContext componentContext) {
		s_logger.info("Bundle " + APP_ID + " tries to start...");
		gpioController = GpioFactory.getInstance();

		motionSensor = gpioController.provisionDigitalMultipurposePin(
				RaspiPin.GPIO_01, PinMode.DIGITAL_INPUT);
		motionSensor.setShutdownOptions(true);

		motionSensor.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(
					GpioPinDigitalStateChangeEvent event) {
				s_logger.info("Pin state: {}", event.getState());
			}
		});

		s_logger.info("Bundle " + APP_ID + " has started!");
		s_logger.debug(APP_ID + ": This is a debug message.");
	}

	protected void deactivate(ComponentContext componentContext) {
		if (gpioController != null) {
			gpioController.unexportAll();
			gpioController.shutdown();
		}

		s_logger.info("Bundle " + APP_ID + " has stopped!");
	}

}
