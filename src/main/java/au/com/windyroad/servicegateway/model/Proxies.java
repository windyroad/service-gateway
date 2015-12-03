package au.com.windyroad.servicegateway.model;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.LinkedEntity;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.hateoas.server.annotations.HateoasChildren;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.Repository;
import au.com.windyroad.servicegateway.controller.AdminProxiesController;

@HateoasController(AdminProxiesController.class)
public class Proxies {

    ApplicationContext context;

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    @JsonIgnore
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Repository repository;

    private Map<String, Entity> proxies = new HashMap<>();

    public Proxies() {
    }

    @HateoasAction(nature = HttpMethod.POST, controller = AdminProxiesController.class)
    public LinkedEntity createProxy(String proxyName, String endpoint)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {

        // repository.getProxy(proxyName);

        ProxyEntity existingProxy = repository.getProxy(proxyName);

        if (existingProxy != null) {
            return existingProxy.toLinkedEntity();
        } else {
            ProxyEntity proxy = new ProxyEntity(
                    new Proxy(this, proxyName, endpoint), proxyName);
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
            bpp.processInjection(proxy);

            // proxies.put(proxyName, proxy);
            repository.store(proxy);
            return proxy.toLinkedEntity();
        }
    }

    @HateoasChildren(Relationship.ITEM)
    @JsonIgnore
    public Collection<Entity> getProxies() {
        return proxies.values();
    }

    @HateoasChildren(Relationship.ITEM)
    public void setEndpoints(Collection<LinkedEntity> proxies) {
        for (LinkedEntity proxy : proxies) {
            URI address = proxy.getAddress();
            String[] pathElements = address.getPath().split("/");
            this.proxies.put(pathElements[pathElements.length - 1], proxy);
        }
    }

    void deleteProxy(String proxyName) {
        proxies.remove(proxyName);
    }

}
