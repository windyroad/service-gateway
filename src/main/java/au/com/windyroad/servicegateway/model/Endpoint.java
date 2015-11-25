package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.annotations.Title;
import au.com.windyroad.hateoas2.HateoasController;
import au.com.windyroad.hateoas2.ResolvedEntity;
import au.com.windyroad.servicegateway.controller.AdminEndpointController;

@HateoasController(AdminEndpointController.class)
@Title("Endpoint `{proxyName}/{target}`")
public class Endpoint extends ResolvedEntity {

    protected Endpoint() {
    }

    public Endpoint(String proxyName, String target, boolean available)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        super(proxyName, target);

        this.getProperties().setProperty("target", target);
        this.getProperties().setProperty("available",
                Boolean.toString(available));
    }

    public void setAvailable(boolean available) {
        this.getProperties().setProperty("available",
                Boolean.toString(available));
    }

    @JsonIgnore
    public boolean isAvailable() {
        return Boolean
                .parseBoolean(this.getProperties().getProperty("available"));
    }
}
