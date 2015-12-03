package au.com.windyroad.servicegateway;

import java.util.HashMap;
import java.util.Map;

import au.com.windyroad.servicegateway.model.ProxyEntity;

@org.springframework.stereotype.Repository
public class InMemoryRepository implements Repository {

    Map<String, ProxyEntity> proxies = new HashMap<>();

    @Override
    public ProxyEntity getProxy(String proxyName) {
        return proxies.get(proxyName);
    }

    @Override
    public void store(ProxyEntity proxy) {
        proxies.put(proxy.getProperties().getName(), proxy);
    }

}
