package au.com.windyroad.servicegateway.model;

import java.util.HashMap;
import java.util.Map;

import au.com.windyroad.hateoas.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Proxy extends Entity {

	@JsonProperty("target")
	private String target;

	@JsonProperty("endpoints")
	private Map<String, Boolean> endpoints = new HashMap<>();

	protected Proxy() {
	}

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
