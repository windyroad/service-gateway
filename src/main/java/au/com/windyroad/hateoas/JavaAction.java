package au.com.windyroad.hateoas;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestParam;

import au.com.windyroad.hateoas.annotations.PresentationType;

public class JavaAction extends Action {

    private Method method;

    protected JavaAction() {
    }

    public JavaAction(Method method) {
        super(method.getName(), extractParameters(method));
        this.method = method;
    }

    private static au.com.windyroad.hateoas.Parameter[] extractParameters(
            Method method) {
        Parameter[] params = method.getParameters();
        au.com.windyroad.hateoas.Parameter[] rval = new au.com.windyroad.hateoas.Parameter[params.length
                + 1];
        for (int i = 0; i < params.length; ++i) {
            rval[i] = new au.com.windyroad.hateoas.Parameter(
                    params[i].getAnnotation(RequestParam.class).value());
        }
        rval[params.length] = new au.com.windyroad.hateoas.Parameter("trigger",
                PresentationType.SUBMIT, method.getName());
        return rval;
    }

    @Override
    public <T extends Entity> Entity invoke(T entity,
            Map<String, String> context) throws IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        List<Object> args = new ArrayList<>(getParameters().size());
        for (au.com.windyroad.hateoas.Parameter param : getParameters()) {
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
            URI uri = ControllerLinkBuilder.linkTo(controller).toUri();
            return uri;
        } else {
            return null;
        }
    }

}
