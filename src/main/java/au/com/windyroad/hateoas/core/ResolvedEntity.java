package au.com.windyroad.hateoas.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableSet;

import au.com.windyroad.hateoas.annotations.Label;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.hateoas.server.annotations.HateoasChildren;

@JsonPropertyOrder({ "class", "properties", "entities", "actions", "links",
        "title" })
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonSerialize(as = ResolvedEntity.class)
public class ResolvedEntity<T> extends Entity<T> {

    T properties;

    @JsonProperty("links")
    private Set<NavigationalRelationship> navigationalRelationships = new HashSet<>();

    private Map<String, Action> actions = new HashMap<>();

    public ResolvedEntity() {
    }

    public ResolvedEntity(T properties, String... args) {
        super(args);
        this.properties = properties;
        add(new NavigationalRelationship(
                new JavaLink(properties, (Object[]) args), Relationship.SELF));
        for (Method method : properties.getClass().getMethods()) {
            if (method.getAnnotation(HateoasAction.class) != null) {
                add(new JavaAction(method, (Object[]) args));
            }
        }
        getNatures().add(properties.getClass().getSimpleName());

        Label titleAnnotation = properties.getClass()
                .getAnnotation(Label.class);
        if (titleAnnotation != null) {
            setTitle(titleAnnotation.value(), args);
        }
    }

    @Override
    public Action getAction(String identifier) {
        return actions.get(identifier);
    }

    @JsonProperty("actions")
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
    public ImmutableSet<EntityRelationship<?>> getEntities()
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Set<EntityRelationship<?>> entityRelationships = new HashSet<>();
        if (properties != null) {
            for (Method method : properties.getClass().getMethods()) {
                HateoasChildren hateoasChildren = method
                        .getAnnotation(HateoasChildren.class);
                if (hateoasChildren != null
                        && method.getParameterTypes().length == 0) {
                    entityRelationships
                            .addAll((Collection<EntityRelationship<?>>) method
                                    .invoke(properties));
                }
            }
        }
        return ImmutableSet.copyOf(entityRelationships);
    }

    public void setEntities(
            Collection<EntityRelationship<?>> entityRelationships)
                    throws IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException {
        if (properties != null) {
            for (Method method : properties.getClass().getMethods()) {
                HateoasChildren hateoasChildren = method
                        .getAnnotation(HateoasChildren.class);
                if (hateoasChildren != null
                        && method.getParameterTypes().length != 0) {
                    List<EntityRelationship<?>> entityRels = entityRelationships
                            .stream()
                            .filter(e -> Arrays.asList(e.getNature())
                                    .contains(hateoasChildren.value()))
                            .collect(Collectors.toList());
                    method.invoke(properties, entityRels);
                }
            }
        }
    }

    @Override
    public T getProperties() {
        return properties;
    }

    @Override
    public ImmutableSet<NavigationalRelationship> getLinks() {
        return ImmutableSet.copyOf(navigationalRelationships);
    }

    protected void add(Action action) {
        this.actions.put(action.getIdentifier(), action);
    }

    public void add(NavigationalRelationship navigationalRelationship) {
        navigationalRelationships.add(navigationalRelationship);
    }

    @Override
    public <M, K extends ResolvedEntity<M>> K resolve(Class<K> type) {
        return (K) this;
    }

    @Override
    public LinkedEntity<T> toLinkedEntity() {
        return new LinkedEntity<T>(getLink(Relationship.SELF).getAddress(),
                getNatures(), getLabel());
    }

    @Override
    public <M, K extends ResolvedEntity<M>> K resolve(
            ParameterizedTypeReference<K> type) {
        return (K) this;
    }
}
