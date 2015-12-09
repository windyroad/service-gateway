package au.com.windyroad.servicegateway;

import java.util.Collection;

import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.EntityWrapper;

public interface Repository {

    EntityWrapper<?> get(String path);

    void put(String path, EntityWrapper<?> resolvedEntity);

    void remove(String path);

    Collection<EntityWrapper<?>> getEndpointsUnder(String target);

    Collection<EntityWrapper<?>> getProxies();

    Collection<EntityRelationship> getChildren(String parentPath);

    void addChild(String parentPath, String childPath, EntityWrapper<?> child,
            String... natures);

}
