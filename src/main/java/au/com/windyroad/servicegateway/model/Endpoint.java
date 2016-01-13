package au.com.windyroad.servicegateway.model;

import java.util.Map;

import au.com.windyroad.hateoas.annotations.Label;

@Label("Endpoint `{target}`")
public class Endpoint {

    private boolean available;
    private String target;

    protected Endpoint() {
    }

    public Endpoint(Map<String, String> properties) {
        this.target = properties.get("target");
        this.available = Boolean.parseBoolean(properties.get("available"));
    }

    public Endpoint(String target, boolean available) {
        this.target = target;
        this.available = available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return this.available;
    }

    public String getTarget() {
        return this.target;
    }

    public static String buildPath(String target) {
        return "/admin/endpoints/"
                + target.replaceFirst("://", "/").replaceFirst(":", "/");
    }
}
