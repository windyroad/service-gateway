package au.com.windyroad.hateoas;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

public class JavaLink extends Link {

    private Entity<?> entity;

    @Override
    public URI getHref() {
        throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED,
                "TODO: convert java link to href. Should only be needed during serialisation");
    }

    @Override
    public Entity<?> follow() {
        return entity;
    }

}
