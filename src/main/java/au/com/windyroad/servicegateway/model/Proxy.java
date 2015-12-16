package au.com.windyroad.servicegateway.model;

import org.springframework.core.ParameterizedTypeReference;

import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.server.annotations.HateoasController;

@HateoasController(ProxyController.class)
public class Proxy {

    private String target;
    private String name;

    public static ParameterizedTypeReference<EntityWrapper<Proxy>> wrapperType() {
        return new ParameterizedTypeReference<EntityWrapper<Proxy>>() {
        };
    }

    protected Proxy() {
    }

    public Proxy(String name, String target) {
        this.name = name;
        this.target = target;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
