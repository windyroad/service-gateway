package au.com.windyroad.servicegateway.model;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

@Component
public class Proxies {
    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @JsonProperty("proxies")
    private UnifiedSetWithHashingStrategy<Proxy> proxies = new UnifiedSetWithHashingStrategy<>(
            HashingStrategies.fromFunction(Proxy::getName));

    public Proxy createProxy(String proxyPath, String targetEndPoint) {
        Proxy proxy = new Proxy(proxyPath, targetEndPoint);
        addProxy(proxy);
        return proxy;
    }

    public Collection<Proxy> getProxies() {
        return proxies;
    }

    public void setFields(Collection<Proxy> proxies) {
        this.proxies = new UnifiedSetWithHashingStrategy<>(
                HashingStrategies.fromFunction(Proxy::getName), proxies);
    }

    public void addProxy(Proxy proxy) {
        this.proxies.add(proxy);
    }

    public Proxy getProxy(String path) {
        return this.proxies.get(new Proxy(path));
    }
}
