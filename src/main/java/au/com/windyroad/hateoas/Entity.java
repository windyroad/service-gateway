package au.com.windyroad.hateoas;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Entity {

    @JsonProperty("class")
    private String classes;

    private Map<String, Object> properties;

    @JsonProperty("actions")
    private Map<String, Action> actions;

    public Entity() {
        this.actions = new HashMap<>();
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public void addAction(Action action) {
        this.actions.put(action.getName(), action);
    }

    public Action getAction(String name) {
        Action rval = this.actions.get(name);
        return rval;
    }

}
