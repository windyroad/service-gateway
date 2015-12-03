package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.LinkedEntity;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.hateoas.server.annotations.HateoasChildren;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.controller.AdminProxyController;

@HateoasController(AdminProxyController.class)
public class Proxy {

    private Map<String, Entity> endpoints = new HashMap<>();
    private String target;
    private String name;

    Proxies parent;

    protected Proxy() {
    }

    public Proxy(Proxies parent, String name, String target) {
        this.parent = parent;
        this.name = name;
        this.target = target;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Autowired
    ApplicationContext context;

    @HateoasAction(nature = HttpMethod.PUT, controller = AdminProxyController.class)
    public void setEndpoint(String target, String available)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        Entity endpoint = getEndpoint(target);

        if (endpoint == null) {
            endpoint = new EndpointEntity(
                    new Endpoint(getName(), target,
                            Boolean.parseBoolean(available)),
                    getName(), target);
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
            bpp.processInjection(endpoint);

            endpoints.put(target, endpoint);
        } else {
            endpoint.resolve(EndpointEntity.class).getProperties()
                    .setAvailable(Boolean.parseBoolean(available));
        }
    }

    public Entity getEndpoint(String target) {
        return endpoints.get(target);
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

    @HateoasAction(nature = HttpMethod.PUT, controller = AdminProxyController.class)
    public Proxy update(String endpoint) {
        this.setTarget(endpoint);

        return this;
    }

    @HateoasChildren(Relationship.ITEM)
    @JsonIgnore
    public Collection<Entity> getEndpoints() {
        return endpoints.values();
    }

    @HateoasChildren(Relationship.ITEM)
    public void setEndpoints(Collection<LinkedEntity> endpoints) {
        for (LinkedEntity endpoint : endpoints) {
            URI address = endpoint.getAddress();
            String[] pathElements = address.getPath().split("/");
            this.endpoints.put(pathElements[pathElements.length - 1], endpoint);
        }
    }

    @HateoasAction(nature = HttpMethod.DELETE, controller = AdminProxyController.class)
    public void deleteProxy() {
        parent.deleteProxy(name);
    }

}
