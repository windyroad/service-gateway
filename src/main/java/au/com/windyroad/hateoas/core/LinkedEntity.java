package au.com.windyroad.hateoas.core;

import java.net.URI;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ImmutableSet;

public class LinkedEntity<T> extends Entity<T> {
    private Link link;

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
        bpp.processInjection(this.link);
    }

    public LinkedEntity(@JsonProperty("href") URI address,
            @JsonProperty("class") Set<String> natures,
            @JsonProperty("title") String label) {
        this.link = new RestLink(address, natures, label);
    }

    public LinkedEntity(Link link) {
        this.link = link;
    }

    @Override
    public <M, K extends ResolvedEntity<M>> K resolve(Class<K> type) {
        return link.resolve(type);
    }

    @Override
    public <M, K extends ResolvedEntity<M>> K resolve(
            ParameterizedTypeReference<K> type) {
        return link.resolve(type);
    }

    @JsonUnwrapped
    public Link getLink() {
        return link;
    }

    @Override
    @JsonIgnore
    public Action getAction(String identifier) {
        return resolve(getType()).getAction(identifier);
    }

    ParameterizedTypeReference<ResolvedEntity<T>> getType() {
        ParameterizedTypeReference<ResolvedEntity<T>> type = new ParameterizedTypeReference<ResolvedEntity<T>>() {
        };
        return type;
    }

    @Override
    @JsonIgnore
    public T getProperties() {
        ResolvedEntity<T> resolved = resolve(getType());
        return resolved.getProperties();
    }

    @Override
    @JsonIgnore
    public ImmutableSet<EntityRelationship<?>> getEntities() {
        return resolve(getType()).getEntities();
    }

    @Override
    @JsonIgnore
    public ImmutableSet<NavigationalRelationship> getLinks() {
        return resolve(getType()).getLinks();
    }

    @Override
    public LinkedEntity toLinkedEntity() {
        return this;
    }
}
