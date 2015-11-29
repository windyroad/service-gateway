package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.controller.AdminProxyController;

@HateoasController(AdminProxyController.class)
public class Proxy extends ResolvedEntity<ProxyProperties> {

    protected Proxy() {
        super(new ProxyProperties());
    }

    public Proxy(String name, String target) throws NoSuchMethodException,
            SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        super(new ProxyProperties(), name);
        getProperties().setName(name);
        getProperties().setTarget(target);
    }

}
