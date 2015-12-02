package au.com.windyroad.servicegateway.model;

import au.com.windyroad.hateoas.core.ResolvedEntity;

public class EndpointEntity extends ResolvedEntity<Endpoint> {

    public EndpointEntity() {
    }

    public EndpointEntity(Endpoint endpoint, String... args) {
        super(endpoint, args);
    }

}