package au.com.windyroad.servicegateway.model;

import java.io.UnsupportedEncodingException;

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

    @HateoasAction(nature = HttpMethod.PUT, controller = AdminProxyController.class)
    public void setEndpoint(String proxyName, String target, String available)
            throws UnsupportedEncodingException {
        ProxyEntity proxy = (ProxyEntity) repository
                .get("/admin/proxies/" + proxyName);
        String path = Endpoint.getUrl(target);
        EndpointEntity endpoint = (EndpointEntity) repository.get(path);

        if (endpoint == null) {
            String restOfTheUrl = target
                    .replace(proxy.getProperties().getTarget() + "/", "");

            endpoint = new EndpointEntity(context, repository, path,
                    new Endpoint(target, Boolean.parseBoolean(available)),
                    target);

            // we shouldn't have to be autowiring the domain objects.
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
            bpp.processInjection(endpoint);

            repository.put(path, endpoint);
        } else {
            endpoint.getProperties()
                    .setAvailable(Boolean.parseBoolean(available));
            repository.put(path, endpoint);
        }
    }

    @HateoasAction(nature = HttpMethod.DELETE, controller = AdminProxyController.class)
    public void deleteProxy(String proxyName) {
        repository.remove(proxyName);
    }

    @HateoasAction(nature = HttpMethod.PUT, controller = AdminProxyController.class)
    public ProxyEntity update(String proxyName, String target) {
        ProxyEntity proxy = (ProxyEntity) repository
                .get("/admin/proxies/" + proxyName);

        proxy.getProperties().setTarget(target);

        return proxy;
    }

}
