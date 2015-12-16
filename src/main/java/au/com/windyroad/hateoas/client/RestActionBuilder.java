package au.com.windyroad.hateoas.client;

import java.net.URI;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.windyroad.hateoas.core.Parameter;

public class RestActionBuilder {

    private String identifier;
    private HttpMethod method;
    private URI href;
    private Parameter[] fields = {};

    @JsonProperty("method")
    public RestActionBuilder setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    @JsonProperty("name")
    public RestActionBuilder setName(String identifier) {
        this.identifier = identifier;
        return this;
    }

    @JsonProperty("href")
    public RestActionBuilder setHref(URI href) {
        this.href = href;
        return this;
    }

    @JsonProperty("fields")
    public RestActionBuilder setFields(Parameter[] fields) {
        this.fields = fields;
        return this;
    }

    public RestAction<?> build() {
        switch (method) {
        case POST:
            return new RestPostAction(identifier, href, fields);
        case DELETE:
            return new RestDeleteAction(identifier, href, fields);
        case GET:
            return new RestGetAction(identifier, href, fields);
        case PUT:
            return new RestPutAction(identifier, href, fields);
        default:
            return null;
        }
    }
}
