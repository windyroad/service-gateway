package au.com.windyroad.hateoas.core;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import au.com.windyroad.hateoas.client.RestAction;

@JsonDeserialize(as = RestAction.class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public abstract class Action extends Titled {

    private String identifier;

    private List<Parameter> parameters = new ArrayList<>();

    protected Action() {
    }

    public Action(String identifier, Parameter... parameters) {
        this.identifier = identifier;
        this.parameters.addAll(Arrays.asList(parameters));
    }

    /**
     * @return the identifier
     */
    @JsonProperty("name")
    public String getIdentifier() {
        return identifier;
    }

    public abstract <T extends EntityWrapper<?>> Entity invoke(
            Map<String, String> context) throws IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException;

    /**
     * @return the nature
     */
    @JsonProperty("method")
    public abstract HttpMethod getNature();

    @JsonProperty("href")
    public abstract URI getAddress()
            throws NoSuchMethodException, SecurityException, URISyntaxException;

    /**
     * @return the parameters
     */
    @JsonProperty("fields")
    public List<Parameter> getParameters() {
        return parameters;
    }

}
