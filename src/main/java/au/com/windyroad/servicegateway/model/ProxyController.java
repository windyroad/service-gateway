package au.com.windyroad.servicegateway.model;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    public ProxyController(ApplicationContext context, Repository repository,
            String path, Proxy properties, String title) {
        super(context, repository, path, properties, title);
    }

    public CompletableFuture<UpdatedLinkedEntity> setEndpoint(
            EntityWrapper<Proxy> proxy, String target, String available)
                    throws UnsupportedEncodingException, InterruptedException,
                    ExecutionException {
        String endpointPath = proxy.getProperties().getTarget() + "/" + target;
        String path = Endpoint.buildPath(endpointPath);
        CompletableFuture<EntityWrapper<?>> future = repository.findOne(path);

        return future.thenApplyAsync(existingEndpoint -> {
            EntityWrapper<Endpoint> endpoint = (EntityWrapper<Endpoint>) existingEndpoint;
            if (existingEndpoint == null) {
                endpoint = new EntityWrapper<Endpoint>(context, repository,
                        path,
                        new Endpoint(endpointPath,
                                Boolean.parseBoolean(available)),
                        "Endpoint `" + endpointPath + "`");
            } else {
                endpoint.getProperties()
                        .setAvailable(Boolean.parseBoolean(available));
            }
            repository.save(endpoint);
            return new UpdatedLinkedEntity(endpoint.getAddress());
        });
    }

    public CompletableFuture<Void> deleteProxy(EntityWrapper<Proxy> proxy) {
        return repository.delete(proxy);
    }

    public CompletableFuture<UpdatedLinkedEntity> update(
            EntityWrapper<Proxy> proxy, String proxyName, String target) {
        return CompletableFuture.supplyAsync(() -> {
            proxy.getProperties().setTarget(target);
            repository.save(proxy);
            return new UpdatedLinkedEntity(proxy.getAddress());
        });
    }

}
