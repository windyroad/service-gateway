package au.com.windyroad.hateoas;

import java.net.URI;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.HeadersBuilder;
import org.springframework.web.client.RestTemplate;

public class RestTemplateResolver implements Resolver {

    RestTemplate restTemplate;

    @Override
    public <T> T resolve(URI address, @Nullable MediaType representationFormat,
            Class<T> responseType) {
        RequestEntity<Void> requestEntity = buildRequestEntity(address,
                representationFormat);
        return restTemplate.exchange(requestEntity, responseType).getBody();
    }

    protected RequestEntity<Void> buildRequestEntity(URI address,
            MediaType representationFormat) {
        return setAcceptHeader(createGetRequest(address), representationFormat)
                .build();
    }

    protected HeadersBuilder<?> setAcceptHeader(HeadersBuilder<?> request,
            MediaType representationFormat) {
        return request.accept(representationFormat);
    }

    protected HeadersBuilder<?> createGetRequest(URI address) {
        return RequestEntity.get(address);
    }

}
