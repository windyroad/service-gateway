package au.com.windyroad.servicegateway.model;

import au.com.windyroad.hateoas.core.ResolvedEntity;

public class ProxyEntity extends ResolvedEntity<Proxy> {

    public ProxyEntity() {
    }

    public ProxyEntity(Proxy properties, String... args) {
        super(properties, args);
    }

}