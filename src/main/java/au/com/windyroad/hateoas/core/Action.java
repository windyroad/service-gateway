package au.com.windyroad.hateoas.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

import au.com.windyroad.hateoas.client.RestActionBuilder;

@JsonDeserialize(builder = RestActionBuilder.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class Action<T> extends Titled {

    private Resolver resolver;

    private String identifier;

    private List<Parameter> parameters = new ArrayList<>();

    private Link link;

    protected Action() {
    }

    public Action(Resolver resolver, String identifier, Link link,
            Parameter... parameters) {
        this.identifier = identifier;
        this.parameters.addAll(Arrays.asList(parameters));
        this.link = link;
        this.resolver = resolver;
    }

    /**
     * @return the identifier
     */
    @JsonProperty("name")
    public String getIdentifier() {
        return identifier;
    }

    public CompletableFuture<T> invoke(Map<String, Object> context) {
        Set<String> parameterKeys = getParameterKeys();
        Map<String, Object> filteredParameters = new HashMap<>(
                Maps.filterKeys(context, Predicates.in(parameterKeys)));
        String id = getIdentifier();
        filteredParameters.put("action", id);
        return doInvoke(resolver, filteredParameters);
    }

    abstract protected CompletableFuture<T> doInvoke(Resolver resolver,
            Map<String, Object> filteredParameters);

    /**
     * @return the nature
     */
    @JsonProperty("method")
    public abstract HttpMethod getNature();

    @JsonProperty("href")
    public URI getAddress() {
        if (getLink() != null) {
            return getLink().getAddress();
        } else {
            return null;
        }
    }

    /**
     * @return the parameters
     */
    @JsonProperty("fields")
    public List<Parameter> getParameters() {
        return parameters;
    }

    @JsonIgnore
    public Link getLink() {
        return this.link;
    }

    public Set<String> getParameterKeys() {
        Set<String> rval = new HashSet<>();
        for (Parameter param : getParameters()) {
            rval.add(param.getIdentifier());
        }
        return rval;
    }

}
