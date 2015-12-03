package au.com.windyroad.servicegateway.model;

import org.springframework.context.ApplicationContext;

import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.servicegateway.Repository;

public class ProxyEntity extends ResolvedEntity<Proxy> {

    public ProxyEntity() {
    }

    public ProxyEntity(ApplicationContext context, Repository repository,
            Proxy properties, String... args) {
        super(context, repository, properties, args);
    }

}