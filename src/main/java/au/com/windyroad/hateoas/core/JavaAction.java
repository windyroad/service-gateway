package au.com.windyroad.hateoas.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.hateoas.mvc.BasicLinkBuilder;
import org.springframework.http.HttpMethod;

import au.com.windyroad.hateoas.annotations.PresentationType;

public class JavaAction<T> extends Action<T> {

    private Method method;
    private Object javaController;
    private EntityWrapper<?> entity;
    private HttpMethod nature;

    protected JavaAction() {
    }

    public JavaAction(EntityWrapper<?> entity, Object controller,
            Method method) {
        super(method.getName(), extractParameters(method));
        this.javaController = controller;
        this.method = method;
        this.entity = entity;
        this.nature = determineMethodNature(method);
    }

    public static HttpMethod determineMethodNature(Method method) {
        Type type = method.getGenericReturnType();
        if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            Class<? extends Type> rawTypeClass = rawType.getClass();
            if (Class.class.isAssignableFrom(rawTypeClass)
                    && CompletableFuture.class
                            .isAssignableFrom((Class<?>) rawType)) {
                Type[] typeParams = parameterizedType.getActualTypeArguments();
                if (typeParams.length == 1) {
                    Type typeParam = typeParams[0];
                    if (Class.class.isAssignableFrom(typeParam.getClass())
                            && Void.class
                                    .isAssignableFrom((Class<?>) typeParam)) {
                        return HttpMethod.DELETE;
                    } else if (Class.class
                            .isAssignableFrom(typeParam.getClass())
                            && CreatedLinkedEntity.class
                                    .isAssignableFrom((Class<?>) typeParam)) {
                        return HttpMethod.POST;
                    } else if (Class.class
                            .isAssignableFrom(typeParam.getClass())
                            && UpdatedLinkedEntity.class
                                    .isAssignableFrom((Class<?>) typeParam)) {
                        return HttpMethod.PUT;
                    } else if (ParameterizedType.class
                            .isAssignableFrom(typeParam.getClass())
                            && EntityWrapper.class.isAssignableFrom(
                                    (Class<?>) ((ParameterizedType) typeParam)
                                            .getRawType())) {
                        return HttpMethod.GET;
                    }
                }
            }
        }
        return null;
    }

    private static au.com.windyroad.hateoas.core.Parameter[] extractParameters(
            Method method) {
        List<Parameter> params = Arrays.asList(method.getParameters());

        List<au.com.windyroad.hateoas.core.Parameter> rval = new ArrayList<>();
        for (int i = 1; i < params.size(); ++i) {
            rval.add(new au.com.windyroad.hateoas.core.Parameter(
                    params.get(i).getName()));
        }
        rval.add(new au.com.windyroad.hateoas.core.Parameter("action",
                PresentationType.SUBMIT, method.getName()));
        return rval.toArray(new au.com.windyroad.hateoas.core.Parameter[0]);
    }

    @Override
    public CompletableFuture<T> invoke(Map<String, String> context) {
        List<Object> args = new ArrayList<>(getParameters().size() + 1);
        args.add(entity);
        for (au.com.windyroad.hateoas.core.Parameter param : getParameters()) {
            if (!PresentationType.SUBMIT.equals(param.getType())) {
                args.add(context.get(param.getIdentifier()));
            }
        }
        try {
            return (CompletableFuture<T>) method.invoke(javaController,
                    args.toArray());
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public HttpMethod getNature() {
        return nature;
    }

    @Override
    public URI getAddress() throws NoSuchMethodException, SecurityException,
            URISyntaxException {
        return BasicLinkBuilder.linkToCurrentMapping().slash(entity).toUri();
    }

}
