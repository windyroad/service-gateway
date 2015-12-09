package au.com.windyroad.servicegateway.model;

import org.springframework.context.ApplicationContext;

import au.com.windyroad.hateoas.core.RestEntity;
import au.com.windyroad.servicegateway.Repository;

public class ProxyEntity extends RestEntity<Proxy> {

    public ProxyEntity() {
    }

    public ProxyEntity(ApplicationContext context, Repository repository,
            String path, Proxy properties, String... args) {
        super(context, repository, path, properties, args);
    }

}