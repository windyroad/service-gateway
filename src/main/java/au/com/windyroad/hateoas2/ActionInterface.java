package au.com.windyroad.hateoas2;

import java.util.Properties;

public interface ActionInterface {

    Entity invoke(Entity entity, Properties context);
}
