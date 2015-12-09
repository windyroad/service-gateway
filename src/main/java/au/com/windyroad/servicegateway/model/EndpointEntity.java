package au.com.windyroad.servicegateway.model;

import org.springframework.context.ApplicationContext;

import au.com.windyroad.hateoas.core.RestEntity;
import au.com.windyroad.servicegateway.Repository;

public class EndpointEntity extends RestEntity<Endpoint> {

    public EndpointEntity() {
    }

    public EndpointEntity(ApplicationContext context, Repository repository,
            String path, Endpoint endpoint, String... args) {
        super(context, repository, path, endpoint, args);
    }

}