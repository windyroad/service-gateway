package au.com.windyroad.servicegateway.driver;

import au.com.windyroad.hateoas.Link;
import au.com.windyroad.servicegateway.TestContext;

public interface Driver {

    void clearProxies();

    void checkPingService(String path) throws Exception;

    Link createProxy(TestContext context) throws Exception;

    void get(String path) throws Exception;

    void checkEndpointAvailable(Link endpoint);

    Link checkEndpointExists(Link proxyLink, String endpoint);

}
