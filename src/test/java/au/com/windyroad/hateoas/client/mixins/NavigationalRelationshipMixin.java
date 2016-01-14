package au.com.windyroad.hateoas.client.mixins;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import au.com.windyroad.hateoas.client.builder.NavigationalRelationshipBuilder;

@JsonDeserialize(builder = NavigationalRelationshipBuilder.class)
public abstract class NavigationalRelationshipMixin {

}
