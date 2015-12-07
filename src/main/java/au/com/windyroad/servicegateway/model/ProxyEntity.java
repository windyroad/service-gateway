package au.com.windyroad.servicegateway.model;

import org.springframework.context.ApplicationContext;

import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.servicegateway.Repository;

public class ProxyEntity extends ResolvedEntity<Proxy> {

    public ProxyEntity() {
    }

    public ProxyEntity(ApplicationContext context, Repository repository,
            String path, Proxy properties, String... args) {
        super(context, repository, path, properties, args);
    }

}