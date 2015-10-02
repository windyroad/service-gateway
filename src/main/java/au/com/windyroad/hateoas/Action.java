package au.com.windyroad.hateoas;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.Collection;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

import au.com.windyroad.hateoas.annotations.Name;
import au.com.windyroad.hateoas.serialization.MediaTypeDeserializer;
import au.com.windyroad.hateoas.serialization.MediaTypeSerializer;

@JsonPropertyOrder({ "name", "class", "method", "href", "title", "fields" })
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Action {

    private static final long serialVersionUID = 1L;

    private String name;
    @JsonProperty("class")
    @Nullable
    private List<String> classes;
    private RequestMethod method = RequestMethod.GET;
    private URI href;
    @Nullable
    private String title;
    @JsonDeserialize(using = MediaTypeDeserializer.class)
    @JsonSerialize(using = MediaTypeSerializer.class)
    private MediaType type = MediaType.APPLICATION_FORM_URLENCODED;

    @Nullable
    private UnifiedSetWithHashingStrategy<Field> fields = new UnifiedSetWithHashingStrategy<>(
            HashingStrategies.fromFunction(Field::getName));

    public Action(Method method, Object... pathParams)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException,
            SecurityException {
        this.href = ControllerLinkBuilder
                .linkTo(method.getDeclaringClass(), method, pathParams).toUri();
        this.name = method.getAnnotation(Name.class).value();
        Parameter[] methodParams = method.getParameters();
        this.method = method.getAnnotation(RequestMapping.class).method()[0];
        for (Parameter param : methodParams) {
            RequestParam requestParam = param.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                addField(new Field(method, param));
            }
        }
    }

    protected Action() {
    }

    public Action(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public URI getHref() {
        return this.href;
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
     * @return the method
     */
    public RequestMethod getMethod() {
        return method;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setMethod(RequestMethod method) {
        this.method = method;
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
     * @return the type
     */
    public MediaType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(MediaType type) {
        this.type = type;
    }

    // public void setType(String type) {
    // this.type = new MediaType(type);
    // }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param href
     *            the href to set
     */
    public void setHref(URI href) {
        this.href = href;
    }

    public Collection<Field> getFields() {
        return fields;
    }

    public void setFields(Collection<Field> fields) {
        this.fields = new UnifiedSetWithHashingStrategy<>(
                HashingStrategies.fromFunction(Field::getName), fields);
    }

    public void addField(Field field) {
        this.fields.add(field);
    }

    public Field getField(String name) {
        return this.fields.get(new Field(name));
    }
}
