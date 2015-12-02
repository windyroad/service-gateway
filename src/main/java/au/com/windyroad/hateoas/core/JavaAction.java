package au.com.windyroad.hateoas.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.com.windyroad.hateoas.annotations.PresentationType;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;

public class JavaAction extends Action {

    private Method method;
    private Object[] pathParameters;
    private Object entity;

    protected JavaAction() {
    }

    public JavaAction(Object entity, Method method, Object... pathParameters) {
        super(method.getName(), extractParameters(method));
        this.method = method;
        this.pathParameters = pathParameters;
        this.entity = entity;
    }

    private static au.com.windyroad.hateoas.core.Parameter[] extractParameters(
            Method method) {
        List<Parameter> params = Arrays.asList(method.getParameters()).stream()
                .filter(p -> p.getAnnotation(RequestParam.class) != null)
                .collect(Collectors.toList());

        au.com.windyroad.hateoas.core.Parameter[] rval = new au.com.windyroad.hateoas.core.Parameter[params
                .size() + 1];
        for (int i = 0; i < params.size(); ++i) {
            RequestParam requestParamAnnotation = params.get(i)
                    .getAnnotation(RequestParam.class);
            rval[i] = new au.com.windyroad.hateoas.core.Parameter(
                    requestParamAnnotation.value());
        }
        rval[params.size()] = new au.com.windyroad.hateoas.core.Parameter(
                "trigger", PresentationType.SUBMIT, method.getName());
        return rval;
    }

    @Override
    public <T extends ResolvedEntity<?>> Entity invoke(
            Map<String, String> context) throws IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        List<Object> args = new ArrayList<>(getParameters().size());
        for (au.com.windyroad.hateoas.core.Parameter param : getParameters()) {
            if (!PresentationType.SUBMIT.equals(param.getType())) {
                args.add(context.get(param.getIdentifier()));
            }
        }
        return (Entity) method.invoke(entity, args.toArray());

    }

    @Override
    public HttpMethod getNature() {
        if (method != null) {
            return method.getAnnotation(HateoasAction.class).nature();
        } else {
            return null;
        }
    }

    @Override
    public URI getAddress() throws NoSuchMethodException, SecurityException {
        if (method != null) {
            Class<?> controller = method.getAnnotation(HateoasAction.class)
                    .controller();
            URI uri = ControllerLinkBuilder.linkTo(controller, pathParameters)
                    .toUri();
            return uri;
        } else {
            return null;
        }
    }

}
