package au.com.windyroad.hateoas;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public interface EmbeddedEntity<T> {

    /**
     * @return the rel
     */
    public String[] getRel();

    public Entity<T> toEntity();

}
