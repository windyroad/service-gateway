package au.com.windyroad.hateoas.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = RestLink.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
abstract public class Link extends Titled {

    public Link() {
    }

    public Link(Set<String> natures, String label) {
        super(natures, label);
    }

    public abstract <T> T resolve(Class<T> type);

    public abstract <T> T resolve(ParameterizedTypeReference<T> type);

    @JsonProperty("type")
    public abstract MediaType getRepresentationFormat();

    @JsonProperty("href")
    public abstract URI getAddress() throws URISyntaxException;
}
