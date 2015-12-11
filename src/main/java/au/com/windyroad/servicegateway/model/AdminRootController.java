package au.com.windyroad.servicegateway.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import au.com.windyroad.hateoas.core.EntityRelationship;
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
        EntityWrapper<?> existingProxy = repository.findOne(path);

        if (existingProxy != null) {
            throw new HttpClientErrorException(HttpStatus.CONFLICT);
        } else {
            EntityWrapper<Proxy> proxy = new EntityWrapper<Proxy>(context,
                    repository, path, new Proxy(proxyName, endpoint),
                    proxyName);
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
            bpp.processInjection(proxy);

            repository.save(proxy);
            repository.setChildren(proxy,
                    AdminRootController::findByEndpointsForProxy);
            return proxy;
        }
    }

    static public Iterator<EntityRelationship> findByEndpointsForProxy(
            Repository repository, EntityWrapper<?> entity) {
        // return StreamSupport
        // .stream(Spliterators.spliteratorUnknownSize(
        // repository.findByEndpointsForProxy(entity),
        // Spliterator.ORDERED), false)
        // .map(e -> new EntityRelationship(e, Relationship.ITEM))
        // .iterator();
        //
        List<EntityWrapper<?>> entities = new ArrayList<>();
        Iterator<EntityWrapper<?>> iterator = repository
                .findByEndpointsForProxy(entity);
        while (iterator.hasNext()) {
            entities.add(iterator.next());
        }
        Iterator<EntityRelationship> relIter = entities.stream()
                .map(e -> new EntityRelationship(e, Relationship.ITEM))
                .iterator();
        List<EntityRelationship> rels = new ArrayList<>();
        while (relIter.hasNext()) {
            rels.add(relIter.next());
        }
        return rels.iterator();
    }

    static public Iterator<EntityRelationship> findAllProxies(
            Repository repository, EntityWrapper<?> entity) {
        // return StreamSupport
        // .stream(Spliterators.spliteratorUnknownSize(
        // repository.findAllProxies(entity), Spliterator.ORDERED),
        // false).map(e -> new EntityRelationship(e, Relationship.ITEM))
        // .iterator();
        List<EntityWrapper<?>> entities = new ArrayList<>();
        Iterator<EntityWrapper<?>> iterator = repository.findAllProxies(entity);
        while (iterator.hasNext()) {
            entities.add(iterator.next());
        }
        return entities.stream()
                .map(e -> new EntityRelationship(e, Relationship.ITEM))
                .collect(Collectors.toList()).iterator();

    }

}
