package au.com.windyroad.hateoas.client;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

import au.com.windyroad.hateoas.core.Action;
import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.LinkedEntity;
import au.com.windyroad.hateoas.core.MediaTypes;
import au.com.windyroad.hateoas.core.Parameter;

public class RestAction extends Action {

    private URI address;
    private MediaType invocationFormat = MediaType.APPLICATION_FORM_URLENCODED;

    private HttpMethod nature = HttpMethod.GET;

    @Autowired
    private RestTemplate restTemplate;
    private ApplicationContext applicationContext;

    public RestAction(@JsonProperty("name") String identifier) {
        super(identifier);
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public CompletableFuture<Entity> invoke(Map<String, String> context)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        switch (nature) {
        case POST: {
            return CompletableFuture.supplyAsync(() -> {
                Set<String> parameterKeys = getParameterKeys();
                Map<String, String> filteredParameters = Maps
                        .filterKeys(context, Predicates.in(parameterKeys));
                filteredParameters.put("action", getIdentifier());
                MultiValueMap<String, String> body = new LinkedMultiValueMap<>(
                        filteredParameters.size());
                for (Entry<String, String> entry : filteredParameters
                        .entrySet()) {
                    body.add(entry.getKey(), entry.getValue());
                }
                RequestEntity<?> request = RequestEntity.post(address)
                        .accept(MediaTypes.SIREN_JSON)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(body);
                URI location = restTemplate.postForLocation(address, request);
                LinkedEntity linkedEntity = new LinkedEntity(location, null,
                        null);
                AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
                bpp.setBeanFactory(
                        applicationContext.getAutowireCapableBeanFactory());
                bpp.processInjection(linkedEntity);

                return linkedEntity;
            });
        }
        case PUT: {
            return CompletableFuture.supplyAsync(() -> {

                Set<String> parameterKeys = getParameterKeys();
                Map<String, String> filteredParameters = Maps
                        .filterKeys(context, Predicates.in(parameterKeys));
                filteredParameters.put("action", getIdentifier());
                MultiValueMap<String, String> body = new LinkedMultiValueMap<>(
                        filteredParameters.size());
                for (Entry<String, String> entry : filteredParameters
                        .entrySet()) {
                    body.add(entry.getKey(), entry.getValue());
                }
                RequestEntity<?> request = RequestEntity.put(address)
                        .accept(MediaTypes.SIREN_JSON)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .body(body);
                ResponseEntity<Void> response = restTemplate.exchange(request,
                        Void.class);
                LinkedEntity linkedEntity = new LinkedEntity(
                        response.getHeaders().getLocation(), null, null);
                AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
                bpp.setBeanFactory(
                        applicationContext.getAutowireCapableBeanFactory());
                bpp.processInjection(linkedEntity);

                return linkedEntity;
            });
        }
        default:
            throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED);
        }
    }

    public Set<String> getParameterKeys() {
        Set<String> rval = new HashSet<>();
        for (Parameter param : getParameters()) {
            rval.add(param.getIdentifier());
        }
        return rval;
    }

    /**
     * @return the nature
     */
    @Override
    public HttpMethod getNature() {
        return nature;
    }

    @Override
    public URI getAddress() {
        return address;
    }

}
