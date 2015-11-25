package au.com.windyroad.hateoas2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableSet;

import au.com.windyroad.hateoas.annotations.Rel;

@JsonPropertyOrder({ "class", "properties", "entities", "actions", "links",
        "title" })
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonSerialize(as = ResolvedEntity.class)
public class ResolvedEntity extends Entity {

    Properties properties = new Properties();

    @JsonProperty("links")
    private Set<NavigationalRelationship> navigationalRelationships = new HashSet<>();

    @JsonProperty("entities")
    private Set<EntityRelationship> entityRelationships = new HashSet<>();

    private Map<String, Action> actions = new HashMap<>();

    public ResolvedEntity() {
    }

    public ResolvedEntity(String... args) {
        super(args);
        add(new NavigationalRelationship(new JavaLink(this, (Object[]) args),
                Rel.SELF));
    }

    @Override
    public Action getAction(String identifier) {
        return actions.get(identifier);
    }

    @JsonProperty("actions")
    // @JsonSerialize(using = ActionAwareSerializer.class)
    public ImmutableSet<Action> getActions() {
        return ImmutableSet.copyOf(actions.values());
    }

    protected void setActions(Action[] actions) {
        for (Action action : actions) {
            this.actions.put(action.getIdentifier(), action);
        }
    }

    @Override
    @JsonProperty("entities")
    public ImmutableSet<EntityRelationship> getEntities() {
        return ImmutableSet.copyOf(entityRelationships);
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public ImmutableSet<NavigationalRelationship> getLinks() {
        return ImmutableSet.copyOf(navigationalRelationships);
    }

    public void addEntity(EntityRelationship entityRelationship) {
        entityRelationships.add(entityRelationship);
    }

    protected void add(Action action) {
        this.actions.put(action.getIdentifier(), action);
    }

    public void add(NavigationalRelationship navigationalRelationship) {
        navigationalRelationships.add(navigationalRelationship);
    }

    @Override
    public ResolvedEntity resolve(Class<? extends ResolvedEntity> type) {
        return this;
    }
}
