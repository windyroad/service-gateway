package au.com.windyroad.hateoas.core;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class EntityRelationship extends Relationship {

    private Entity entity;

    protected EntityRelationship() {
    }

    public EntityRelationship(Entity entity, String... natures) {
        super(natures);
        this.entity = entity;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
        bpp.processInjection(this.entity);
    }

    @JsonCreator
    public EntityRelationship(@JsonProperty("href") URI address,
            @JsonProperty("title") String label,
            @JsonProperty("rel") String... natures) {
        super(natures);
        this.entity = new LinkedEntity(address, label, null);
    }

    public EntityRelationship(Link link, String label, String[] natures) {
        super(natures);
        this.entity = new LinkedEntity(link, label, null);
    }

    /**
     * @return the entity
     */
    @JsonIgnore
    public Entity getEntity() {
        return entity;
    }

    @JsonUnwrapped
    public LinkedEntity getEntityLink() {
        return entity.toLinkedEntity();
    }
}
