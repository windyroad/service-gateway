package au.com.windyroad.hateoas;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "name", "class", "type", "value", "title" })
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Field {

    private String name;
    @JsonProperty("class")
    private List<String> classes;
    private String type = "text";
    @Nullable
    private String value;
    @Nullable
    private String title;
    @Nullable
    private String validation;

    protected Field() {
    }

    public Field(String type, String validation) {
        this.type = type;
        this.validation = validation;
    }

    public String getType() {
        return type;
    }

    public String getValidation() {
        return validation;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the classes
     */
    public List<String> getClasses() {
        return classes;
    }

    /**
     * @param classes
     *            the classes to set
     */
    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param validation
     *            the validation to set
     */
    public void setValidation(String validation) {
        this.validation = validation;
    }

}
