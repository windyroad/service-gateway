package au.com.windyroad.servicegateway.model;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.servicegateway.Repository;

@Component
public class ProxyController {

    @Autowired
    ApplicationContext context;

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    @HateoasAction(nature = HttpMethod.PUT)
    public void setEndpoint(EntityWrapper<Proxy> proxy, String proxyName,
            String target, String available)
                    throws UnsupportedEncodingException {
        String path = Endpoint.buildUrl(target);
        EntityWrapper<Endpoint> endpoint = (EntityWrapper<Endpoint>) repository
                .get(path);

        if (endpoint == null) {
            endpoint = new EntityWrapper<Endpoint>(context, repository, path,
                    new Endpoint(target, Boolean.parseBoolean(available)),
                    "Endpoint `" + target + "`");

            repository.put(endpoint);
            repository.addChild(proxy, endpoint, Relationship.ITEM);

        } else {
            endpoint.getProperties()
                    .setAvailable(Boolean.parseBoolean(available));
            // repository.put(path, endpoint);
        }
    }

    @HateoasAction(nature = HttpMethod.DELETE)
    public void deleteProxy(EntityWrapper<Proxy> proxy) {
        repository.remove(proxy);
        // todo: remove the children records
    }

    @HateoasAction(nature = HttpMethod.PUT)
    public EntityWrapper<Proxy> update(EntityWrapper<Proxy> proxy,
            String proxyName, String target) {
        proxy.getProperties().setTarget(target);

        return proxy;
    }

}
