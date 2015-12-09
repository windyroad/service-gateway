package au.com.windyroad.servicegateway.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.Repository;

@HateoasController(ProxiesController.class)
public class Proxies {

    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    public Proxies() {
    }

}
