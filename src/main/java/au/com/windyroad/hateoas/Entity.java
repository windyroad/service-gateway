package au.com.windyroad.hateoas;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

@JsonPropertyOrder({ "class", "properties", "entities", "actions", "links" })
public class Entity<T> {

    @JsonProperty("class")
    private String[] classes;

    private T properties;

    private List<SubEntity> entities;

    private Set<Link> links = new HashSet<>();

    private UnifiedSetWithHashingStrategy<Action> actions = new UnifiedSetWithHashingStrategy<>(
            HashingStrategies.fromFunction(Action::getName));

    public Entity() {
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

    public void addAction(Action action) {
        this.actions.add(action);
    }

    public Action getAction(String name) {
        return this.actions.get(new Action(name));
    }

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
    public Set<Link> getLinks() {
        return links;
    }

    /**
     * @param links
     *            the links to set
     */
    public void setLinks(Set<Link> links) {
        this.links = links;
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

    public void addLink(Link link) {
        this.links.add(link);
    }
}
