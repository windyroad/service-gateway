package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.annotations.Rel;
import au.com.windyroad.hateoas2.Entity;
import au.com.windyroad.hateoas2.EntityRelationship;
import au.com.windyroad.hateoas2.HateoasAction;
import au.com.windyroad.hateoas2.HateoasController;
import au.com.windyroad.hateoas2.JavaAction;
import au.com.windyroad.hateoas2.Relationship;
import au.com.windyroad.hateoas2.ResolvedEntity;
import au.com.windyroad.servicegateway.controller.AdminProxiesController;

@Component
@HateoasController(AdminProxiesController.class)
public class Proxies extends ResolvedEntity {

    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected Proxies() throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException,
            SecurityException {
        // Properties context = null;
        // JavaAction action = JavaAction.fromFunction("createProxy",
        // ProxiesActionArgument::createProxy2);

        for (Method method : this.getClass().getMethods()) {
            if (method.getAnnotation(HateoasAction.class) != null) {
                super.add(new JavaAction(method));
            }
        }
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
        Stream<EntityRelationship> items = super.getEntities().stream()
                .filter(e -> e.hasNature(Rel.ITEM));
        Optional<EntityRelationship> existingProxy = items.filter(
                e -> e.getEntity().getProperties().getProperty("name") != null
                        && e.getEntity().getProperties().getProperty("name")
                                .equals(proxyPath))
                .findAny();

        if (existingProxy.isPresent()) {
            return existingProxy.get().getEntity();
        } else {
            Proxy proxy = new Proxy(proxyPath, targetEndPoint);
            super.addEntity(new EntityRelationship(proxy, Relationship.ITEM));
            return proxy;
        }
    }

    public Proxy getProxy(String path) {
        return (Proxy) super.getEntities().stream()
                .filter(e -> e.hasNature(Rel.ITEM))
                .filter(e -> e.getEntity().getProperties()
                        .getProperty("name") != null
                        && e.getEntity().getProperties().getProperty("name")
                                .equals(path))
                .findAny().get().getEntity();
    }

}
