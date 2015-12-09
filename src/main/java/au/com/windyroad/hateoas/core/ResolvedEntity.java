package au.com.windyroad.hateoas.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableSet;

import au.com.windyroad.hateoas.annotations.Label;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.hateoas.server.annotations.HateoasChildren;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.Repository;
import au.com.windyroad.servicegateway.controller.RepositoryController;

@JsonPropertyOrder({ "class", "properties", "entities", "actions", "links",
        "title" })
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class ResolvedEntity<T> extends Entity {

    public static URI buildUrl(Class<?> type, Object... parameters) {
        Class<?> controller = type.getAnnotation(HateoasController.class)
                .value();
        URI uri = ControllerLinkBuilder.linkTo(controller, parameters).toUri();
        return uri;
    }

    private Map<String, Action> actions = new HashMap<>();

    private ApplicationContext context;

    @JsonProperty("links")
    private Set<NavigationalRelationship> navigationalRelationships = new HashSet<>();

    T properties;

    private Repository repository;

    public ResolvedEntity() {
    }

    public ResolvedEntity(ApplicationContext context, Repository repository,
            String path, T properties, String... args) {
        super(args);
        this.properties = properties;
        this.repository = repository;
        HateoasController javaControllerAnnotation = properties.getClass()
                .getAnnotation(HateoasController.class);
        Object controller = context.getBean(javaControllerAnnotation.value());
        add(new NavigationalRelationship(
                new JavaLink(controller, path, this, (Object[]) args),
                Relationship.SELF));
        for (Method method : javaControllerAnnotation.value().getMethods()) {
            if (method.getAnnotation(HateoasAction.class) != null) {
                add(new JavaAction(controller, path, method, (Object[]) args));
            }
        }
        getNatures().add(properties.getClass().getSimpleName());

        Label titleAnnotation = properties.getClass()
                .getAnnotation(Label.class);
        if (titleAnnotation != null) {
            setTitle(titleAnnotation.value(), args);
        }
    }

    protected void add(Action action) {
        this.actions.put(action.getIdentifier(), action);
    }

    public void add(NavigationalRelationship navigationalRelationship) {
        navigationalRelationships.add(navigationalRelationship);
    }

    public Action getAction(String identifier) {
        return actions.get(identifier);
    }

    @JsonProperty("actions")
    public ImmutableSet<Action> getActions() {
        return ImmutableSet.copyOf(actions.values());
    }

    @JsonProperty("entities")
    public Collection<EntityRelationship> getEntities()
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        return getEntities(0);
    }

    public Collection<EntityRelationship> getEntities(int page)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        Link link = this.getLink(Relationship.SELF);
        if (link == null) {
            return new ArrayList<>();
        }
        String fullPath = link.getAddress().toString();
        Class<?> controller = RepositoryController.class;
        String basePath = ControllerLinkBuilder.linkTo(controller).toUri()
                .toString();
        basePath = basePath.replace("/admin/**", "");

        String path = fullPath.replace(basePath, "");
        return (repository == null ? new ArrayList<>()
                : repository.getChildren(path));
        /*
         * Set<EntityRelationship> entityRelationships = new HashSet<>(); if
         * (properties != null) { for (Method method :
         * properties.getClass().getMethods()) { HateoasChildren hateoasChildren
         * = method .getAnnotation(HateoasChildren.class); if (hateoasChildren
         * != null && method.getParameterTypes().length == 0) { Object entities
         * = method.invoke(properties); if (entities instanceof Collection) {
         * Collection<?> entityCollection = (Collection<?>) entities; for
         * (Object entity : entityCollection) { entityRelationships.add(new
         * EntityRelationship( (Entity) entity, hateoasChildren.value())); } }
         * else { throw new RuntimeException("unknown entity collection: " +
         * entities.getClass()); } } } } return
         * ImmutableSet.copyOf(entityRelationships);
         */
    }

    public Link getLink(String self) {
        Optional<NavigationalRelationship> link = getLinks().stream()
                .filter(l -> l.hasNature(Relationship.SELF)).findAny();
        if (link.isPresent()) {
            return link.get().getLink();
        } else {
            return null;
        }
    }

    public ImmutableSet<NavigationalRelationship> getLinks() {
        return ImmutableSet.copyOf(navigationalRelationships);
    }

    public T getProperties() {
        return properties;
    }

    @Override
    public <K, L extends ResolvedEntity<K>> L resolve(Class<L> type) {
        return (L) this;
    }

    @Override
    public <K, L extends ResolvedEntity<K>> L resolve(
            ParameterizedTypeReference<L> type) {
        return (L) this;
    }

    protected void setActions(Action[] actions) {
        for (Action action : actions) {
            this.actions.put(action.getIdentifier(), action);
        }
    }

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
        bpp.processInjection(properties);
    }

    public void setEntities(Collection<EntityRelationship> entityRelationships)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        if (properties != null) {
            for (Method method : properties.getClass().getMethods()) {
                HateoasChildren hateoasChildren = method
                        .getAnnotation(HateoasChildren.class);
                if (hateoasChildren != null
                        && method.getParameterTypes().length != 0) {
                    List<LinkedEntity> entities = new ArrayList<>();
                    entityRelationships.stream()
                            .filter(e -> Arrays.asList(e.getNature())
                                    .contains(hateoasChildren.value()))
                            .forEach(er -> entities
                                    .add(er.getEntity().toLinkedEntity()));

                    method.invoke(properties, entities);
                }
            }
        }
    }

    @Override
    public LinkedEntity toLinkedEntity() {
        LinkedEntity linkedEntity = new LinkedEntity(getLink(Relationship.SELF),
                getNatures(), getLabel());
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
        bpp.processInjection(linkedEntity);
        return linkedEntity;
    }

    public <L extends ResolvedEntity<T>> L refresh() {
        return (L) getLink(Relationship.SELF).resolve(this.getClass());
    }

    @Override
    @JsonIgnore
    public URI getAddress() throws URISyntaxException {
        return getLink(Relationship.SELF).getAddress();
    }

}
