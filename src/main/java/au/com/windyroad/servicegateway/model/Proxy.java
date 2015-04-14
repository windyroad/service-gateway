package au.com.windyroad.servicegateway.model;

import java.util.HashMap;
import java.util.Map;

public class Proxy {

	private String target;
	private Map<String, Boolean> endpoints = new HashMap<>();

	public Proxy(String target) {
		this.target = target;
	}

	public String getTarget() {
		return this.target;
	}

	public void addEndpoint(String target, boolean available) {
		endpoints.put(target, available);
	}

	public Boolean getEndpoint(String target) {
		return endpoints.get(target);
	}

}
