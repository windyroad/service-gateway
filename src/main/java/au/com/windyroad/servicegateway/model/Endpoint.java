package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import au.com.windyroad.hateoas.annotations.Label;
import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.controller.AdminEndpointController;

@HateoasController(AdminEndpointController.class)
@Label("Endpoint `{proxyName}/{target}`")
public class Endpoint extends ResolvedEntity<EndpointProperties> {

    protected Endpoint() {
        super(new EndpointProperties());
    }

    public Endpoint(String proxyName, String target, boolean available)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        super(new EndpointProperties(), proxyName, target);

        this.getProperties().setProperty("target", target);
        this.getProperties().setProperty("available",
                Boolean.toString(available));
    }
}
