package au.com.windyroad.hateoas.client.mixins;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import au.com.windyroad.hateoas.client.builder.EntityRelationshipBuilder;

@JsonDeserialize(builder = EntityRelationshipBuilder.class)
public abstract class EntityRelationshipMixin {

}
