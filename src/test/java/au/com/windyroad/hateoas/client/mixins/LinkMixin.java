package au.com.windyroad.hateoas.client.mixins;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import au.com.windyroad.hateoas.client.RestLink;

@JsonDeserialize(as = RestLink.class)
public abstract class LinkMixin {

}
