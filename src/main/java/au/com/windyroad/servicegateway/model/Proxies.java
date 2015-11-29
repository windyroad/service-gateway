package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.controller.AdminProxiesController;

@HateoasController(AdminProxiesController.class)
public class Proxies {

    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public Proxies() {
    }

    @HateoasAction(nature = HttpMethod.POST, controller = AdminProxiesController.class)
    public Entity createProxy(ResolvedEntity<Proxies> entity,
            @RequestParam("proxyName") String proxyPath,
            @RequestParam("endpoint") String targetEndPoint)
                    throws NoSuchMethodException, SecurityException,
                    IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException, URISyntaxException {

        Stream<EntityRelationship<?>> items = entity.getEntities().stream()
                .filter(e -> e.hasNature(Relationship.ITEM));
        Optional<EntityRelationship<?>> existingProxy = items
                .filter(e -> ((ResolvedEntity<Proxy>) (e.getEntity()))
                        .getProperties().getProperty("name") != null
                        && ((ResolvedEntity<Proxy>) (e.getEntity()))
                                .getProperties().getProperty("name")
                                .equals(proxyPath))
                .findAny();

        if (existingProxy.isPresent()) {
            return existingProxy.get().getEntity();
        } else {
            ResolvedEntity<Proxy> proxy = new ResolvedEntity<Proxy>(
                    new Proxy(proxyPath, targetEndPoint), proxyPath);
            entity.addEntity(
                    new EntityRelationship<>(proxy, Relationship.ITEM));
            return proxy;
        }
    }

    public ResolvedEntity<Proxy> getProxy(
            ResolvedEntity<Proxies> entity, String path) {
        return (ResolvedEntity<Proxy>) entity.getEntities().stream()
                .filter(e -> e.hasNature(Relationship.ITEM))
                .filter(e -> ((ResolvedEntity<Proxy>) (e.getEntity()))
                        .getProperties().getProperty("name") != null
                        && ((ResolvedEntity<Proxy>) (e.getEntity()))
                                .getProperties().getProperty("name")
                                .equals(path))
                .findAny().get().getEntity();
    }

}
