package au.com.windyroad.hateoas;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class SirenTemplate {

    private RestTemplate restTemplate;

    public SirenTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> ResponseEntity<T> executeForLocation(Entity<?> currEntity,
            String actionName, Map<String, Object> context,
            ParameterizedTypeReference<T> type) {
        Action action = currEntity.getAction(actionName);
        if (action == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        for (Field field : action.getFields()) {
            Object value = field.getValue();
            if (field.getType() != "hidden") {
                value = context.get(field.getName());
            }
            params.add(field.getName(), value);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections
                .singletonList(new MediaType("application", "vnd.siren+json")));
        HttpEntity<?> request = new HttpEntity<>(params, headers);
        URI location = restTemplate.postForLocation(action.getHref(), request);

        ResponseEntity<T> response = restTemplate
                .exchange(RequestEntity.get(location).build(), type);
        return response;
    }
}
