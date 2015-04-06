package au.com.windyroad.servicegateway.driver;

public interface Driver {

	void clearProxies();

	void checkPingService(String path) throws Exception;

	void createProxy(String targetEndPoint, String proxyPath);

	void get(String path) throws Exception;

	void checkEndpointExists(String endpoint);

	void checkEndpointAvailable(String endpoint);

}
