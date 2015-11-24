package au.com.windyroad.hateoas.serialization;

import com.fasterxml.jackson.databind.util.StdConverter;

import au.com.windyroad.hateoas2.Entity;
import au.com.windyroad.hateoas2.LinkedEntity;
import au.com.windyroad.hateoas2.Relationship;

public class EntityLinkConverter
        extends StdConverter<Entity, LinkedEntity> {

    public EntityLinkConverter() {

    }

    @Override
    public LinkedEntity convert(Entity value) {
        if (LinkedEntity.class.isAssignableFrom(value.getClass())) {
            return (LinkedEntity) value;
        } else {
            return new LinkedEntity(value.getLinks().stream()
                    .filter(l -> l.hasNature(Relationship.SELF)).findAny()
                    .get().getLink());
        }
    }
}