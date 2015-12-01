package au.com.windyroad.hateoas.core;

import java.net.URI;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LinkedEntity<T> extends Entity<T> {
    private Link link;
    private RestTemplate restTemplate;

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
        bpp.processInjection(this.link);
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
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
    public <K> ResolvedEntity<K> resolve(Class<ResolvedEntity<K>> type) {
        return link.resolve(type);
    }

    @Override
    public <K> ResolvedEntity<K> resolve(
            ParameterizedTypeReference<ResolvedEntity<K>> type) {
        return link.resolve(type);
    }

    @JsonIgnore
    public Link getLink() {
        return link;
    }

    @JsonProperty("href")
    public URI getAddress() {
        return link.getAddress();
    }

    ParameterizedTypeReference<ResolvedEntity<T>> getType() {
        ParameterizedTypeReference<ResolvedEntity<T>> type = new ParameterizedTypeReference<ResolvedEntity<T>>() {
        };
        return type;
    }

    @Override
    public LinkedEntity<T> toLinkedEntity() {
        return this;
    }
}
