package au.com.windyroad.hateoas;

import java.lang.reflect.Method;
import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.core.DummyInvocationUtils.MethodInvocation;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import au.com.windyroad.hateoas.client.LinkVisitor;

public class HttpLink extends Link {
    URI href;

    RestTemplate restTemplate;

    protected HttpLink() {
    }

    public HttpLink(URI href, String... rel) {
        super(rel);
        this.href = href;
    }

    public HttpLink(MethodInvocation invocation) {
        super(invocation);
        this.href = ControllerLinkBuilder.linkTo(invocation.getTargetType(),
                invocation.getMethod(), invocation.getArguments()).toUri();
    }

    public HttpLink(Method method, Object... pathParams) {
        this.href = ControllerLinkBuilder
                .linkTo(method.getDeclaringClass(), method, pathParams).toUri();
    }

    @Override
    public URI getHref() {
        return href;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T extends Entity<?>> T follow(Class<T> type) {
        RequestEntity<?> requestEntity = RequestEntity.get(href).build();
        return restTemplate.exchange(requestEntity, type).getBody();
    }

    @Override
    public <T extends Entity<?>> T follow(ParameterizedTypeReference<T> type) {
        RequestEntity<?> requestEntity = RequestEntity.get(href).build();
        return restTemplate.exchange(requestEntity, type).getBody();
    }

    @Override
    public void accept(LinkVisitor linkVisitor) {
        linkVisitor.visit(this);
    }

}
