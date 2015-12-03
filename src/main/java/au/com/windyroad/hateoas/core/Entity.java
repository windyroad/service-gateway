package au.com.windyroad.hateoas.core;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Entity extends Resolvable {

    public Entity(String... args) {
        super(args);
    }

    public abstract <K, T extends ResolvedEntity<K>> T resolve(Class<T> type);

    public abstract <K, T extends ResolvedEntity<K>> T resolve(
            ParameterizedTypeReference<T> type);

    @JsonIgnore
    public abstract LinkedEntity toLinkedEntity();

    public abstract URI getAddress();

}
