package au.com.windyroad.hateoas;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class EmbeddedEntityLink extends Link
        implements EmbeddedEntity {

    public EmbeddedEntityLink(String[] rel) {
        super(rel);
    }

    @Override
    public <T extends Entity<?>> T toEntity(Class<T> type) {
        return (T) follow();
    }

    @Override
    public <T extends Entity<?>> T toEntity(
            ParameterizedTypeReference<T> type) {
        return (T) follow();
    }

}
