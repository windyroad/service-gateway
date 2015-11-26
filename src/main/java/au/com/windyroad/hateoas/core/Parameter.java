package au.com.windyroad.hateoas.core;

import org.eclipse.jdt.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

//@JsonPropertyOrder({ "class", "properties", "entities", "actions", "links",
//        "title" })
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Parameter {

    protected Parameter() {
    }

    public Parameter(@JsonProperty("name") String identifier) {
        this.identifier = identifier;
    }

    public Parameter(String identifier, String type, String value) {
        this(identifier);
        this.type = type;
        this.value = value;
    }

    private String identifier;

    private String[] natures = {};

    private String type;

    @Nullable
    private String value;

    @Nullable
    private String label;

    /**
     * @return the identifier
     */
    @JsonProperty("name")
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return the natures
     */
    @JsonProperty("class")
    public String[] getNatures() {
        return natures;
    }

    /**
     * @return the type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the label
     */
    @JsonProperty("title")
    public String getLabel() {
        return label;
    }

}
