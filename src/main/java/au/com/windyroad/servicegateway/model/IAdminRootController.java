package au.com.windyroad.servicegateway.model;

import java.util.concurrent.CompletableFuture;

import au.com.windyroad.hateoas.core.CreatedLinkedEntity;

public interface IAdminRootController {

    public CompletableFuture<CreatedLinkedEntity> createProxy(String proxyName,
            String endpoint);
}
