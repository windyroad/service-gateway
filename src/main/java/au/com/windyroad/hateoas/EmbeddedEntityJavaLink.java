package au.com.windyroad.hateoas;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.core.DummyInvocationUtils.MethodInvocation;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import au.com.windyroad.hateoas.client.LinkVisitor;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonDeserialize(as = EmbeddedEntityHttpLink.class)
public class EmbeddedEntityJavaLink<K extends Entity<?>>
        extends EmbeddedEntityLink {

    private K entity;
    private MethodInvocation invocation;

    protected EmbeddedEntityJavaLink() {
    }

    public EmbeddedEntityJavaLink(K entity, MethodInvocation invocation) {
        super(invocation);
        this.entity = entity;
        this.invocation = invocation;
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
