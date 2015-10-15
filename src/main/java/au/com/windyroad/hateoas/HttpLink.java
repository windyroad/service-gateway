package au.com.windyroad.hateoas;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

public class HttpLink extends Link {
    URI href;

    RestTemplate restTemplate;

    protected HttpLink() {
    }

    public HttpLink(Object invocationValue) {
        super(invocationValue);
        this.href = ControllerLinkBuilder.linkTo(invocationValue).toUri();
    }

    @Override
    public URI getHref() {
        return href;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T extends Entity<?>> T follow(Class<T> type) {
        RequestEntity<?> requestEntity = RequestEntity.get(href).build();
        return restTemplate.exchange(requestEntity, type).getBody();
    }

    @Override
    public <T extends Entity<?>> T follow(ParameterizedTypeReference<T> type) {
        RequestEntity<?> requestEntity = RequestEntity.get(href).build();
        return restTemplate.exchange(requestEntity, type).getBody();
    }

}
