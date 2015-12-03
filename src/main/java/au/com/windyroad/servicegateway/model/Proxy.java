package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.LinkedEntity;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.hateoas.server.annotations.HateoasChildren;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.Repository;
import au.com.windyroad.servicegateway.controller.AdminProxyController;

@HateoasController(AdminProxyController.class)
public class Proxy {

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    private String target;
    private String name;

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

    @Autowired
    ApplicationContext context;

    @HateoasAction(nature = HttpMethod.PUT, controller = AdminProxyController.class)
    public void setEndpoint(String target, String available)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        EndpointEntity endpoint = repository
                .getEndpoint(getTarget() + "/" + target);

        if (endpoint == null) {
            endpoint = new EndpointEntity(
                    new Endpoint(getName(), target,
                            Boolean.parseBoolean(available)),
                    getName(), target);
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
            bpp.processInjection(endpoint);

            repository.store(getTarget() + "/" + target, endpoint);
        } else {
            endpoint.getProperties()
                    .setAvailable(Boolean.parseBoolean(available));
            repository.store(getTarget() + "/" + target, endpoint);
        }
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
    public Collection<EndpointEntity> getEndpoints() {
        return repository.getEndpoints();
    }

    @HateoasChildren(Relationship.ITEM)
    public void setEndpoints(Collection<LinkedEntity> endpoints) {
        // hmmm.., this is called during deserialisation.
        // clients don't have access to the repository, so
        // this will fail.
        // need to think about how to handle this
        // client side classes need to deserialize very differently.
        // for (LinkedEntity endpoint : endpoints) {
        // URI address = endpoint.getAddress();
        // String[] pathElements = address.getPath().split("/");
        // this.endpoints.put(pathElements[pathElements.length - 1], endpoint);
        // }
    }

    @HateoasAction(nature = HttpMethod.DELETE, controller = AdminProxyController.class)
    public void deleteProxy() {
        repository.deleteProxy(getName());
    }

    public Entity getEndpoint(String endpointName) {
        return repository.getEndpoint(getTarget() + "/" + endpointName);
    }

}
