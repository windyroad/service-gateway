package au.com.windyroad.hateoas;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

public class JavaLink extends Link {

    private Entity<?> entity;
    private Object invocationValue;

    protected JavaLink() {

    }

    public JavaLink(Entity<?> entity, Object invocationValue) {
        super(invocationValue);
        this.invocationValue = invocationValue;
        this.entity = entity;
    }

    @Override
    public URI getHref() {
        URI uri = invocationValue == null ? null
                : ControllerLinkBuilder.linkTo(invocationValue).toUri();
        return uri;
    }

    @Override
    public <T extends Entity<?>> T follow(Class<T> type) {
        return (T) entity;
    }

    @Override
    public <T extends Entity<?>> T follow(ParameterizedTypeReference<T> type) {
        return (T) entity;
    }

}
