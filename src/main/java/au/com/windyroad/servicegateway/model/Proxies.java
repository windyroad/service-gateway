package au.com.windyroad.servicegateway.model;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

import au.com.windyroad.hateoas.Entity;
import au.com.windyroad.hateoas.annotations.Rel;

@Component
public class Proxies extends Entity<Map<String, String>> {
    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @JsonIgnore
    private UnifiedSetWithHashingStrategy<Proxy> proxies = new UnifiedSetWithHashingStrategy<>(
            HashingStrategies.fromFunction(Proxy::getName));

    public boolean createProxy(String proxyPath, String targetEndPoint) {
        Proxy proxy = new Proxy(proxyPath, targetEndPoint);
        return addProxy(proxy);
    }

    public boolean addProxy(Proxy proxy) {
        proxies.add(proxy);
        return super.addEmbeddedEntity(proxy, Rel.ITEM);
    }

    public Proxy getProxy(String path) {
        return this.proxies.get(new Proxy(path));
    }
}
