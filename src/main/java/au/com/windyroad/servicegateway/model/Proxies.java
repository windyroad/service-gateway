package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.controller.AdminProxiesController;

@Component
@HateoasController(AdminProxiesController.class)
public class Proxies extends ResolvedEntity<Properties> {

    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected Proxies() throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException,
            SecurityException {
        super(new Properties());

    }

    // class ProxiesActionArgument extends ActionArgument<Proxies, Proxies> {
    // public ProxiesActionArgument(Proxies entity, Properties context) {
    // super(entity, context);
    // }
    //
    // public Proxies createProxy2() {
    // return getEntity().createProxy(proxyPath, targetEndPoint)
    // }
    //
    // }

    @HateoasAction(nature = HttpMethod.POST, controller = AdminProxiesController.class)
    public Entity createProxy(@RequestParam("proxyName") String proxyPath,
            @RequestParam("endpoint") String targetEndPoint)
                    throws NoSuchMethodException, SecurityException,
                    IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException, URISyntaxException {
        Stream<EntityRelationship<?>> items = super.getEntities().stream()
                .filter(e -> e.hasNature(Relationship.ITEM));
        Optional<EntityRelationship<?>> existingProxy = items
                .filter(e -> ((Proxy) (e.getEntity())).getProperties()
                        .getProperty("name") != null
                        && ((Proxy) (e.getEntity())).getProperties()
                                .getProperty("name").equals(proxyPath))
                .findAny();

        if (existingProxy.isPresent()) {
            return existingProxy.get().getEntity();
        } else {
            Proxy proxy = new Proxy(proxyPath, targetEndPoint);
            super.addEntity(new EntityRelationship<>(proxy, Relationship.ITEM));
            return proxy;
        }
    }

    public Proxy getProxy(String path) {
        return (Proxy) super.getEntities().stream()
                .filter(e -> e.hasNature(Relationship.ITEM))
                .filter(e -> ((Proxy) (e.getEntity())).getProperties()
                        .getProperty("name") != null
                        && ((Proxy) (e.getEntity())).getProperties()
                                .getProperty("name").equals(path))
                .findAny().get().getEntity();
    }

}
