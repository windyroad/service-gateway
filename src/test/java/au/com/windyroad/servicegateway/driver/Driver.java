package au.com.windyroad.servicegateway.driver;

import au.com.windyroad.servicegateway.Context;

public interface Driver {

	void clearProxies();

	void checkPingService(String path) throws Exception;

	void createProxy(Context context) throws Exception;

	void get(String path) throws Exception;

	void checkEndpointAvailable(Context context);

	void checkEndpointExists(Context context);

}
