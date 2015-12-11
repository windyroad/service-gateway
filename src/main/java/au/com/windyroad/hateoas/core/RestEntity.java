package au.com.windyroad.hateoas.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.springframework.context.ApplicationContext;

import au.com.windyroad.servicegateway.Repository;

public class RestEntity<T> extends EntityWrapper<T> {

    private Collection<EntityRelationship> entities = new ArrayList<>();

    public RestEntity() {

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
    public Iterator<EntityRelationship> getEntities(int page)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        return this.entities.iterator();
    }

    /*
     * (non-Javadoc)
     * 
     * @see au.com.windyroad.hateoas.core.ResolvedEntity#setEntities(java.util.
     * Collection)
     */
    @Override
    public void setEntities(Collection<EntityRelationship> entityRelationships)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        this.entities.addAll(entityRelationships);
    }

}
