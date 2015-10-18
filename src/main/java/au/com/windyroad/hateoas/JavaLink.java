package au.com.windyroad.hateoas;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.core.DummyInvocationUtils.LastInvocationAware;
import org.springframework.hateoas.core.DummyInvocationUtils.MethodInvocation;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import au.com.windyroad.hateoas.client.LinkVisitor;

public class JavaLink extends Link {

    private Entity<?> entity;
    private MethodInvocation invocation;

    protected JavaLink() {

    }

    public JavaLink(Entity<?> entity, Object invocationValue) {
        super(((LastInvocationAware) invocationValue).getLastInvocation());
        this.invocation = ((LastInvocationAware) invocationValue)
                .getLastInvocation();
        this.entity = entity;
    }

    @Override
    public URI getHref() {
        URI uri = invocation == null ? null
                : ControllerLinkBuilder.linkTo(invocation.getTargetType(),
                        invocation.getMethod(), invocation.getArguments())
                        .toUri();
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

    @Override
    public void accept(LinkVisitor linkVisitor) {
        linkVisitor.visit(this);
    }

}
