package au.com.windyroad.servicegateway.resource;

import au.com.windyroad.hateoas.Resource;
import au.com.windyroad.servicegateway.model.Proxies;

public class ProxyResource extends Resource<Proxies> {

	public ProxyResource(Proxies content) {
		super(content);
	}

	public ProxyResource() {
		super();
	}

}
