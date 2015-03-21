package org.eclipse.iot.hotdesking.consumer;

import org.eclipse.kura.data.DataService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class DataServiceTracker extends ServiceTracker<DataService, Object> {
    
    private BundleContext context;
    
    private HotDeskingDilemmaConsumer consumer;
    
    public DataServiceTracker(BundleContext context) {
        super(context, DataService.class.getName(), null);
        this.context = context;
    }

    @Override
    public Object addingService(ServiceReference<DataService> serviceReference) {
        consumer = new HotDeskingDilemmaConsumer(context.getService(serviceReference));
        return null;
    }

    @Override
    public void modifiedService(ServiceReference<DataService> serviceReference, Object object) {
        removedService(serviceReference, object);
        addingService(serviceReference);
    }

    @Override
    public void removedService(ServiceReference<DataService> serviceReference, Object object) {
        consumer.stopConsuming();
    }
    
    @Override
    public void close() {
        if (consumer != null) {
            consumer.stopConsuming();
        }
    }

}
