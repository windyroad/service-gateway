package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.LinkedEntity;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.hateoas.server.annotations.HateoasChildren;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.controller.AdminProxyController;

@HateoasController(AdminProxyController.class)
public class Proxy extends Properties {

    private static final String TARGET = "target";
    private static final String NAME = "name";

    private Map<String, Entity> endpoints = new HashMap<>();

    protected Proxy() {
    }

    public Proxy(String name, String target) throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        setProperty(NAME, name);
        setProperty(TARGET, target);
    }

    public String getTarget() {
        return this.getProperty(TARGET);
    }

    public void setTarget(String target) {
        this.setProperty(TARGET, target);
    }

    @Autowired
    ApplicationContext context;

    public void setEndpoint(Entity entity, String target, boolean available)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        Entity endpoint = getEndpoint(target);

        if (endpoint == null) {
            endpoint = new ResolvedEntity<Endpoint>(
                    new Endpoint(getName(), target, available), getName(),
                    target);
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
            bpp.processInjection(endpoint);

            endpoints.put(target, endpoint);
        } else {
            // TODO we can't assume that the endpoint is local
            // we should invoke an action to update it's state
            ParameterizedTypeReference<ResolvedEntity<Endpoint>> type = new ParameterizedTypeReference<ResolvedEntity<Endpoint>>() {
            };
            endpoint.resolve(type).getProperties().setAvailable(available);
        }
    }

    public Entity getEndpoint(String target) {
        return endpoints.get(target);
    }

    /**
     * @return the name
     */
    public String getName() {
        return getProperty("name");
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        setProperty("name", name);
    }

    @HateoasAction(nature = HttpMethod.PUT, controller = AdminProxyController.class)
    public Proxy update(ResolvedEntity<Proxies> entity,
            @RequestParam("endpoint") String targetEndPoint) {
        this.setTarget(targetEndPoint);

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

}
