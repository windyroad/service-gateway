package au.com.windyroad.hateoas;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonDeserialize(as = EmbeddedEntityHttpLink.class)
public class EmbeddedEntityJavaLink<K extends Entity<?>>
        extends EmbeddedEntityLink {

    private K entity;
    private Object invocationValue;

    protected EmbeddedEntityJavaLink() {
    }

    public EmbeddedEntityJavaLink(K entity, Object invocationValue) {
        super(invocationValue);
        this.entity = entity;
        this.invocationValue = invocationValue;
    }

    @Override
    public URI getHref() {
        URI uri = invocationValue == null ? null
                : ControllerLinkBuilder.linkTo(invocationValue).toUri();
        return uri;
    }

    @Override
    public <T extends Entity<?>> T follow(Class<T> type) {
        return (T) entity;
    }

    @Override
    public <T extends Entity<?>> T follow(ParameterizedTypeReference<T> type) {
        return (T) entity;
    }

}
