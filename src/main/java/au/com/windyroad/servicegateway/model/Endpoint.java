package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.springframework.hateoas.core.DummyInvocationUtils;

import au.com.windyroad.hateoas.Entity;
import au.com.windyroad.hateoas.JavaLink;
import au.com.windyroad.servicegateway.controller.AdminEndpointController;

public class Endpoint extends Entity<Endpoint.Properties> {

    protected Endpoint() {
    }

    public Endpoint(String proxyName, String target, boolean available)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        this.setProperties(new Properties(target, available));
        this.addLink(
                new JavaLink(this,
                        DummyInvocationUtils
                                .methodOn(AdminEndpointController.class)
                                .self(proxyName, target)));
    }

    public static class Properties {
        private boolean available;
        private String target;

        @SuppressWarnings("unused")
        private Properties() {
        }

        public Properties(String target, boolean available) {
            this.target = target;
            this.available = available;
        }

        public boolean getAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        /**
         * @return the target
         */
        public String getTarget() {
            return target;
        }

        /**
         * @param target
         *            the target to set
         */
        public void setTarget(String target) {
            this.target = target;
        }
    }

    public void setAvailable(boolean available) {
        this.getProperties().available = available;
    }
}
