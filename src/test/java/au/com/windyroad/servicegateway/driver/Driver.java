package au.com.windyroad.servicegateway.driver;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

public interface Driver {

    void clearProxies();

    void checkPingService(String path) throws Exception;

    void get(String path) throws Exception;

    void checkCurrentEndpointAvailable();

    void checkEndpointExists(String proxyName, String endpoint)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException;

    void createProxy(String proxyName, String endpoint)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException;

}
