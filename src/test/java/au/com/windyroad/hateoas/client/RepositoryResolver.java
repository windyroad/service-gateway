package au.com.windyroad.hateoas.client;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import au.com.windyroad.hateoas.core.Link;
import au.com.windyroad.hateoas.core.Resolver;
import au.com.windyroad.hateoas.core.entities.CreatedEntity;
import au.com.windyroad.hateoas.core.entities.EntityWrapper;
import au.com.windyroad.hateoas.core.entities.UpdatedEntity;
import au.com.windyroad.servicegateway.Repository;

@Component()
public class RepositoryResolver implements Resolver {

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    @Override
    public CompletableFuture<CreatedEntity> create(Link link,
            Map<String, Object> filteredParameters) {
        throw new NotImplementedException("todo");
    }

    @Override
    public CompletableFuture<Void> delete(Link link,
            Map<String, Object> filteredParameters) {
        throw new NotImplementedException("todo");

    }

    @Override
    public CompletableFuture<EntityWrapper<?>> get(Link link,
            Map<String, Object> filteredParameters) {
        throw new NotImplementedException("todo");
    }

    @Override
    public CompletableFuture<UpdatedEntity> update(Link link,
            Map<String, Object> filteredParameters) {
        throw new NotImplementedException("todo");
    }

    @Override
    public <E> CompletableFuture<E> get(String path, Class<E> type) {

        return repository.findOne(path).thenApply(entity -> {
            @SuppressWarnings("unchecked")
            E root = (E) entity;
            return root;
        });
    }

}
