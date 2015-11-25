package au.com.windyroad.hateoas2;

import com.fasterxml.jackson.databind.util.StdConverter;

public class EntityLinkConverter extends StdConverter<Entity, LinkedEntity> {

    public EntityLinkConverter() {

    }

    @Override
    public LinkedEntity convert(Entity value) {
        if (LinkedEntity.class.isAssignableFrom(value.getClass())) {
            return (LinkedEntity) value;
        } else {
            return new LinkedEntity(
                    value.getLink(Relationship.SELF).getAddress(),
                    value.getNatures(), value.getLabel());
        }
    }
}