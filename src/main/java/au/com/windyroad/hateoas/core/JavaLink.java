package au.com.windyroad.hateoas.core;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.windyroad.hateoas.server.annotations.HateoasController;

public class JavaLink extends Link {

    private Object entityOrProperties;
    private Object[] parameters;
    private Object javaController;

    protected JavaLink() {
    }

    public JavaLink(Object javaController, Object entityOrProperties,
            Object... parameters) {
        this.entityOrProperties = entityOrProperties;
        this.parameters = parameters;
        this.javaController = javaController;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T resolve(Class<T> type) {
        return (T) entityOrProperties;
    }

    @Override
    public MediaType getRepresentationFormat() {
        return MediaTypes.SIREN_JSON;
    }

    @Override
    @JsonProperty("href")
    public URI getAddress() {
        if (entityOrProperties != null) {
            Class<?> controller;
            if (entityOrProperties instanceof ResolvedEntity<?>) {
                ResolvedEntity<?> entity = (ResolvedEntity<?>) entityOrProperties;
                controller = javaController.getClass()
                        .getAnnotation(HateoasController.class).value();
            } else {
                controller = entityOrProperties.getClass()
                        .getAnnotation(HateoasController.class).value();
            }
            URI uri = ControllerLinkBuilder.linkTo(controller, parameters)
                    .toUri();
            return uri;
        } else {
            return null;
        }
    }

    @Override
    public <T> T resolve(ParameterizedTypeReference<T> type) {
        return (T) entityOrProperties;
    }
}
