package au.com.windyroad.hateoas.core;

import java.net.URI;

public class UpdatedLinkedEntity extends LinkedEntity {

    public UpdatedLinkedEntity(EntityWrapper<?> entity) {
        super(entity.getLink(Relationship.SELF), entity.getNatures(),
                entity.getTitle());
    }

    public UpdatedLinkedEntity(URI location) {
        super(location);
    }

}
