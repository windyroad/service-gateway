package au.com.windyroad.hateoas.core.entities;

import au.com.windyroad.hateoas.core.Link;
import au.com.windyroad.hateoas.core.Relationship;

public class CreatedEntity extends LinkedEntity {

    public CreatedEntity(EntityWrapper<?> entity) {
        super(entity.getLink(Relationship.SELF), entity.getLabel(),
                entity.getNatures());
    }

    public CreatedEntity(Link link) {
        super(link);
    }

}
