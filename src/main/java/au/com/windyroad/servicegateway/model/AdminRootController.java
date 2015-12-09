package au.com.windyroad.servicegateway.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.servicegateway.Repository;

@Component
public class AdminRootController {

    @Autowired
    ApplicationContext context;

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    @HateoasAction(nature = HttpMethod.POST)
    public EntityWrapper<?> createProxy(EntityWrapper<AdminRoot> entity,
            String proxyName, String endpoint) {
        String path = entity.getId() + "/" + proxyName;
        EntityWrapper<?> existingProxy = repository.get(path);

        if (existingProxy != null) {
            throw new HttpClientErrorException(HttpStatus.CONFLICT);
        } else {
            EntityWrapper<Proxy> proxy = new EntityWrapper<Proxy>(context,
                    repository, path, new Proxy(proxyName, endpoint),
                    proxyName);
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
            bpp.processInjection(proxy);

            repository.put(proxy);
            repository.addChild(entity, proxy, Relationship.ITEM);
            return proxy;
        }
    }
}
