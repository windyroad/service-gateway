package au.com.windyroad.hateoas2;

import org.eclipse.jdt.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import au.com.windyroad.hateoas.serialization.MessageSourceAwareSerializer;

abstract public class Resolvable {

    @JsonProperty("class")
    String[] natures = {};

    @Nullable
    @JsonSerialize(using = MessageSourceAwareSerializer.class)
    @JsonProperty("title")
    String label = null;
}
