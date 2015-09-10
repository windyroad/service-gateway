package au.com.windyroad.servicegateway.model;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import au.com.windyroad.hateoas.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class Proxies extends Entity {
	@JsonIgnore
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@JsonProperty("proxies")
	private Map<String, Proxy> proxies = new HashMap<>();

	public Proxy createProxy(String proxyPath, String targetEndPoint) {
		Proxy proxy = new Proxy(targetEndPoint);
		proxies.put(proxyPath, proxy);
		return proxy;
	}

	public Proxy getProxy(String name) {
		return this.proxies.get(name);
	}
}
