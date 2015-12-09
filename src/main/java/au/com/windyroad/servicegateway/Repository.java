package au.com.windyroad.servicegateway;

import java.util.Collection;

import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.EntityWrapper;

public interface Repository {

    EntityWrapper<?> get(String path);

    void put(EntityWrapper<?> resolvedEntity);

    void remove(EntityWrapper<?> entity);

    Collection<EntityWrapper<?>> getEndpointsUnder(String target);

    Collection<EntityWrapper<?>> getProxies();

    Collection<EntityRelationship> getChildren(String parentPath);

    void addChild(EntityWrapper<?> parent, EntityWrapper<?> child,
            String... natures);

}
