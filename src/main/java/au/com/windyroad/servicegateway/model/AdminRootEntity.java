package au.com.windyroad.servicegateway.model;

import org.springframework.context.ApplicationContext;

import au.com.windyroad.hateoas.core.RestEntity;
import au.com.windyroad.servicegateway.Repository;

public class AdminRootEntity extends RestEntity<AdminRoot> {

    public AdminRootEntity() {
    }

    public AdminRootEntity(ApplicationContext context, Repository repository,
            String path, AdminRoot properties, String title) {
        super(context, repository, path, properties, title);
    }

}