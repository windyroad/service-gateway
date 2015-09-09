package au.com.windyroad.hateoas;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Field {

    private String name;
    @JsonProperty("class")
    private List<String> classes;
    private String type;
    private String value;
    private String title;
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

}
