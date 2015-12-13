package au.com.windyroad.servicegateway;

import java.util.function.BiFunction;
import java.util.stream.Stream;

import org.springframework.data.repository.PagingAndSortingRepository;

import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.EntityWrapper;

public interface Repository
        extends PagingAndSortingRepository<EntityWrapper<?>, String> {

    public Stream<EntityWrapper<?>> findByEndpointsForProxy(
            EntityWrapper<?> entity);

    public Stream<EntityWrapper<?>> findAllProxies(EntityWrapper<?> entity);

    public void setChildren(EntityWrapper<?> entity,
            BiFunction<Repository, EntityWrapper<?>, Stream<EntityRelationship>> function);

    public Stream<EntityRelationship> findChildren(EntityWrapper<?> entity);
}
