package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.core.DummyInvocationUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

import au.com.windyroad.hateoas.EmbeddedEntity;
import au.com.windyroad.hateoas.EmbeddedEntityLink;
import au.com.windyroad.hateoas.Entity;
import au.com.windyroad.hateoas.JavaLink;
import au.com.windyroad.servicegateway.controller.AdminProxiesController;
import au.com.windyroad.servicegateway.controller.AdminProxyController;

@Component
public class Proxies extends Entity<Map<String, String>> {
    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @JsonIgnore
    private UnifiedSetWithHashingStrategy<Proxy> proxies = new UnifiedSetWithHashingStrategy<>(
            HashingStrategies.fromFunction(Proxy::getName));

    protected Proxies() throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException,
            SecurityException {
        this.addLink(new JavaLink(this, DummyInvocationUtils
                .methodOn(AdminProxiesController.class).self()));
    }

    public EmbeddedEntityLink createProxy(String proxyPath,
            String targetEndPoint)
                    throws NoSuchMethodException, SecurityException,
                    IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException, URISyntaxException {
        Proxy proxy = new Proxy(proxyPath, targetEndPoint);
        return addProxy(proxy);
    }

    public EmbeddedEntityLink addProxy(Proxy proxy)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        boolean added = proxies.add(proxy);
        if (added) {
            ;
            return super.addEmbeddedEntity(proxy,
                    DummyInvocationUtils.methodOn(AdminProxyController.class)
                            .self(proxy.getName()));
        }
        return null;
    }

    public Proxy getProxy(String path) {
        Proxy proxy1 = this.proxies.get(new Proxy(path));
        for (EmbeddedEntity entity : super.getEntities()) {
            Proxy proxy2 = entity.toEntity(Proxy.class);
            if (path != null && path.equals(proxy2.getProperties().getName())) {
                return proxy2;
            }
        }
        return proxy1;
    }

}
