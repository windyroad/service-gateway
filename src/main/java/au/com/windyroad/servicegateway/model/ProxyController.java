package au.com.windyroad.servicegateway.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.Repository;
import au.com.windyroad.servicegateway.controller.AdminProxyController;

@Component
@HateoasController(AdminProxyController.class)
public class ProxyController {

    @Autowired
    ApplicationContext context;

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    public ProxyEntity self(String proxyName) {
        return repository.getProxy(proxyName);
    }

    @HateoasAction(nature = HttpMethod.PUT, controller = AdminProxyController.class)
    public void setEndpoint(String proxyName, String target, String available) {
        ProxyEntity proxy = self(proxyName);
        EndpointEntity endpoint = repository.getEndpoint(target);

        if (endpoint == null) {
            String restOfTheUrl = target
                    .replace(proxy.getProperties().getTarget() + "/", "");

            endpoint = new EndpointEntity(context, repository,
                    new Endpoint(proxy.getProperties().getName(), target,
                            Boolean.parseBoolean(available)),
                    proxy.getProperties().getName(), restOfTheUrl);

            // we shouldn't have to be autowiring the domain objects.
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
            bpp.processInjection(endpoint);

            repository.store(endpoint);
        } else {
            endpoint.getProperties()
                    .setAvailable(Boolean.parseBoolean(available));
            repository.store(endpoint);
        }
    }

    @HateoasAction(nature = HttpMethod.DELETE, controller = AdminProxyController.class)
    public void deleteProxy(String proxyName) {
        repository.deleteProxy(proxyName);
    }

    @HateoasAction(nature = HttpMethod.PUT, controller = AdminProxyController.class)
    public ProxyEntity update(String proxyName, String target) {
        ProxyEntity proxy = self(proxyName);
        proxy.getProperties().setTarget(target);

        return proxy;
    }

}
