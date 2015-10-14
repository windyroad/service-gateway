package au.com.windyroad.servicegateway.model;

import au.com.windyroad.hateoas.Entity;

public class Endpoint extends Entity<Endpoint.Properties> {

    public Endpoint(String target, boolean available) {
        this.setProperties(new Properties(target, available));
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
