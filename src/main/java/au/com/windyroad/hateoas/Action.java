package au.com.windyroad.hateoas;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Action {

    private static final long serialVersionUID = 1L;

    private String name;
    @JsonProperty("class")
    private List<String> classes;
    private RequestMethod method = RequestMethod.GET;
    private URI href;
    private String title;
    private MediaType type = MediaType.APPLICATION_FORM_URLENCODED;

    private Map<String, Field> fields = new HashMap<>();

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
                Validation validation = param.getAnnotation(Validation.class);
                String type = "text";// param.getAnnotation(PresentationType.class).value();
                String validationMethodName = validation.value();
                String validator = (String) method.getDeclaringClass()
                        .getMethod(validationMethodName).invoke(null);
                fields.put(requestParam.value(), new Field(type, validator));
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

    public Map<String, Field> getFields() {
        return this.fields;
    }

}
