package au.com.windyroad.servicegateway.model;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import au.com.windyroad.hateoas.core.CreatedLinkedEntity;
import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.servicegateway.Repository;

public class AdminRootController extends EntityWrapper<AdminRoot>
        implements IAdminRootController {

    @Autowired
    ApplicationContext context;

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    protected AdminRootController() {

    }

    protected AdminRootController(AdminRootController src) {
        super(src);
        this.context = src.context;
        this.repository = src.repository;
    }

    public AdminRootController(ApplicationContext context,
            Repository repository, String path, AdminRoot properties,
            String title) {
        super(context, repository, path, properties, title);
    }

    public CompletableFuture<CreatedLinkedEntity> createProxy(String proxyName,
            String endpoint) {

        String path = this.getId() + "/" + proxyName;
        CompletableFuture<EntityWrapper<?>> existingProxyFuture = repository
                .findOne(path);

        final RequestAttributes currentRequestAttributes = RequestContextHolder
                .getRequestAttributes();

        return existingProxyFuture.thenApplyAsync(existingProxy -> {
            RequestContextHolder.setRequestAttributes(currentRequestAttributes);
            if (existingProxy != null) {
                throw new HttpClientErrorException(HttpStatus.CONFLICT);
            } else {
                ProxyController proxy = new ProxyController(context, repository,
                        path, new Proxy(proxyName, endpoint), proxyName);
                AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
                bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
                bpp.processInjection(proxy);

                repository.save(proxy);
                repository.setChildren(proxy,
                        AdminRootController::findByEndpointsForProxy);
                return new CreatedLinkedEntity(proxy);
            }
        });
    }

    static public Stream<EntityRelationship> findByEndpointsForProxy(
            Repository repository, EntityWrapper<?> entity) {
        return repository.findByEndpointsForProxy(entity)
                .map(e -> new EntityRelationship(e, Relationship.ITEM));
    }

    static public Stream<EntityRelationship> findAllProxies(
            Repository repository, EntityWrapper<?> entity) {
        return repository.findAllProxies(entity)
                .map(e -> new EntityRelationship(e, Relationship.ITEM));
    }

}
