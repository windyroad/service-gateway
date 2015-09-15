package au.com.windyroad.hateoas;

import org.apache.commons.lang.NotImplementedException;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class EmbeddedEntityLink<T> extends Link implements EmbeddedEntity<T> {

    @Override
    public Entity<T> toEntity() {
        throw new NotImplementedException(
                "need's to get the link and return the corresponding entity");
    }

}
