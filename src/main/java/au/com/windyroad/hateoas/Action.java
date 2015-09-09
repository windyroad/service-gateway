package au.com.windyroad.hateoas;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Action {

    private static final long serialVersionUID = 1L;

    private String name;

    @JsonProperty("href")
    private URI href;

    @JsonProperty("method")
    private RequestMethod[] method;

    @JsonProperty("params")
    private Map<String, Param> params = new HashMap<>();

    public Action(Method method, Object... pathParams)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException,
            SecurityException {
        this.href = ControllerLinkBuilder
                .linkTo(method.getDeclaringClass(), method, pathParams).toUri();
        this.name = method.getAnnotation(Rel.class).value();
        Parameter[] methodParams = method.getParameters();
        this.method = method.getAnnotation(RequestMapping.class).method();
        for (Parameter param : methodParams) {
            RequestParam requestParam = param.getAnnotation(RequestParam.class);
            if (requestParam != null) {
                Validation validation = param.getAnnotation(Validation.class);
                Class<?> type = param.getType();
                String validationMethodName = validation.value();
                String validator = (String) method.getDeclaringClass()
                        .getMethod(validationMethodName).invoke(null);
                params.put(requestParam.value(), new Param(type, validator));
            }
        }
    }

    protected Action() {
    }

    public String getName() {
        return this.name;
    }

    public URI getHref() {
        return this.href;
    }

    public Map<String, Param> getParams() {
        return this.params;
    }

}
