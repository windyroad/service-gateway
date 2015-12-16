package au.com.windyroad.servicegateway.model;

import org.springframework.core.ParameterizedTypeReference;

import au.com.windyroad.hateoas.annotations.Label;
import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.server.annotations.HateoasController;

@HateoasController(EndpointController.class)
@Label("Endpoint `{target}`")
public class Endpoint {

    private boolean available;
    private String target;

    public static ParameterizedTypeReference<EntityWrapper<Endpoint>> wrapperType() {
        return new ParameterizedTypeReference<EntityWrapper<Endpoint>>() {
        };
    }

    protected Endpoint() {
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
