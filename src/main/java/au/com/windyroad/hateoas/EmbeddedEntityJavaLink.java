package au.com.windyroad.hateoas;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonDeserialize(as = EmbeddedEntityHttpLink.class)
public class EmbeddedEntityJavaLink<K extends Entity<?>>
        extends EmbeddedEntityLink {

    private K entity;

    public EmbeddedEntityJavaLink(K entity, String[] rel) {
        super(rel);
        this.entity = entity;
    }

    @Override
    public URI getHref() {
        throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED,
                "TODO: convert to html link");
    }

    @Override
    public Entity<?> follow() {
        return entity;
    }

}
