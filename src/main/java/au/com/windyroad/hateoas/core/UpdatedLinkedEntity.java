package au.com.windyroad.hateoas.core;

public class UpdatedLinkedEntity extends LinkedEntity {

    public UpdatedLinkedEntity(EntityWrapper<?> entity) {
        super(entity.getLink(Relationship.SELF), entity.getNatures(),
                entity.getTitle());
    }

    public UpdatedLinkedEntity(Link link) {
        super(link);
    }

}
