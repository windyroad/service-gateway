package au.com.windyroad.hateoas2;

import java.util.Properties;

import com.google.common.collect.ImmutableSet;

public abstract class Entity extends Resolvable {

    public abstract Properties getProperties();

    public abstract Action getAction(String identifier);

    public abstract ImmutableSet<NavigationalRelationship> getLinks();

    public abstract ImmutableSet<EntityRelationship> getEntities();
}
