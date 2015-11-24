package au.com.windyroad.hateoas2;

import java.net.URI;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.windyroad.hateoas.MediaTypes;

public class JavaLink extends Link {

    private ResolvedEntity entity;
    private Object[] parameters;

    protected JavaLink() {
    }

    public JavaLink(ResolvedEntity entity, Object... parameters) {
        this.entity = entity;
        this.parameters = parameters;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T resolve(Class<T> type) {
        return (T) entity;
    }

    @Override
    public MediaType getRepresentationFormat() {
        return MediaTypes.SIREN_JSON;
    }

    @Override
    @JsonProperty("href")
    public URI getAddress() throws NoSuchMethodException, SecurityException {
        if (entity != null) {
            Class<?> controller = entity.getClass()
                    .getAnnotation(HateoasController.class).value();
            URI uri = ControllerLinkBuilder.linkTo(controller, parameters)
                    .toUri();
            return uri;
        } else {
            return null;
        }
    }
}
