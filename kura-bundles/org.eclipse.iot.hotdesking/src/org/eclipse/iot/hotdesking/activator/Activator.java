package org.eclipse.iot.hotdesking.activator;

import org.eclipse.iot.hotdesking.consumer.DataServiceTracker;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Activator {

    private Logger logger = LoggerFactory.getLogger(Activator.class);

    private DataServiceTracker serviceTracker;

    private static final String APP_ID = "org.eclipse.iot.hotdesking";

    protected void activate(ComponentContext componentContext) {
        logger.info("Bundle {} is starting...", APP_ID);

        serviceTracker = new DataServiceTracker(componentContext.getBundleContext());
        serviceTracker.open();

        logger.info("Bundle {} has started!", APP_ID);
    }

    protected void deactivate(ComponentContext componentContext) {
        logger.info("Bundle {} is stopping...", APP_ID);

        serviceTracker.close();

        logger.info("Bundle {} has stopped!", APP_ID);
    }

}
