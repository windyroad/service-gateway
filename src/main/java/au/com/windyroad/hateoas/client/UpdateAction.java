package au.com.windyroad.hateoas.client;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpMethod;

import au.com.windyroad.hateoas.core.Action;
import au.com.windyroad.hateoas.core.Link;
import au.com.windyroad.hateoas.core.Parameter;
import au.com.windyroad.hateoas.core.UpdatedLinkedEntity;

public class UpdateAction extends Action<UpdatedLinkedEntity> {

    public UpdateAction(Resolver resolver, String identifier, Link link,
            Parameter[] fields) {
        super(resolver, identifier, link, fields);
    }

    @Override
    public CompletableFuture<UpdatedLinkedEntity> doInvoke(Resolver resolver,
            Map<String, Object> filteredParameters) {
        return resolver.update(getLink(), filteredParameters);
    }

    /**
     * @return the nature
     */
    @Override
    public HttpMethod getNature() {
        return HttpMethod.PUT;
    }

}
