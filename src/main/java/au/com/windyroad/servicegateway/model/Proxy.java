package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.com.windyroad.hateoas.annotations.Rel;
import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.JavaLink;
import au.com.windyroad.hateoas.core.NavigationalRelationship;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.controller.AdminProxyController;

@HateoasController(AdminProxyController.class)
public class Proxy extends ResolvedEntity {

    private static final String TARGET = "target";
    private static final String NAME = "name";

    protected Proxy() {
    }

    public Proxy(String name, String target) throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        super.add(new NavigationalRelationship(new JavaLink(this, name),
                Rel.SELF));
        getProperties().setProperty(NAME, name);
        getProperties().setProperty(TARGET, target);
    }

    public String getTarget() {
        return this.getProperties().getProperty(TARGET);
    }

    public void setTarget(String target) {
        this.getProperties().setProperty(TARGET, target);
    }

    public void setEndpoint(String target, boolean available)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        Optional<EntityRelationship> relatedEntity = super.getEntities()
                .stream().filter(e -> e.hasNature(Rel.ITEM))
                .filter(e -> e.getEntity().getProperties()
                        .getProperty("target") != null
                        && e.getEntity().getProperties().getProperty("target")
                                .equals(target))
                .findAny();
        if (!relatedEntity.isPresent()) {
            super.addEntity(new EntityRelationship(
                    new Endpoint(getName(), target, available),
                    Relationship.ITEM));
        } else {
            Entity entity = relatedEntity.get().getEntity();
            if (entity instanceof Endpoint) {
                Endpoint endpoint = (Endpoint) entity;
                endpoint.setAvailable(available);
            }
        }
    }

    public Endpoint getEndpoint(String target) {
        return (Endpoint) super.getEntities().stream()
                .filter(e -> e.hasNature(Rel.ITEM))
                .filter(e -> e.getEntity().getProperties()
                        .getProperty("target") != null
                        && e.getEntity().getProperties().getProperty("target")
                                .equals(target))
                .findAny().get().getEntity();

    }

    /**
     * @return the name
     */
    public String getName() {
        return getProperties().getProperty("name");
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        getProperties().setProperty("name", name);
    }

    @HateoasAction(nature = HttpMethod.PUT, controller = AdminProxyController.class)
    public Entity update(@RequestParam("endpoint") String targetEndPoint) {
        this.setTarget(targetEndPoint);
        return this;
    }

}
