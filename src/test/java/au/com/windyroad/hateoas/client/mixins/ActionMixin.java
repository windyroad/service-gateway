package au.com.windyroad.hateoas.client.mixins;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import au.com.windyroad.hateoas.client.builder.RestActionBuilder;

@JsonDeserialize(builder = RestActionBuilder.class)
public abstract class ActionMixin {

}
