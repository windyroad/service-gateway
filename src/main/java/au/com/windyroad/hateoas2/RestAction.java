package au.com.windyroad.hateoas2;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;

import au.com.windyroad.hateoas.MediaTypes;

public class RestAction extends Action {

    private URI address;
    private MediaType invocationFormat = MediaType.APPLICATION_FORM_URLENCODED;

    private HttpMethod nature = HttpMethod.GET;

    @Autowired
    private RestTemplate restTemplate;

    public RestAction(@JsonProperty("name") String identifier) {
        super(identifier);
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T extends Entity> Entity invoke(T entity,
            Map<String, String> context) throws IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        switch (nature) {
        case POST:
            Set<String> parameterKeys = getParameterKeys();
            Map<String, String> filteredParameters = Maps.filterKeys(context,
                    Predicates.in(parameterKeys));
            filteredParameters.put("trigger", getIdentifier());
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>(
                    filteredParameters.size());
            for (Entry<String, String> entry : filteredParameters.entrySet()) {
                body.add(entry.getKey(), entry.getValue());
            }
            RequestEntity<?> request = RequestEntity.post(address)
                    .accept(MediaTypes.SIREN_JSON)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body);
            return restTemplate.exchange(request, ResolvedEntity.class)
                    .getBody();
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
