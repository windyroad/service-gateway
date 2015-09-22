package au.com.windyroad.hateoas;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class EmbeddedEntityLink<T> extends Link implements EmbeddedEntity<T> {

    @Override
    public Entity<T> toEntity() {
        throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED,
                "TODO: need's to get the link and return the corresponding entity");
    }

}
