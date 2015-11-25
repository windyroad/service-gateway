package au.com.windyroad.hateoas2;

import java.net.URI;
import java.util.Set;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.windyroad.hateoas.MediaTypes;

public class RestLink extends Link {

    URI address;

    @Nullable
    MediaType representationFormat = MediaTypes.SIREN_JSON;

    private RestTemplate restTemplate;

    public RestLink() {
    }

    public RestLink(URI address, Set<String> natures, String label) {
        super(natures, label);
        this.address = address;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> T resolve(Class<T> type) {
        RequestEntity<Void> request = RequestEntity.get(address)
                .accept(getRepresentationFormat()).build();
        ResponseEntity<T> response = restTemplate.exchange(address,
                HttpMethod.GET, request, type);
        return response.getBody();
    }

    @Override
    public MediaType getRepresentationFormat() {
        return representationFormat == null ? MediaTypes.SIREN_JSON
                : representationFormat;
    }

    @Override
    @JsonProperty("href")
    public URI getAddress() {
        return address;
    }

    public void setAddress(URI address) {
        this.address = address;
    }

}
