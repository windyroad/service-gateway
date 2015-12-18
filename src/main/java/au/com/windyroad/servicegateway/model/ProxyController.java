package au.com.windyroad.servicegateway.model;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.core.UpdatedLinkedEntity;
import au.com.windyroad.servicegateway.Repository;

public class ProxyController extends EntityWrapper<Proxy> {

    @Autowired
    ApplicationContext context;

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    protected ProxyController() {

    }

    protected ProxyController(ProxyController src) {
        super(src);
        this.context = src.context;
        this.repository = src.repository;
    }

    public ProxyController(ApplicationContext context, Repository repository,
            String path, Proxy properties, String title) {
        super(context, repository, path, properties, title);
    }

    public CompletableFuture<UpdatedLinkedEntity> setEndpoint(String target,
            boolean available) {
        String endpointPath = this.getProperties().getTarget() + "/" + target;
        String path = Endpoint.buildPath(endpointPath);
        CompletableFuture<EntityWrapper<?>> future = repository.findOne(path);

        return future.thenApplyAsync(existingEndpoint -> {
            EndpointController endpoint = (EndpointController) existingEndpoint;
            if (existingEndpoint == null) {
                endpoint = new EndpointController(context, repository, path,
                        new Endpoint(endpointPath, available),
                        "Endpoint `" + endpointPath + "`");
            } else {
                endpoint.getProperties().setAvailable(available);
            }
            repository.save(endpoint);
            return new UpdatedLinkedEntity(endpoint);
        });
    }

    public CompletableFuture<Void> deleteProxy() {
        return repository.delete(this);
    }

    public CompletableFuture<UpdatedLinkedEntity> update(String proxyName,
            String target) {
        return CompletableFuture.supplyAsync(() -> {
            this.getProperties().setTarget(target);
            repository.save(this);
            return new UpdatedLinkedEntity(this);
        });
    }

}
