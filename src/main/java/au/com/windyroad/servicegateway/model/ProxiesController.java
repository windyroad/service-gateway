package au.com.windyroad.servicegateway.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.core.ResolvedEntity;
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

    @HateoasAction(nature = HttpMethod.POST, controller = AdminProxiesController.class)
    public ResolvedEntity<?> createProxy(String proxyName, String endpoint) {

        String path = "/admin/proxies/" + proxyName;
        ResolvedEntity<?> existingProxy = repository.get(path);

        if (existingProxy != null) {
            return existingProxy;
        } else {
            ResolvedEntity<Proxy> proxy = new ResolvedEntity<Proxy>(context,
                    repository, path, new Proxy(proxyName, endpoint),
                    proxyName);
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
            bpp.processInjection(proxy);

            repository.put(path, proxy);
            repository.addChild("/admin/proxies", path, proxy,
                    Relationship.ITEM);
            return proxy;
        }
    }
}
