package au.com.windyroad.servicegateway;

import java.util.Collection;

import au.com.windyroad.hateoas.core.ResolvedEntity;

public interface Repository {

    // ProxyEntity getProxy(String proxyName);
    //
    // void store(ProxyEntity proxy);
    //
    // Collection<ProxyEntity> getProxies();
    //
    // void deleteProxy(String proxyName);
    //
    // EndpointEntity getEndpoint(String target);
    //
    // void store(EndpointEntity endpoint);
    //
    // Collection<EndpointEntity> getEndpoints();
    //
    // ProxiesEntity getRoot();

    ResolvedEntity<?> get(String path);

    void put(String path, ResolvedEntity<?> resolvedEntity);

    void remove(String path);

    Collection<ResolvedEntity<?>> getEndpointsUnder(String target);

    Collection<ResolvedEntity<?>> getProxies();

}
