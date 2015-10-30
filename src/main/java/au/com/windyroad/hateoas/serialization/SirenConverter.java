package au.com.windyroad.hateoas.serialization;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.io.RuntimeIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.fasterxml.jackson.databind.util.StdConverter;

import au.com.windyroad.hateoas.Action;
import au.com.windyroad.hateoas.Entity;
import au.com.windyroad.hateoas.HttpLink;
import au.com.windyroad.hateoas.annotations.Rel;
import au.com.windyroad.hateoas.annotations.SirenAction;
import au.com.windyroad.hateoas.annotations.SirenEntity;
import au.com.windyroad.hateoas.annotations.SirenLink;
import au.com.windyroad.hateoas.annotations.SirenProperty;
import au.com.windyroad.hateoas.annotations.Title;

public class SirenConverter
        extends StdConverter<Object, Entity<Map<String, Object>>> {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // @Autowired
    // ObjectMapper objectMapper;

    @Override
    public Entity<Map<String, Object>> convert(Object value) {
        Map<String, Object> properties = new HashMap<>();
        Entity<Map<String, Object>> entity = new Entity<Map<String, Object>>(
                new HashMap<>());
        try {
            for (Method method : value.getClass().getMethods()) {
                if (method.getAnnotation(SirenProperty.class) != null) {
                    properties.put(getPropertyName(method),
                            method.invoke(value));
                } else if (method.getAnnotation(SirenEntity.class) != null) {
                    for (Object subentity : ((Collection) method
                            .invoke(value))) {
                        entity.addEmbeddedEntity((Entity<?>) subentity, null);
                    }
                } else if (method.getAnnotation(SirenAction.class) != null) {
                    SirenAction sirenActionAnnotation = method
                            .getAnnotation(SirenAction.class);

                    entity.addAction(
                            new Action(sirenActionAnnotation.controller()
                                    .getMethod(sirenActionAnnotation.method(),
                                            method.getParameterTypes())));
                } else if (method.getAnnotation(SirenLink.class) != null) {
                    SirenLink sirenLinkAnnotation = method
                            .getAnnotation(SirenLink.class);

                    Method controllerMethod = sirenLinkAnnotation.controller()
                            .getMethod(sirenLinkAnnotation.method(),
                                    method.getParameterTypes());
                    URI href = ControllerLinkBuilder
                            .linkTo(controllerMethod.getDeclaringClass(),
                                    controllerMethod)
                            .toUri();

                    entity.addLink(new HttpLink(href,
                            method.getAnnotation(Rel.class).value()));
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            throw new RuntimeIOException(e);
        }
        entity.setProperties(properties);

        Title title = value.getClass().getAnnotation(Title.class);
        if (title == null) {

        }

        return entity;
    }

    private String getPropertyName(Method method) {
        String name = method.getAnnotation(SirenProperty.class).name();
        if ("".equals(name)) {
            name = method.getName().replace("get", "");
            name = name.replace(name.substring(0, 1),
                    name.substring(0, 1).toLowerCase());
        }
        return name;
    }

}
