package au.com.windyroad.hateoas.core;

public class UpdatedLinkedEntity extends LinkedEntity {

    public UpdatedLinkedEntity(EntityWrapper<?> entity) {
        super(entity.getLink(Relationship.SELF), entity.getTitle(),
                entity.getNatures());
    }

    public UpdatedLinkedEntity(Link link) {
        super(link);
    }

}
