package au.com.windyroad.hateoas.core;

import java.util.Properties;

import com.google.common.collect.ImmutableSet;

public abstract class Entity extends Resolvable {

    public Entity(String... args) {
        super(args);
    }

    public abstract Properties getProperties();

    public abstract Action getAction(String identifier);

    public abstract ImmutableSet<NavigationalRelationship> getLinks();

    public abstract ImmutableSet<EntityRelationship> getEntities();

    public Link getLink(String self) {
        return getLinks().stream().filter(l -> l.hasNature(Relationship.SELF))
                .findAny().get().getLink();
    }

    public abstract ResolvedEntity resolve(
            Class<? extends ResolvedEntity> type);

    public abstract LinkedEntity toLinkedEntity();

}
