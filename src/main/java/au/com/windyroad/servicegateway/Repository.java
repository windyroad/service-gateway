package au.com.windyroad.servicegateway;

import au.com.windyroad.servicegateway.model.ProxyEntity;

public interface Repository {

    ProxyEntity getProxy(String proxyName);

    void store(ProxyEntity proxy);

}
