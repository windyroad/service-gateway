package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.EntityRelationship;
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

    private Set<EntityRelationship<Endpoint>> endpoints = new HashSet<>();

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

    public void setEndpoint(ResolvedEntity<Proxy> entity, String target,
            boolean available) throws NoSuchMethodException, SecurityException,
                    IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException, URISyntaxException {
        Optional<EntityRelationship<Endpoint>> relatedEntity = getEndpoints()
                .stream().filter(e -> e.hasNature(Relationship.ITEM))
                .filter(e -> ((ResolvedEntity<Endpoint>) (e.getEntity()))
                        .getProperties().getProperty("target") != null
                        && ((ResolvedEntity<Endpoint>) (e.getEntity()))
                                .getProperties().getProperty("target")
                                .equals(target))
                .findAny();
        if (!relatedEntity.isPresent()) {
            endpoints.add(new EntityRelationship<>(new ResolvedEntity<Endpoint>(
                    new Endpoint(getName(), target, available), getName(),
                    target), Relationship.ITEM));
        } else {
            Entity<?> childEntity = relatedEntity.get().getEntity();
            if (childEntity instanceof ResolvedEntity<?>) {
                ResolvedEntity<Endpoint> endpoint = (ResolvedEntity<Endpoint>) childEntity;
                endpoint.getProperties().setAvailable(available);
            }
        }
    }

    public ResolvedEntity<Endpoint> getEndpoint(ResolvedEntity<Proxy> entity,
            String target) {
        return (ResolvedEntity<Endpoint>) getEndpoints().stream()
                .filter(e -> e.hasNature(Relationship.ITEM)
                        && e.getEntity() instanceof ResolvedEntity<?>)
                .filter(e -> ((ResolvedEntity<Endpoint>) (e.getEntity()))
                        .getProperties().getProperty("target") != null
                        && ((ResolvedEntity<Endpoint>) (e.getEntity()))
                                .getProperties().getProperty("target")
                                .equals(target))
                .findAny().get().getEntity();

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
    public Set<EntityRelationship<Endpoint>> getEndpoints() {
        return endpoints;
    }

    @HateoasChildren(Relationship.ITEM)
    public void setEndpoints(
            Collection<EntityRelationship<Endpoint>> endpoints) {
        this.endpoints.addAll(endpoints);
    }

}
