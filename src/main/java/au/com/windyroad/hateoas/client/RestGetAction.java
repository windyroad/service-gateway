package au.com.windyroad.hateoas.client;

import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.core.FutureConverter;
import au.com.windyroad.hateoas.core.MediaTypes;
import au.com.windyroad.hateoas.core.Parameter;

public class RestGetAction extends RestAction<EntityWrapper<?>> {

    @Autowired
    private AsyncRestTemplate restTemplate;
    private ApplicationContext applicationContext;

    public RestGetAction(@JsonProperty("name") String identifier) {
        super(identifier);
    }

    public RestGetAction(String identifier, URI href, Parameter[] fields) {
        super(identifier, href, fields);
    }

    @Autowired
    public void setRestTemplate(AsyncRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public CompletableFuture<EntityWrapper<?>> invoke(
            Map<String, Object> context) {
        Set<String> parameterKeys = getParameterKeys();
        Map<String, Object> filteredParameters = Maps.filterKeys(context,
                Predicates.in(parameterKeys));
        filteredParameters.put("action", getIdentifier());
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>(
                filteredParameters.size());
        for (Entry<String, Object> entry : filteredParameters.entrySet()) {
            body.add(entry.getKey(), entry.getValue());
        }
        RequestEntity<?> request = RequestEntity.put(getAddress())
                .accept(MediaTypes.SIREN_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(body);

        ListenableFuture<ResponseEntity<EntityWrapper>> responseFuture = restTemplate
                .exchange(getAddress(), HttpMethod.GET, request,
                        EntityWrapper.class);
        return FutureConverter.convert(responseFuture)
                .thenApplyAsync(response -> {
                    return response.getBody();
                });
    }

    @Override
    public Set<String> getParameterKeys() {
        Set<String> rval = new HashSet<>();
        for (Parameter param : getParameters()) {
            rval.add(param.getIdentifier());
        }
        return rval;
    }

    @Override
    public HttpMethod getNature() {
        return HttpMethod.GET;
    }

}
