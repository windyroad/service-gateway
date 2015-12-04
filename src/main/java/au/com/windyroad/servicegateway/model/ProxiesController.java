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
import au.com.windyroad.servicegateway.controller.AdminProxiesController;

@Component
@HateoasController(AdminProxiesController.class)
public class ProxiesController {

    @Autowired
    ApplicationContext context;

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    public ProxiesEntity self() {
        return repository.getRoot();
    }

    @HateoasAction(nature = HttpMethod.POST, controller = AdminProxiesController.class)
    public ProxyEntity createProxy(String proxyName, String endpoint) {

        ProxyEntity existingProxy = repository.getProxy(proxyName);

        if (existingProxy != null) {
            return existingProxy;
        } else {
            ProxyEntity proxy = new ProxyEntity(context, repository,
                    new Proxy(proxyName, endpoint), proxyName);
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
            bpp.processInjection(proxy);

            repository.store(proxy);
            return proxy;
        }
    }
}
