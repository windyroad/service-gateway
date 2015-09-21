package au.com.windyroad.hateoas;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({ "name", "class", "type", "value", "title" })
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Field {

    private String name;
    @JsonProperty("class")
    private List<String> classes;

    // the default is TEXT as per
    // http://www.w3.org/TR/html51/semantics.html#the-input-element on
    // 2015/09/21
    private String type = PresentationType.TEXT;
    @Nullable
    private String value;
    @Nullable
    private String title;
    @Nullable
    private String validation;

    protected Field() {

    }

    public Field(String name, String type, String validation) {
        this.name = name;
        this.type = type;
        this.validation = validation;
    }

    public Field(String name) {
        this.name = name;
    }

    public Field(Method method, Parameter param) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        RequestParam requestParam = param.getAnnotation(RequestParam.class);
        this.name = requestParam.value();

        Validation validation = param.getAnnotation(Validation.class);
        String validationMethodName = validation.value();
        this.validation = (String) method.getDeclaringClass()
                .getMethod(validationMethodName).invoke(null);

        PresentationType type = param.getAnnotation(PresentationType.class);
        if (type != null) {
            this.type = type.value();
        }

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
