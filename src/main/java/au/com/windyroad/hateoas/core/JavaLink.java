package au.com.windyroad.hateoas.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.mvc.BasicLinkBuilder;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonProperty;

public class JavaLink extends Link {

    private EntityWrapper<?> properties;

    protected JavaLink() {
    }

    public JavaLink(EntityWrapper<?> properties) {
        this.properties = properties;
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
        return BasicLinkBuilder.linkToCurrentMapping().slash(properties)
                .toUri();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T resolve(ParameterizedTypeReference<T> type) {
        return (T) properties;
    }
}
