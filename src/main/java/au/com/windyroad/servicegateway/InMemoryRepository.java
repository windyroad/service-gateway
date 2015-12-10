package au.com.windyroad.servicegateway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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

    Map<String, BiFunction<Repository, EntityWrapper<?>, List<EntityRelationship>>> childrenQuery = new HashMap<>();

    @Override
    public <S extends EntityWrapper<?>> S save(S entity) {
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends EntityWrapper<?>> Iterable<S> save(Iterable<S> entities) {
        entities.forEach(e -> save(e));
        return entities;
    }

    @Override
    public EntityWrapper<?> findOne(String id) {
        return entities.get(id);
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
    public void delete(String id) {
        entities.remove(id);
    }

    @Override
    public void delete(EntityWrapper<?> entity) {
        entities.remove(entity.getId());
    }

    @Override
    public void delete(Iterable<? extends EntityWrapper<?>> entities) {
        entities.forEach(e -> delete(e));
    }

    @Override
    public void deleteAll() {
        entities.clear();
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
    public List<EntityWrapper<?>> findByEndpointsForProxy(
            EntityWrapper<?> entity) {
        return this.entities.values().stream().filter(e -> {
            if (!e.hasNature("Endpoint")) {
                return false;
            }
            EntityWrapper<Proxy> proxy = (EntityWrapper<Proxy>) entity;
            EntityWrapper<Endpoint> endpoint = (EntityWrapper<Endpoint>) e;
            String target = proxy.getProperties().getTarget();
            return endpoint.getProperties().getTarget().startsWith(target);
        }).collect(Collectors.toList());
    }

    @Override
    public void setChildren(EntityWrapper<?> entity,
            BiFunction<Repository, EntityWrapper<?>, List<EntityRelationship>> function) {
        childrenQuery.put(entity.getId(), function);
    }

    @Override
    public List<EntityRelationship> findChildren(EntityWrapper<?> entity) {
        BiFunction<Repository, EntityWrapper<?>, List<EntityRelationship>> function = childrenQuery
                .get(entity.getId());
        return function == null ? new ArrayList<>()
                : function.apply(this, entity);
    }

    @Override
    public List<EntityWrapper<?>> findAllProxies(EntityWrapper<?> entity) {
        return entities.values().stream().filter(e -> e.hasNature("Proxy"))
                .collect(Collectors.toList());
    }

}
