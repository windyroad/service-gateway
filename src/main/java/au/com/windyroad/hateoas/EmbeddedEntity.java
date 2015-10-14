package au.com.windyroad.hateoas;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public interface EmbeddedEntity {

    /**
     * @return the rel
     */
    public String[] getRel();

    public <T extends Entity<?>> T toEntity(Class<T> type);

    public <T extends Entity<?>> T toEntity(ParameterizedTypeReference<T> type);

}
