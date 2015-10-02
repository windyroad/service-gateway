package au.com.windyroad.hateoas;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

@JsonPropertyOrder({ "class", "properties", "entities", "actions", "links",
        "title" })
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Entity<T> {

    @Nullable
    @JsonProperty("class")
    private String[] classes;

    @Nullable
    private T properties;

    @Nullable
    private List<EmbeddedEntity> entities = new ArrayList<>();

    @Nullable
    private Multimap<String, Link> links = HashMultimap.create();

    @Nullable
    private UnifiedSetWithHashingStrategy<Action> actions = new UnifiedSetWithHashingStrategy<>(
            HashingStrategies.fromFunction(Action::getName));

    @Nullable
    private String title;

    public Entity() {
    }

    public Entity(T properties) {
        this.classes = new String[] { properties.getClass().getSimpleName() };
        this.properties = properties;
    }

    public Entity(T properties, Collection<Action> actions) {
        this.classes = new String[] { properties.getClass().getSimpleName() };
        this.properties = properties;
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
    public Collection<Link> getLinks() {
        return links.values().stream().collect(Collectors.toSet());
    }

    /**
     * @param links
     *            the links to set
     */
    public void setLinks(Collection<Link> links) {
        this.links.clear();
        for (Link link : links) {
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

    public void addLink(Link link) {
        // TODO: have multiple rels for a single link rather than creating
        // multiple links for each rel.
        for (String rel : link.getRel()) {
            this.links.put(rel, link);
        }
    }

    public Collection<Link> getLink(String rel) {
        return this.links.get(rel);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the title
     */
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

    public void addEmbeddedEntity(EmbeddedEntity<?> embeddedEntity) {
        entities.add(embeddedEntity);
    }
}
