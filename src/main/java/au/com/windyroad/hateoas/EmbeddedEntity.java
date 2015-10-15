package au.com.windyroad.hateoas;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonDeserialize(as = EmbeddedEntityHttpLink.class)
public interface EmbeddedEntity {

    /**
     * @return the rel
     */
    public String[] getRel();

    public <T extends Entity<?>> T toEntity(Class<T> type);

    public <T extends Entity<?>> T toEntity(ParameterizedTypeReference<T> type);

}
