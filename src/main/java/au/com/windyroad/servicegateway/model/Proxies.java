package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.core.ResolvedEntity;

@Component
public class Proxies extends ResolvedEntity<ProxiesProperties> {

    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected Proxies() throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException,
            SecurityException {
        super(new ProxiesProperties());

    }
}
