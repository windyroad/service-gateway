package au.com.windyroad.servicegateway.model;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AdminRoot implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -5101362070340337389L;

    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public AdminRoot() {
    }

    @JsonProperty
    private String foo = "42";

}
