package au.com.windyroad.servicegateway.model;

import java.util.concurrent.CompletableFuture;

import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;

import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.core.UpdatedLinkedEntity;
import au.com.windyroad.servicegateway.Repository;

public class Proxy {

    private String target;
    private String name;

    public static ParameterizedTypeReference<EntityWrapper<Proxy>> wrapperType() {
        return new ParameterizedTypeReference<EntityWrapper<Proxy>>() {
        };
    }

    protected Proxy() {
    }

    public Proxy(String name, String target) {
        this.name = name;
        this.target = target;
    }

    public String getTarget() {
        return this.target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    public CompletableFuture<UpdatedLinkedEntity> setEndpoint(
            ApplicationContext context, Repository repository, String target,
            String available) {
        String path = Endpoint.buildPath(this.target + "/" + target);
        CompletableFuture<EntityWrapper<?>> future = repository.findOne(path);

        return future.thenApplyAsync(existingEndpoint -> {
            EntityWrapper<Endpoint> endpoint = (EntityWrapper<Endpoint>) existingEndpoint;
            if (existingEndpoint == null) {
                endpoint = new EntityWrapper<Endpoint>(context, repository,
                        path,
                        new Endpoint(this.target + "/" + target,
                                Boolean.parseBoolean(available)),
                        "Endpoint `" + this.target + "/" + target + "`");
            } else {
                endpoint.getProperties()
                        .setAvailable(Boolean.parseBoolean(available));
            }
            repository.save(endpoint);
            return new UpdatedLinkedEntity(endpoint);
        });
    }
}
