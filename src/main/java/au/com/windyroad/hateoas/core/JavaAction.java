package au.com.windyroad.hateoas.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpMethod;

import au.com.windyroad.hateoas.annotations.PresentationType;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.servicegateway.Repository;

public class JavaAction extends Action {

    private Method method;
    private Object[] pathParameters;
    private Object entity;
    private ApplicationContext context;
    private Repository repository;

    protected JavaAction() {
    }

    public JavaAction(ApplicationContext context, Repository repository,
            Object entity, Method method, Object... pathParameters) {
        super(method.getName(), extractParameters(method));
        this.context = context;
        this.repository = repository;
        this.method = method;
        this.pathParameters = pathParameters;
        this.entity = entity;
    }

    private static au.com.windyroad.hateoas.core.Parameter[] extractParameters(
            Method method) {
        List<Parameter> params = Arrays.asList(method.getParameters());

        List<au.com.windyroad.hateoas.core.Parameter> rval = new ArrayList<>();
        for (int i = 3; i < params.size(); ++i) {
            rval.add(new au.com.windyroad.hateoas.core.Parameter(
                    params.get(i).getName()));
        }
        rval.add(new au.com.windyroad.hateoas.core.Parameter("trigger",
                PresentationType.SUBMIT, method.getName()));
        return rval.toArray(new au.com.windyroad.hateoas.core.Parameter[0]);
    }

    @Override
    public <T extends ResolvedEntity<?>> Entity invoke(
            Map<String, String> context) throws IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        List<Object> args = new ArrayList<>(getParameters().size());
        args.add(this.context);
        args.add(repository);
        args.add(entity);

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
