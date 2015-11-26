package au.com.windyroad.hateoas;

import java.net.URI;
import java.util.Properties;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ImmutableSet;

public class LinkedEntity extends Entity {
    private Link link;

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
        bpp.processInjection(this.link);
    }

    public LinkedEntity() {
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
    public ResolvedEntity resolve(Class<? extends ResolvedEntity> type) {
        return link.resolve(type);
    }

    @JsonUnwrapped
    public Link getLink() {
        return link;
    }

    @Override
    @JsonIgnore
    public Action getAction(String identifier) {
        return resolve(ResolvedEntity.class).getAction(identifier);
    }

    @Override
    @JsonIgnore
    public Properties getProperties() {
        return resolve(ResolvedEntity.class).getProperties();
    }

    @Override
    @JsonIgnore
    public ImmutableSet<EntityRelationship> getEntities() {
        return resolve(ResolvedEntity.class).getEntities();
    }

    @Override
    @JsonIgnore
    public ImmutableSet<NavigationalRelationship> getLinks() {
        return resolve(ResolvedEntity.class).getLinks();
    }
}
