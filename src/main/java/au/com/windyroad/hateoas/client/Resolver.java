package au.com.windyroad.hateoas.client;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import au.com.windyroad.hateoas.core.CreatedLinkedEntity;
import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.core.Link;
import au.com.windyroad.hateoas.core.UpdatedLinkedEntity;

public interface Resolver {

    CompletableFuture<CreatedLinkedEntity> create(Link link,
            Map<String, Object> filteredParameters);

    CompletableFuture<Void> delete(Link link,
            Map<String, Object> filteredParameters);

    CompletableFuture<EntityWrapper<?>> get(Link link,
            Map<String, Object> filteredParameters);

    CompletableFuture<UpdatedLinkedEntity> update(Link link,
            Map<String, Object> filteredParameters);

}
