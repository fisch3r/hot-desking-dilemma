package org.eclipse.iot.hotdesking.publisher;

import org.eclipse.iot.hotdesking.actions.DeskStateChangedListener;
import org.eclipse.kura.KuraException;
import org.eclipse.kura.KuraStoreException;
import org.eclipse.kura.data.DataService;
import org.eclipse.kura.data.DataServiceListener;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotDeskingDilemmaPublisher implements DeskStateChangedListener, DataServiceListener {
    
private static final String TOPIC = "hot-desks/table1";

private Logger logger = LoggerFactory.getLogger(HotDeskingDilemmaPublisher.class);
    
    private DataService dataService;
    
    protected void activate(ComponentContext componentContext) {
        logger.debug("activating hot desking dilemma publisher module");
    }

    protected void deactivate(ComponentContext componentContext) {
        logger.debug("Deactivating hot desking dilemma publisher module");
    }
    
    protected void setDataService(DataService dataService) {
        this.dataService = dataService;
    }
    
    protected void unsetDataService(DataService dataService) {
        this.dataService = null;
    }

    @Override
    public void stateChanged(boolean occupied) {
        if (dataService == null) {
            logger.warn("failed to publish message, data service is not ready");
            return;
        }
        String text = "table is " + (occupied ? "occupied" : "free");
        try {
            dataService.publish(TOPIC, text.getBytes(), 1, false, 1);
        } catch (KuraStoreException e) {
            logger.error("failed to publish message", e);
        }
    }

    @Override
    public void onConnectionEstablished() {
        logger.debug("connection has been established");
        try {
            this.dataService.subscribe(TOPIC, 1);
            logger.debug("subscribed to mqtt/demo");
        } catch (KuraException e) {
            logger.error("failed to subscribe", e);
        }
    }

    @Override
    public void onDisconnecting() {
        logger.debug("disconnecting...");
    }

    @Override
    public void onDisconnected() {
        logger.debug("application is disconnected");
    }

    @Override
    public void onConnectionLost(Throwable cause) {
        logger.debug("connection has been lost", cause);
    }

    @Override
    public void onMessageArrived(String topic, byte[] payload, int qos, boolean retained) {
        logger.debug("message arrived on topic {}, payload was: {}", topic, String.valueOf(payload));
    }

    @Override
    public void onMessagePublished(int messageId, String topic) {
        logger.debug("message has been published with it {} to topic {}", messageId, topic);
    }

    @Override
    public void onMessageConfirmed(int messageId, String topic) {
        logger.debug("message has been confirmed with id {} for topic {}", messageId, topic);
    }

}
