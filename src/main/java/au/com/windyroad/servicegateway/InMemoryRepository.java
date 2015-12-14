package au.com.windyroad.servicegateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;

import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.servicegateway.model.Endpoint;
import au.com.windyroad.servicegateway.model.Proxy;

@org.springframework.stereotype.Repository("serverRepository")
public class InMemoryRepository implements Repository {

    Map<String, EntityWrapper<?>> entities = new HashMap<>();
    MultiValueMap<String, EntityRelationship> children = new LinkedMultiValueMap<>();

    Map<String, BiFunction<Repository, EntityWrapper<?>, Stream<EntityRelationship>>> childrenQuery = new HashMap<>();

    @Override
    public <S extends EntityWrapper<?>> CompletableFuture<S> save(S entity) {
        return CompletableFuture.supplyAsync(() -> {
            entities.put(entity.getId(), entity);
            return entity;
        });

    }

    @Override
    public <S extends EntityWrapper<?>> CompletableFuture<Iterable<S>> save(
            Iterable<S> entities) {
        return CompletableFuture.supplyAsync(() -> {
            entities.forEach(e -> save(e));
            return entities;
        });

    }

    @Override
    public CompletableFuture<EntityWrapper<?>> findOne(String id) {
        return CompletableFuture.supplyAsync(() -> entities.get(id));

    }

    @Override
    public boolean exists(String id) {
        return entities.containsKey(id);
    }

    @Override
    public Iterable<EntityWrapper<?>> findAll() {
        return entities.values();
    }

    @Override
    public Iterable<EntityWrapper<?>> findAll(Iterable<String> ids) {
        Set<EntityWrapper<?>> rval = new HashSet<EntityWrapper<?>>();
        ids.forEach(id -> {
            EntityWrapper<?> entity = entities.get(id);
            if (entity != null) {
                rval.add(entity);
            }
        });
        return rval;
    }

    @Override
    public long count() {
        return entities.size();
    }

    @Override
    public CompletableFuture<Void> delete(String id) {
        return CompletableFuture.runAsync(() -> {
            entities.remove(id);
        });
    }

    @Override
    public CompletableFuture<Void> delete(EntityWrapper<?> entity) {
        return CompletableFuture.runAsync(() -> {
            entities.remove(entity.getId());
        });

    }

    @Override
    public CompletableFuture<Void> delete(
            Iterable<? extends EntityWrapper<?>> entities) {
        return CompletableFuture.runAsync(() -> {
            entities.forEach(e -> delete(e));
        });
    }

    @Override
    public CompletableFuture<Void> deleteAll() {
        return CompletableFuture.runAsync(() -> {
            entities.clear();
        });
    }

    @Override
    public Iterable<EntityWrapper<?>> findAll(Sort sort) {
        throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public Page<EntityWrapper<?>> findAll(Pageable pageable) {
        throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED);
    }

    @Override
    public Stream<EntityWrapper<?>> findByEndpointsForProxy(
            EntityWrapper<?> entity) {
        return this.entities.values().stream().filter(e -> {
            if (!e.hasNature("Endpoint")) {
                return false;
            }
            EntityWrapper<Proxy> proxy = (EntityWrapper<Proxy>) entity;
            EntityWrapper<Endpoint> endpoint = (EntityWrapper<Endpoint>) e;
            String target = proxy.getProperties().getTarget();
            return endpoint.getProperties().getTarget().startsWith(target);
        });
    }

    @Override
    public void setChildren(EntityWrapper<?> entity,
            BiFunction<Repository, EntityWrapper<?>, Stream<EntityRelationship>> function) {
        childrenQuery.put(entity.getId(), function);
    }

    @Override
    public Stream<EntityRelationship> findChildren(EntityWrapper<?> entity) {
        BiFunction<Repository, EntityWrapper<?>, Stream<EntityRelationship>> function = childrenQuery
                .get(entity.getId());
        if (function != null) {
            Stream<EntityRelationship> result = function.apply(this, entity);
            return result;
        }
        final ArrayList<EntityRelationship> rval = new ArrayList<EntityRelationship>();
        return rval.stream();
    }

    @Override
    public Stream<EntityWrapper<?>> findAllProxies(EntityWrapper<?> entity) {
        return entities.values().stream().filter(e -> e.hasNature("Proxy"));
    }

}
