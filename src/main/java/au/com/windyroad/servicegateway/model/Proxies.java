package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.core.DummyInvocationUtils;
import org.springframework.hateoas.core.DummyInvocationUtils.LastInvocationAware;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

import au.com.windyroad.hateoas.EmbeddedEntityJavaLink;
import au.com.windyroad.hateoas.EmbeddedEntityLink;
import au.com.windyroad.hateoas.annotations.Rel;
import au.com.windyroad.hateoas.annotations.SirenAction;
import au.com.windyroad.hateoas.annotations.SirenEntity;
import au.com.windyroad.hateoas.annotations.SirenLink;
import au.com.windyroad.hateoas.annotations.SirenProperty;
import au.com.windyroad.hateoas.annotations.Title;
import au.com.windyroad.hateoas.serialization.SirenConverter;
import au.com.windyroad.servicegateway.controller.AdminProxiesController;
import au.com.windyroad.servicegateway.controller.AdminProxyController;

@Component
@Title("{au.com.windyroad.service-gateway.messages.proxies}")
@JsonSerialize(converter = SirenConverter.class)
public class Proxies {

    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @JsonIgnore
    private UnifiedSetWithHashingStrategy<Proxy> proxies = new UnifiedSetWithHashingStrategy<>(
            HashingStrategies.fromFunction(Proxy::getName));

    protected Proxies() throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException,
            SecurityException {
    }

    @SirenLink(controller = AdminProxiesController.class, method = "self")
    @Rel(Rel.SELF)
    public Proxies self() {
        return this;
    }

    @SirenProperty
    public int getCount() {
        return proxies.size();
    }

    @SirenAction(controller = AdminProxiesController.class, method = "createProxy")
    public EmbeddedEntityLink createProxy(String proxyPath,
            String targetEndPoint)
                    throws NoSuchMethodException, SecurityException,
                    IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException, URISyntaxException {
        Proxy proxy = new Proxy(proxyPath, targetEndPoint);
        return addProxy(proxy);
    }

    private EmbeddedEntityLink addProxy(Proxy proxy)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        boolean added = proxies.add(proxy);

        if (added) {
            Object invocation = DummyInvocationUtils
                    .methodOn(AdminProxyController.class).self(proxy.getName());
            EmbeddedEntityJavaLink<?> link = new EmbeddedEntityJavaLink<>(proxy,
                    ((LastInvocationAware) invocation).getLastInvocation());
            return link;
        }
        return null;
    }

    public Proxy getProxy(String path) throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        Proxy proxy1 = this.proxies.get(new Proxy(path));
        return proxy1;
    }

    @SirenEntity
    public Collection<Proxy> getProxies() {
        return this.proxies.toSet();
    }

}
