package au.com.windyroad.hateoas.client;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;

import au.com.windyroad.hateoas.core.CreatedLinkedEntity;
import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.core.FutureConverter;
import au.com.windyroad.hateoas.core.Link;
import au.com.windyroad.hateoas.core.MediaTypes;
import au.com.windyroad.hateoas.core.Resolver;
import au.com.windyroad.hateoas.core.RestLink;
import au.com.windyroad.hateoas.core.UpdatedLinkedEntity;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;

@Component()
@Profile(value = "integration")
@Primary
public class RestTemplateResolver implements Resolver {

    @Autowired
    private AsyncRestTemplate restTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public CompletableFuture<CreatedLinkedEntity> create(Link link,
            Map<String, Object> filteredParameters) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>(
                filteredParameters.size());
        for (Entry<String, Object> entry : filteredParameters.entrySet()) {
            body.add(entry.getKey(), entry.getValue());
        }
        RequestEntity<?> request = RequestEntity.post(link.getAddress())
                .accept(MediaTypes.SIREN_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(body);
        ListenableFuture<URI> locationFuture = restTemplate
                .postForLocation(link.getAddress(), request);
        return FutureConverter.convert(locationFuture).thenApplyAsync(uri -> {
            CreatedLinkedEntity linkedEntity = new CreatedLinkedEntity(
                    new RestLink(uri));
            AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
            bpp.setBeanFactory(
                    applicationContext.getAutowireCapableBeanFactory());
            bpp.processInjection(linkedEntity);

            return linkedEntity;
        });
    }

    @Override
    public CompletableFuture<Void> delete(Link link,
            Map<String, Object> filteredParameters) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>(
                filteredParameters.size());
        for (Entry<String, Object> entry : filteredParameters.entrySet()) {
            body.add(entry.getKey(), entry.getValue());
        }
        RequestEntity<?> request = RequestEntity.put(link.getAddress())
                .accept(MediaTypes.SIREN_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(body);

        ListenableFuture<ResponseEntity<Void>> responseFuture = restTemplate
                .exchange(link.getAddress(), HttpMethod.DELETE, request,
                        Void.class);
        return FutureConverter.convert(responseFuture)
                .thenApplyAsync(response -> {
                    return response.getBody();
                });
    }

    @Override
    public CompletableFuture<EntityWrapper<?>> get(Link link,
            Map<String, Object> filteredParameters) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>(
                filteredParameters.size());
        for (Entry<String, Object> entry : filteredParameters.entrySet()) {
            body.add(entry.getKey(), entry.getValue());
        }
        RequestEntity<?> request = RequestEntity.put(link.getAddress())
                .accept(MediaTypes.SIREN_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(body);

        ListenableFuture<ResponseEntity<EntityWrapper>> responseFuture = restTemplate
                .exchange(link.getAddress(), HttpMethod.GET, request,
                        EntityWrapper.class);
        return FutureConverter.convert(responseFuture)
                .thenApplyAsync(response -> {
                    return response.getBody();
                });

    }

    @Override
    public CompletableFuture<UpdatedLinkedEntity> update(Link link,
            Map<String, Object> filteredParameters) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>(
                filteredParameters.size());
        for (Entry<String, Object> entry : filteredParameters.entrySet()) {
            body.add(entry.getKey(), entry.getValue());
        }
        RequestEntity<?> request = RequestEntity.put(link.getAddress())
                .accept(MediaTypes.SIREN_JSON)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED).body(body);

        ListenableFuture<ResponseEntity<Void>> responseFuture = restTemplate
                .exchange(link.getAddress(), HttpMethod.PUT, request,
                        Void.class);
        return FutureConverter.convert(responseFuture)
                .thenApplyAsync(response -> {
                    UpdatedLinkedEntity linkedEntity = new UpdatedLinkedEntity(
                            new RestLink(response.getHeaders().getLocation()));
                    AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
                    bpp.setBeanFactory(
                            applicationContext.getAutowireCapableBeanFactory());
                    bpp.processInjection(linkedEntity);

                    return linkedEntity;
                });
    }

    @Autowired
    ServiceGatewayTestConfiguration config;

    @Override
    public <E> CompletableFuture<E> get(String path, Class<E> type) {
        URI rootUrl = config.getBaseUri().resolve(path);

        return FutureConverter.convert(
                restTemplate.exchange(rootUrl, HttpMethod.GET, null, type))
                .thenApply(r -> {
                    return r.getBody();
                });

    }

}
