package au.com.windyroad.hateoas.core;

import java.net.URI;
import java.util.Set;

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

    public LinkedEntity(@JsonProperty("href") URI address,
            @JsonProperty("class") Set<String> natures,
            @JsonProperty("title") String label) {
        this.link = new RestLink(address, natures, label);
        setNatures(natures);
        setLabel(label);
    }

    public LinkedEntity(Link link, Set<String> natures, String label) {
        this.link = link;
        setNatures(natures);
        setLabel(label);
    }

    @Override
    public <K, T extends ResolvedEntity<K>> T resolve(Class<T> type) {
        return link.resolve(type);
    }

    @Override
    public <K, T extends ResolvedEntity<K>> T resolve(
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
}
