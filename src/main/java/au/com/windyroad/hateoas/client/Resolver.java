package au.com.windyroad.hateoas.client;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import au.com.windyroad.hateoas.core.CreatedLinkedEntity;
import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.core.Link;
import au.com.windyroad.hateoas.core.UpdatedLinkedEntity;

public interface Resolver {

    public CompletableFuture<CreatedLinkedEntity> create(Link link,
            Map<String, Object> filteredParameters);

    public CompletableFuture<Void> delete(Link link,
            Map<String, Object> filteredParameters);

    public CompletableFuture<EntityWrapper<?>> get(Link link,
            Map<String, Object> filteredParameters);

    public CompletableFuture<UpdatedLinkedEntity> update(Link link,
            Map<String, Object> filteredParameters);

    public <E> CompletableFuture<E> get(String path, Class<E> type)
            throws URISyntaxException;

}
