package au.com.windyroad.hateoas.core;

public class CreatedLinkedEntity extends LinkedEntity {

    public CreatedLinkedEntity(EntityWrapper<?> entity) {
        super(entity.getLink(Relationship.SELF), entity.getTitle(),
                entity.getNatures());
    }

    public CreatedLinkedEntity(Link link) {
        super(link);
    }

}
