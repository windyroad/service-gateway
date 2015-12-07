package au.com.windyroad.servicegateway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.servicegateway.model.Endpoint;

@org.springframework.stereotype.Repository("serverRepository")
public class InMemoryRepository implements Repository {

    Map<String, ResolvedEntity<?>> resolvedEntities = new HashMap<>();
    // Map<String, ProxyEntity> proxies = new HashMap<>();
    // Map<String, EndpointEntity> endpoints = new HashMap<>();

    // @Override
    // public ProxyEntity getProxy(String proxyName) {
    // return proxies.get(proxyName);
    // }
    //
    // @Override
    // public void store(ProxyEntity proxy) {
    // proxies.put(proxy.getProperties().getName(), proxy);
    // }
    //
    // @Override
    // public Collection<ProxyEntity> getProxies() {
    // // TODO paging
    // return proxies.values();
    // }
    //
    // @Override
    // public void deleteProxy(String proxyName) {
    // proxies.remove(proxyName);
    // }
    //
    // @Override
    // public EndpointEntity getEndpoint(String target) {
    // return this.endpoints.get(target);
    // }
    //
    // @Override
    // public void store(EndpointEntity endpoint) {
    // this.endpoints.put(endpoint.getProperties().getTarget(), endpoint);
    // }
    //
    // @Override
    // public Collection<EndpointEntity> getEndpoints() {
    // // TODO paging
    // return endpoints.values();
    // }
    //
    // @Override
    // public ProxiesEntity getRoot() {
    // return proxiesEntity;
    // }

    @Override
    public ResolvedEntity<?> get(String path) {
        return resolvedEntities.get(path);
    }

    @Override
    public void put(String path, ResolvedEntity<?> resolvedEntity) {
        resolvedEntities.put(path, resolvedEntity);
    }

    @Override
    public void remove(String path) {
        resolvedEntities.remove(path);
    }

    @Override
    public Collection<ResolvedEntity<?>> getEndpointsUnder(String target) {
        List<ResolvedEntity<?>> rval = new ArrayList<>();
        for (ResolvedEntity<?> entity : resolvedEntities.values()) {
            boolean isEndpoint = entity.getNatures().contains("Endpoint");
            if (isEndpoint) {
                String candidateTarget = ((Endpoint) entity.getProperties())
                        .getTarget();
                boolean underTarget = candidateTarget.startsWith(target);
                if (underTarget) {
                    rval.add(entity);
                }
            }
        }
        return rval;
    }

    @Override
    public Collection<ResolvedEntity<?>> getProxies() {
        return resolvedEntities.values().stream()
                .filter(e -> e.getNatures().contains("Proxy"))
                .collect(Collectors.toList());
    }

}
