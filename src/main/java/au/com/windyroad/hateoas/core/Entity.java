package au.com.windyroad.hateoas.core;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Entity extends Resolvable {

    public Entity(String... args) {
        super(args);
    }

    public abstract <K> ResolvedEntity<K> resolve(
            Class<ResolvedEntity<K>> type);

    public abstract <K> ResolvedEntity<K> resolve(
            ParameterizedTypeReference<ResolvedEntity<K>> type);

    @JsonIgnore
    public abstract LinkedEntity toLinkedEntity();

}
