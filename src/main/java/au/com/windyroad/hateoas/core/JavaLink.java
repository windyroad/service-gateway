package au.com.windyroad.hateoas.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.mvc.BasicLinkBuilder;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JavaLink extends Link {

    private Object properties;
    private Object[] parameters;
    private Object javaController;
    private String path;

    protected JavaLink() {
    }

    public JavaLink(Object javaController, String path, Object properties,
            Object... parameters) {
        this.properties = properties;
        this.parameters = parameters;
        this.javaController = javaController;
        this.path = path;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T resolve(Class<T> type) {
        return (T) properties;
    }

    @Override
    public MediaType getRepresentationFormat() {
        return MediaTypes.SIREN_JSON;
    }

    @Override
    @JsonProperty("href")
    public URI getAddress() throws URISyntaxException {
        return BasicLinkBuilder.linkToCurrentMapping().slash(path).toUri();
    }

    @Override
    public <T> T resolve(ParameterizedTypeReference<T> type) {
        return (T) properties;
    }
}
