package au.com.windyroad.hateoas;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class EmbeddedEntityLink extends Link
        implements EmbeddedEntity {

    protected EmbeddedEntityLink() {
    }

    public EmbeddedEntityLink(String[] rel) {
        super(rel);
    }

    public EmbeddedEntityLink(Object invocationValue) {
        super(invocationValue);
    }

    @Override
    public <T extends Entity<?>> T toEntity(Class<T> type) {
        return follow(type);
    }

    @Override
    public <T extends Entity<?>> T toEntity(
            ParameterizedTypeReference<T> type) {
        return follow(type);
    }

}
