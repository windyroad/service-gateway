package au.com.windyroad.hateoas.core;

public class CreatedLinkedEntity extends LinkedEntity {

    public CreatedLinkedEntity(EntityWrapper<?> entity) {
        super(entity.getLink(Relationship.SELF), entity.getNatures(),
                entity.getTitle());
    }

    public CreatedLinkedEntity(Link link) {
        super(link);
    }

}
