package au.com.windyroad.servicegateway.model;

import au.com.windyroad.hateoas.annotations.Label;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.controller.AdminEndpointController;

@HateoasController(AdminEndpointController.class)
@Label("Endpoint `{proxyName}/{target}`")
public class Endpoint {

    private boolean available;
    private String target;

    protected Endpoint() {
    }

    public Endpoint(String proxyName, String target, boolean available) {
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

}
