package au.com.windyroad.hateoas2;

import java.util.Properties;

public class ActionArgument<T, K> {
    T entity;
    Properties context;

    public ActionArgument(T entity, Properties context) {
        this.entity = entity;
        this.context = context;
    }

    public T getEntity() {
        return entity;
    }
}