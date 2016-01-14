package au.com.windyroad.hateoas.core;

import java.net.URI;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkedEntity extends Entity {
    private Link link;

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
        bpp.processInjection(this.link);
    }

    public LinkedEntity(Link link) {
        this.link = link;
    }

    public LinkedEntity(@JsonProperty("href") URI address,
            @JsonProperty("title") String label,
            @JsonProperty("class") Set<String> natures) {
        this.link = new RestLink(address, natures, label);
        setNatures(natures);
        setTitle(label);
    }

    public LinkedEntity(Link link, String label, Set<String> natures) {
        this.link = link;
        setNatures(natures);
        setTitle(label);
    }

    @Override
    public <K, T extends EntityWrapper<K>> T resolve(Class<T> type) {
        return link.resolve(type);
    }

    @Override
    public <K, T extends EntityWrapper<K>> T resolve(
            ParameterizedTypeReference<T> type) {
        return link.resolve(type);
    }

    @JsonIgnore
    public Link getLink() {
        return link;
    }

    @Override
    @JsonProperty("href")
    public URI getAddress() {
        return link.getAddress();
    }

    @Override
    public LinkedEntity toLinkedEntity() {
        return this;
    }

    @Override
    public <K, T extends EntityWrapper<K>> T reload(Class<T> type) {
        throw new NotImplementedException("TODO");
    }
}
