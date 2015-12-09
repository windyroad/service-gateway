package au.com.windyroad.hateoas.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.hateoas.mvc.BasicLinkBuilder;
import org.springframework.http.HttpMethod;

import au.com.windyroad.hateoas.annotations.PresentationType;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;

public class JavaAction extends Action {

    private Method method;
    private Object[] pathParameters;
    private Object javaController;
    private String path;

    protected JavaAction() {
    }

    public JavaAction(Object controller, String path, Method method,
            Object... pathParameters) {
        super(method.getName(), extractParameters(method));
        this.javaController = controller;
        this.method = method;
        this.pathParameters = pathParameters;
        this.path = path;
    }

    private static au.com.windyroad.hateoas.core.Parameter[] extractParameters(
            Method method) {
        List<Parameter> params = Arrays.asList(method.getParameters());

        List<au.com.windyroad.hateoas.core.Parameter> rval = new ArrayList<>();
        for (int i = 0; i < params.size(); ++i) {
            rval.add(new au.com.windyroad.hateoas.core.Parameter(
                    params.get(i).getName()));
        }
        rval.add(new au.com.windyroad.hateoas.core.Parameter("trigger",
                PresentationType.SUBMIT, method.getName()));
        return rval.toArray(new au.com.windyroad.hateoas.core.Parameter[0]);
    }

    @Override
    public <T extends EntityWrapper<?>> Entity invoke(
            Map<String, String> context) throws IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        List<Object> args = new ArrayList<>(getParameters().size());
        for (au.com.windyroad.hateoas.core.Parameter param : getParameters()) {
            if (!PresentationType.SUBMIT.equals(param.getType())) {
                args.add(context.get(param.getIdentifier()));
            }
        }
        return (Entity) method.invoke(javaController, args.toArray());

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
    public URI getAddress() throws NoSuchMethodException, SecurityException,
            URISyntaxException {
        return BasicLinkBuilder.linkToCurrentMapping().slash(path).toUri();
    }

}
