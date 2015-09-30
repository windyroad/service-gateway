package au.com.windyroad.hateoas;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class EmbeddedEntityRepresentation<T> extends Entity<T>
        implements EmbeddedEntity<T> {

    private String[] rel;

    public EmbeddedEntityRepresentation(T properties, String rel) {
        super(properties);
    }

    /**
     * @return the rel
     */
    @Override
    public String[] getRel() {
        return rel;
    }

    /**
     * @param rel
     *            the rel to set
     */
    public void setRel(String[] rel) {
        this.rel = rel;
    }

    @Override
    public Entity<T> toEntity() {
        return this;
    }
}
