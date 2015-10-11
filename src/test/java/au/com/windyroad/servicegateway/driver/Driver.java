package au.com.windyroad.servicegateway.driver;

import au.com.windyroad.servicegateway.TestContext;

public interface Driver {

    void clearProxies();

    void checkPingService(String path) throws Exception;

    void createProxy(TestContext context) throws Exception;

    void get(String path) throws Exception;

    void checkEndpointAvailable(TestContext context);

    void checkEndpointExists(TestContext context);

}
