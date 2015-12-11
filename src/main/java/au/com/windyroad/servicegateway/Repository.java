package au.com.windyroad.servicegateway;

import java.util.Iterator;
import java.util.function.BiFunction;

import org.springframework.data.repository.PagingAndSortingRepository;

import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.EntityWrapper;

public interface Repository
        extends PagingAndSortingRepository<EntityWrapper<?>, String> {

    public Iterator<EntityWrapper<?>> findByEndpointsForProxy(
            EntityWrapper<?> entity);

    public Iterator<EntityWrapper<?>> findAllProxies(EntityWrapper<?> entity);

    public void setChildren(EntityWrapper<?> entity,
            BiFunction<Repository, EntityWrapper<?>, Iterator<EntityRelationship>> function);

    public Iterator<EntityRelationship> findChildren(EntityWrapper<?> entity);
}
