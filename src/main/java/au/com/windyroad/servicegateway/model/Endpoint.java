package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Properties;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.annotations.Label;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.controller.AdminEndpointController;

@HateoasController(AdminEndpointController.class)
@Label("Endpoint `{proxyName}/{target}`")
public class Endpoint extends Properties {

    protected Endpoint() {
    }

    public Endpoint(String proxyName, String target, boolean available)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        this.setProperty("target", target);
        this.setProperty("available", Boolean.toString(available));
    }

    public void setAvailable(boolean available) {
        this.setProperty("available", Boolean.toString(available));
    }

    @JsonIgnore
    public boolean isAvailable() {
        return Boolean.parseBoolean(this.getProperty("available"));
    }

    public String getTarget() {
        return this.getProperty("target");
    }

}
