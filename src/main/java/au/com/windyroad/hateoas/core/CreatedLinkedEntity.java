package au.com.windyroad.hateoas.core;

import java.net.URI;

public class CreatedLinkedEntity extends LinkedEntity {

    public CreatedLinkedEntity(EntityWrapper<?> entity) {
        super(entity.getLink(Relationship.SELF), entity.getNatures(),
                entity.getTitle());
    }

    public CreatedLinkedEntity(URI location) {
        super(location);
    }

}
