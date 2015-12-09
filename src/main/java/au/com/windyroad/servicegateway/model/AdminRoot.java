package au.com.windyroad.servicegateway.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.server.annotations.HateoasController;

@HateoasController(AdminRootController.class)
public class AdminRoot {

    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public AdminRoot() {
    }

}
