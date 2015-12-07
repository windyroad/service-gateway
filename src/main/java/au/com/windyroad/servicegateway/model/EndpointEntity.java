package au.com.windyroad.servicegateway.model;

import org.springframework.context.ApplicationContext;

import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.servicegateway.Repository;

public class EndpointEntity extends ResolvedEntity<Endpoint> {

    public EndpointEntity() {
    }

    public EndpointEntity(ApplicationContext context, Repository repository,
            String path, Endpoint endpoint, String... args) {
        super(context, repository, path, endpoint, args);
    }

}