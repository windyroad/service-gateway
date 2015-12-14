package au.com.windyroad.servicegateway;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;

import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.EntityWrapper;

public interface Repository {

    @Async
    public <S extends EntityWrapper<?>> CompletableFuture<S> save(S entity);

    public <S extends EntityWrapper<?>> CompletableFuture<Iterable<S>> save(
            Iterable<S> entities);

    public CompletableFuture<EntityWrapper<?>> findOne(String id);

    public boolean exists(String id);

    public Iterable<EntityWrapper<?>> findAll();

    public Iterable<EntityWrapper<?>> findAll(Iterable<String> ids);

    public long count();

    public void delete(String id);

    public void delete(EntityWrapper<?> entity);

    public void delete(Iterable<? extends EntityWrapper<?>> entities);

    public void deleteAll();

    public Iterable<EntityWrapper<?>> findAll(Sort sort);

    public Page<EntityWrapper<?>> findAll(Pageable pageable);

    public Stream<EntityWrapper<?>> findByEndpointsForProxy(
            EntityWrapper<?> entity);

    public void setChildren(EntityWrapper<?> entity,
            BiFunction<Repository, EntityWrapper<?>, Stream<EntityRelationship>> function);

    public Stream<EntityRelationship> findChildren(EntityWrapper<?> entity);

    public Stream<EntityWrapper<?>> findAllProxies(EntityWrapper<?> entity);

}
