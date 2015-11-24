package au.com.windyroad.hateoas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.hateoas.core.DummyInvocationUtils.LastInvocationAware;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

import au.com.windyroad.hateoas.annotations.Title;
import au.com.windyroad.hateoas.serialization.MessageSourceAwareSerializer;

@JsonPropertyOrder({ "class", "properties", "entities", "actions", "links",
        "title" })
// @JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Entity<T> {

    @Nullable
    @JsonProperty("class")
    private String[] classes;

    @Nullable
    private T properties;

    @Nullable
    private List<EmbeddedEntity> entities = new ArrayList<>();

    @Nullable
    private Multimap<String, OldLink> links = HashMultimap.create();

    @Nullable
    private UnifiedSetWithHashingStrategy<Action> actions = new UnifiedSetWithHashingStrategy<>(
            HashingStrategies.fromFunction(Action::getName));

    @Nullable
    private String title;

    protected Entity() {
        this.classes = new String[] { this.getClass().getSimpleName() };
        Title titleAnnotation = this.getClass().getAnnotation(Title.class);
        if (titleAnnotation != null) {
            title = titleAnnotation.value();
        }
    }

    public Entity(T properties) {
        this();
        this.properties = properties;
    }

    public Entity(T properties, Collection<Action> actions) {
        this(properties);
        setActions(actions);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @return the classes
     */
    public String[] getClasses() {
        return classes;
    }

    /**
     * @param classes
     *            the classes to set
     */
    public void setClasses(String[] classes) {
        this.classes = classes;
    }

    /**
     * @return the properties
     */
    public T getProperties() {
        return properties;
    }

    /**
     * @param properties
     *            the properties to set
     */
    public void setProperties(T properties) {
        this.properties = properties;
    }

    /**
     * @return the links
     */
    public Collection<OldLink> getLinks() {
        return links.values().stream().collect(Collectors.toSet());
    }

    /**
     * @param links
     *            the links to set
     */
    public void setLinks(Collection<OldLink> links) {
        this.links.clear();
        for (OldLink link : links) {
            addLink(link);
        }
    }

    /**
     * @return the actions
     */
    public Collection<Action> getActions() {
        return actions;
    }

    public void setActions(Collection<Action> actions) {
        this.actions = new UnifiedSetWithHashingStrategy<>(
                HashingStrategies.fromFunction(Action::getName), actions);
    }

    public void addAction(Action action) {
        this.actions.add(action);
    }

    public Action getAction(String name) {
        return this.actions.get(new Action(name));
    }

    public void addLink(OldLink link) {
        // TODO: have multiple rels for a single link rather than creating
        // multiple links for each rel.
        for (String rel : link.getRel()) {
            this.links.put(rel, link);
        }
    }

    public Collection<OldLink> getLink(String rel) {
        return this.links.get(rel);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the title
     */

    @JsonSerialize(using = MessageSourceAwareSerializer.class)
    public String getTitle() {
        return title;
    }

    /**
     * @return the entities
     */
    public List<EmbeddedEntity> getEntities() {
        return entities;
    }

    /**
     * @param entities
     *            the entities to set
     */
    public void setEntities(List<EmbeddedEntity> entities) {
        this.entities = entities;
    }

    public EmbeddedEntityLink addEmbeddedEntity(Entity<?> entity,
            Object invocation) {
        EmbeddedEntityJavaLink<?> link = new EmbeddedEntityJavaLink<>(entity,
                ((LastInvocationAware) invocation).getLastInvocation());
        if (entities.add(link)) {
            return link;
        } else {
            return null;
        }
    }

}
