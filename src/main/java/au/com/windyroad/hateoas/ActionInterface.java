package au.com.windyroad.hateoas;

import java.util.Properties;

public interface ActionInterface {

    Entity invoke(Entity entity, Properties context);
}
