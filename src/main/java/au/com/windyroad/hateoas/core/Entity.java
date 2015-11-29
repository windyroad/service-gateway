package au.com.windyroad.hateoas.core;

import org.springframework.core.ParameterizedTypeReference;

import com.google.common.collect.ImmutableSet;

public abstract class Entity<T> extends Resolvable {

    public Entity(String... args) {
        super(args);
    }

    public abstract T getProperties();

    public abstract Action getAction(String identifier);

    public abstract ImmutableSet<NavigationalRelationship> getLinks();

    public abstract ImmutableSet<EntityRelationship<?>> getEntities();

    public Link getLink(String self) {
        return getLinks().stream().filter(l -> l.hasNature(Relationship.SELF))
                .findAny().get().getLink();
    }

    public abstract <M, K extends ResolvedEntity<M>> K resolve(Class<K> type);

    public abstract <M, K extends ResolvedEntity<M>> K resolve(
            ParameterizedTypeReference<K> type);

    public abstract LinkedEntity<T> toLinkedEntity();

}
