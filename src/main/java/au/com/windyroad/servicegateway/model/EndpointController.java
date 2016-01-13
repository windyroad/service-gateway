package au.com.windyroad.servicegateway.model;

import org.springframework.context.ApplicationContext;

import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.servicegateway.Repository;

public class EndpointController extends EntityWrapper<Endpoint> {

    protected EndpointController() {
        super(new Endpoint());
    }

    protected EndpointController(EndpointController src) {
        super(src);
    }

    public EndpointController(ApplicationContext context, Repository repository,
            String path, Endpoint properties, String title) {
        super(context, repository, path, properties, title);
    }
}
