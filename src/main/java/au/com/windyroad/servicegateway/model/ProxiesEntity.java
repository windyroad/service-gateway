package au.com.windyroad.servicegateway.model;

import org.springframework.context.ApplicationContext;

import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.servicegateway.Repository;

public class ProxiesEntity extends ResolvedEntity<Proxies> {

    public ProxiesEntity() {
    }

    public ProxiesEntity(ApplicationContext context, Repository repository,
            String path, Proxies properties, String... args) {
        super(context, repository, path, properties, args);
    }

}