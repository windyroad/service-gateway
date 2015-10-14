package au.com.windyroad.hateoas;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.annotation.JsonInclude;

import au.com.windyroad.hateoas.annotations.Rel;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class EmbeddedEntityHttpLink extends Link
        implements EmbeddedEntity {

    public EmbeddedEntityHttpLink(String[] rel) {
        super(rel);
    }

    @Override
    public <T extends Entity<?>> T toEntity(Class<T> type) {
        throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED,
                "TODO: need's to get the link and return the corresponding entity");
    }

    @Override
    public <T extends Entity<?>> T toEntity(
            ParameterizedTypeReference<T> type) {
        throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED,
                "TODO: need's to get the link and return the corresponding entity");
    }

}
