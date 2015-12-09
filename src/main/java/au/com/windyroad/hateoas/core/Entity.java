package au.com.windyroad.hateoas.core;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Entity extends Titled {

    public Entity() {
    }

    public Entity(String title) {
        super(title);
    }

    public abstract <K, T extends EntityWrapper<K>> T resolve(Class<T> type);

    public abstract <K, T extends EntityWrapper<K>> T resolve(
            ParameterizedTypeReference<T> type);

    @JsonIgnore
    public abstract LinkedEntity toLinkedEntity();

    public abstract URI getAddress() throws URISyntaxException;

}
