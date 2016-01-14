package au.com.windyroad.hateoas.core.entities;

import au.com.windyroad.hateoas.core.Link;
import au.com.windyroad.hateoas.core.Relationship;

public class UpdatedEntity extends LinkedEntity {

    public UpdatedEntity(EntityWrapper<?> entity) {
        super(entity.getLink(Relationship.SELF), entity.getLabel(),
                entity.getNatures());
    }

    public UpdatedEntity(Link link) {
        super(link);
    }

}
