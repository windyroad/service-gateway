package au.com.windyroad.hateoas;

import java.net.URI;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonInclude;

import au.com.windyroad.hateoas.client.LinkVisitor;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class EmbeddedEntityHttpLink extends EmbeddedEntityLink {

    private RestTemplate restTemplate;
    private URI href;

    protected EmbeddedEntityHttpLink() {
    }

    @Override
    public <T extends Entity<?>> T toEntity(Class<T> type) {
        return follow(type);
    }

    @Override
    public <T extends Entity<?>> T toEntity(
            ParameterizedTypeReference<T> type) {
        return follow(type);
    }

    @Override
    public URI getHref() {
        return href;
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

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void accept(LinkVisitor linkVisitor) {
        linkVisitor.visit(this);
    }

}
