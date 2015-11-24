package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.annotations.Rel;
import au.com.windyroad.hateoas2.HateoasController;
import au.com.windyroad.hateoas2.JavaLink;
import au.com.windyroad.hateoas2.NavigationalRelationship;
import au.com.windyroad.hateoas2.ResolvedEntity;
import au.com.windyroad.servicegateway.controller.AdminEndpointController;

@HateoasController(AdminEndpointController.class)
public class Endpoint extends ResolvedEntity {

    protected Endpoint() {
    }

    public Endpoint(String proxyName, String target, boolean available)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        super.add(new NavigationalRelationship(
                new JavaLink(this, proxyName, target), Rel.SELF));

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
