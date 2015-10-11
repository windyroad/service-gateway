package au.com.windyroad.servicegateway.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.windyroad.hateoas.Entity;

public class Proxy extends Entity<Map<String, String>> {

    @JsonProperty("target")
    private String target;
    private String name;

    @JsonProperty("endpoints")
    private Map<String, Boolean> endpoints = new HashMap<>();

    protected Proxy() {
    }

    public Proxy(String name, String target) {
        this.name = name;
        this.target = target;
    }

    public Proxy(String name) {
        this.name = name;
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

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

}
