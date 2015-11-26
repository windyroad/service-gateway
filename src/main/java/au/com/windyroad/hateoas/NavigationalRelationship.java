package au.com.windyroad.hateoas;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class NavigationalRelationship extends Relationship {

    private Link link;

    private NavigationalRelationship() {
    }

    public NavigationalRelationship(Link link, String... natures) {
        super(natures);
        this.link = link;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
        bpp.processInjection(this.link);
    }

    @JsonCreator
    public NavigationalRelationship(@JsonProperty("href") URI address,
            @JsonProperty("rel") String... natures) {
        super(natures);
        this.link = new RestLink(address, null, null);
    }

    @JsonUnwrapped
    public Link getLink() {
        return this.link;
    }

}
