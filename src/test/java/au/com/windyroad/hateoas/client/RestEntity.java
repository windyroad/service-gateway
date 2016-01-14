package au.com.windyroad.hateoas.client;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.context.ApplicationContext;

import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.entities.EntityWrapper;
import au.com.windyroad.servicegateway.Repository;

public class RestEntity<T> extends EntityWrapper<T> {

    private Collection<EntityRelationship> entities = new ArrayList<>();

    public RestEntity(T properties) {
        super(properties);
        throw new NotImplementedException("dead?");
    }

    public RestEntity(ApplicationContext context, Repository repository,
            String path, T properties, String title) {
        super(context, repository, path, properties, title);
    }

    /*
     * (non-Javadoc)
     * 
     * @see au.com.windyroad.hateoas.core.ResolvedEntity#getEntities(int)
     */
    @Override
    public Collection<EntityRelationship> getEntities(int page)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        return this.entities;
    }

    /*
     * (non-Javadoc)
     * 
     * @see au.com.windyroad.hateoas.core.ResolvedEntity#setEntities(java.util.
     * Collection)
     */
    // @Override
    // public void setEntities(Collection<EntityRelationship>
    // entityRelationships)
    // throws IllegalAccessException, IllegalArgumentException,
    // InvocationTargetException {
    // this.entities.addAll(entityRelationships);
    // }

}
