package au.com.windyroad.servicegateway.model;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.annotation.JsonIgnore;

import au.com.windyroad.hateoas.core.LinkedEntity;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.server.annotations.HateoasChildren;
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

    // @HateoasAction(nature = HttpMethod.POST, controller =
    // AdminProxiesController.class)
    // static public LinkedEntity createProxy(ApplicationContext context,
    // Repository repository, Proxies proxies, String proxyName,
    // String endpoint) throws NoSuchMethodException, SecurityException,
    // IllegalAccessException, IllegalArgumentException,
    // InvocationTargetException, URISyntaxException {
    //
    // ProxyEntity existingProxy = repository.getProxy(proxyName);
    //
    // if (existingProxy != null) {
    // return existingProxy.toLinkedEntity();
    // } else {
    // ProxyEntity proxy = new ProxyEntity(context, repository,
    // new Proxy(proxyName, endpoint), proxyName);
    // AutowiredAnnotationBeanPostProcessor bpp = new
    // AutowiredAnnotationBeanPostProcessor();
    // bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
    // bpp.processInjection(proxy);
    //
    // repository.store(proxy);
    // return proxy.toLinkedEntity();
    // }
    // }

    // @HateoasAction(nature = HttpMethod.POST, controller =
    // AdminProxiesController.class)
    // public LinkedEntity createProxy(String proxyName, String endpoint)
    // throws NoSuchMethodException, SecurityException,
    // IllegalAccessException, IllegalArgumentException,
    // InvocationTargetException, URISyntaxException {
    //
    // ProxyEntity existingProxy = repository.getProxy(proxyName);
    //
    // if (existingProxy != null) {
    // return existingProxy.toLinkedEntity();
    // } else {
    // ProxyEntity proxy = new ProxyEntity(new Proxy(proxyName, endpoint),
    // proxyName);
    // AutowiredAnnotationBeanPostProcessor bpp = new
    // AutowiredAnnotationBeanPostProcessor();
    // bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
    // bpp.processInjection(proxy);
    //
    // repository.store(proxy);
    // return proxy.toLinkedEntity();
    // }
    // }

    @HateoasChildren(Relationship.ITEM)
    @JsonIgnore
    public Collection<ProxyEntity> getProxies() {
        return repository.getProxies();
    }

    @HateoasChildren(Relationship.ITEM)
    public void setEndpoints(Collection<LinkedEntity> proxies) {
        // hmmm.., this is called during deserialisation.
        // clients don't have access to the repository, so
        // this will fail.
        // need to think about how to handle this
        // client side classes need to deserialize very differently.
        // for (LinkedEntity proxy : proxies) {
        // URI address = proxy.getAddress();
        // String[] pathElements = address.getPath().split("/");
        // repository.store(proxy);
        // this.proxies.put(pathElements[pathElements.length - 1], proxy);
        // }
    }

}
