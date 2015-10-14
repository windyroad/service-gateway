package au.com.windyroad.hateoas;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class EmbeddedEntityRepresentation<T> extends Entity<T>
        implements EmbeddedEntity {

    private String[] rel;

    protected EmbeddedEntityRepresentation() {

    }

    public EmbeddedEntityRepresentation(Entity<T> properties, String... rel) {
        this.rel = rel;
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
    public <T extends Entity<?>> T toEntity(Class<T> type) {
        return null;
    }

    @Override
    public <T extends Entity<?>> T toEntity(
            ParameterizedTypeReference<T> type) {
        return null;
    }
}
