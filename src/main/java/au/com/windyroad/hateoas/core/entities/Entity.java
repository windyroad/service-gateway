package au.com.windyroad.hateoas.core.entities;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.core.Labelled;

public abstract class Entity extends Labelled {

    public Entity() {
    }

    public Entity(Entity entity) {
        super(entity);
    }

    public Entity(String label) {
        super(label);
    }

    public abstract <K, T extends EntityWrapper<K>> T resolve(Class<T> type);

    public abstract <K, T extends EntityWrapper<K>> T resolve(
            ParameterizedTypeReference<T> type);

    @JsonIgnore
    public abstract LinkedEntity toLinkedEntity();

    public abstract URI getAddress() throws URISyntaxException;

    public abstract <K, T extends EntityWrapper<K>> T reload(Class<T> type);

}
