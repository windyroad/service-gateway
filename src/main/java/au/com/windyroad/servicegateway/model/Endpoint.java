package au.com.windyroad.servicegateway.model;

import java.io.UnsupportedEncodingException;

import au.com.windyroad.hateoas.annotations.Label;
import au.com.windyroad.hateoas.server.annotations.HateoasController;

@HateoasController(EndpointController.class)
@Label("Endpoint `{target}`")
public class Endpoint {

    private boolean available;
    private String target;

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

    public static String buildUrl(String target)
            throws UnsupportedEncodingException {
        return "/admin/endpoints/"
                + target.replaceFirst("://", "/").replaceFirst(":", "/");
    }
}
