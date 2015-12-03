package au.com.windyroad.servicegateway;

import java.util.Collection;

import au.com.windyroad.servicegateway.model.EndpointEntity;
import au.com.windyroad.servicegateway.model.ProxyEntity;

public interface Repository {

    ProxyEntity getProxy(String proxyName);

    void store(ProxyEntity proxy);

    Collection<ProxyEntity> getProxies();

    void deleteProxy(String proxyName);

    EndpointEntity getEndpoint(String target);

    void store(EndpointEntity endpoint);

    Collection<EndpointEntity> getEndpoints();

}
