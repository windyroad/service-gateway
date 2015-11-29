package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Properties;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.controller.AdminProxyController;

@HateoasController(AdminProxyController.class)
public class ProxyProperties extends Properties {

    private static final String TARGET = "target";
    private static final String NAME = "name";

    protected ProxyProperties() {
        super(new Properties());
    }

    public ProxyProperties(String name, String target)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
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

    public void setEndpoint(ResolvedEntity<ProxyProperties> entity,
            String target, boolean available)
                    throws NoSuchMethodException, SecurityException,
                    IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException, URISyntaxException {
        Optional<EntityRelationship<?>> relatedEntity = entity.getEntities()
                .stream().filter(e -> e.hasNature(Relationship.ITEM))
                .filter(e -> ((Endpoint) (e.getEntity())).getProperties()
                        .getProperty("target") != null
                        && ((Endpoint) (e.getEntity())).getProperties()
                                .getProperty("target").equals(target))
                .findAny();
        if (!relatedEntity.isPresent()) {
            entity.addEntity(new EntityRelationship<>(
                    new Endpoint(getName(), target, available),
                    Relationship.ITEM));
        } else {
            Entity<?> childEntity = relatedEntity.get().getEntity();
            if (childEntity instanceof Endpoint) {
                Endpoint endpoint = (Endpoint) childEntity;
                endpoint.getProperties().setAvailable(available);
            }
        }
    }

    public Endpoint getEndpoint(ResolvedEntity<ProxyProperties> entity,
            String target) {
        return (Endpoint) entity.getEntities().stream()
                .filter(e -> e.hasNature(Relationship.ITEM)
                        && e.getEntity() instanceof Endpoint)
                .filter(e -> ((Endpoint) (e.getEntity())).getProperties()
                        .getProperty("target") != null
                        && ((Endpoint) (e.getEntity())).getProperties()
                                .getProperty("target").equals(target))
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
    public ProxyProperties update(ResolvedEntity<ProxiesProperties> entity,
            @RequestParam("endpoint") String targetEndPoint) {
        this.setTarget(targetEndPoint);
        return this;
    }

}
