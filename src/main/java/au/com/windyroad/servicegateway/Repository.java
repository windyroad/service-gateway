package au.com.windyroad.servicegateway;

import java.util.List;
import java.util.function.BiFunction;

import org.springframework.data.repository.PagingAndSortingRepository;

import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.EntityWrapper;

public interface Repository
        extends PagingAndSortingRepository<EntityWrapper<?>, String> {

    public List<EntityWrapper<?>> findByEndpointsForProxy(
            EntityWrapper<?> entity);

    public List<EntityWrapper<?>> findAllProxies(EntityWrapper<?> entity);

    public void setChildren(EntityWrapper<?> entity,
            BiFunction<Repository, EntityWrapper<?>, List<EntityRelationship>> function);

    public List<EntityRelationship> findChildren(EntityWrapper<?> entity);
}
