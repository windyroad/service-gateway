package au.com.windyroad.servicegateway;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.servicegateway.model.Endpoint;

@org.springframework.stereotype.Repository("serverRepository")
public class InMemoryRepository implements Repository {

    Map<String, Object> entities = new HashMap<>();
    Map<String, EntityWrapper<?>> resolvedEntities = new HashMap<>();
    MultiValueMap<String, EntityRelationship> children = new LinkedMultiValueMap<>();

    @Override
    public EntityWrapper<?> get(String path) {
        return resolvedEntities.get(path);
    }

    @Override
    public void put(EntityWrapper<?> resolvedEntity) {
        resolvedEntities.put(resolvedEntity.getId(), resolvedEntity);
    }

    @Override
    public void remove(EntityWrapper<?> entity) {
        resolvedEntities.remove(entity.getId());
    }

    @Override
    public Collection<EntityWrapper<?>> getEndpointsUnder(String target) {
        List<EntityWrapper<?>> rval = new ArrayList<>();
        for (EntityWrapper<?> entity : resolvedEntities.values()) {
            boolean isEndpoint = entity.getNatures().contains("Endpoint");
            if (isEndpoint) {
                String candidateTarget = ((Endpoint) entity.getProperties())
                        .getTarget();
                boolean underTarget = candidateTarget.startsWith(target);
                if (underTarget) {
                    rval.add(entity);
                }
            }
        }
        return rval;
    }

    @Override
    public Collection<EntityWrapper<?>> getProxies() {
        return resolvedEntities.values().stream()
                .filter(e -> e.getNatures().contains("Proxy"))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<EntityRelationship> getChildren(String parentPath) {
        List<EntityRelationship> children = this.children.get(parentPath);

        return children == null ? new ArrayList<EntityRelationship>()
                : children;
    }

    @Override
    public void addChild(EntityWrapper<?> parent, EntityWrapper<?> child,
            String... natures) {
        children.add(parent.getId(), new EntityRelationship(child, natures));

    }

}
