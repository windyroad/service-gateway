package au.com.windyroad.hateoas2;

import java.net.URI;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = RestLink.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
abstract public class Link extends Resolvable {

    public abstract <T> T resolve(Class<T> type);

    @JsonProperty("type")
    public abstract MediaType getRepresentationFormat();

    @JsonProperty("href")
    public abstract URI getAddress()
            throws NoSuchMethodException, SecurityException;
}
