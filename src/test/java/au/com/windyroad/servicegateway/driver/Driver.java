package au.com.windyroad.servicegateway.driver;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

public interface Driver {

    void clearProxies();

    void checkPingService(String path) throws Exception;

    void get(String path) throws Exception;

    void checkCurrentEndpointAvailable();

    void checkEndpointExists(String proxyName, String endpoint)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, UnsupportedEncodingException,
            URISyntaxException;

    void createProxy(String proxyName, String endpoint)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException;

}
