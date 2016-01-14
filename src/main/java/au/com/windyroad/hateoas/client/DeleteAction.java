package au.com.windyroad.hateoas.client;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpMethod;

import au.com.windyroad.hateoas.core.Action;
import au.com.windyroad.hateoas.core.Link;
import au.com.windyroad.hateoas.core.Parameter;
import au.com.windyroad.hateoas.core.Resolver;

public class DeleteAction extends Action<Void> {

    public DeleteAction(Resolver resolver, String identifier, Link link,
            Parameter[] fields) {
        super(resolver, identifier, link, fields);
    }

    @Override
    public CompletableFuture<Void> doInvoke(Resolver resolver,
            Map<String, Object> filteredParameters) {
        return resolver.delete(getLink(), filteredParameters);
    }

    /**
     * @return the nature
     */
    @Override
    public HttpMethod getNature() {
        return HttpMethod.DELETE;
    }

}
