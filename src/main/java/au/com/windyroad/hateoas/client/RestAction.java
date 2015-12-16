package au.com.windyroad.hateoas.client;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonProperty;

import au.com.windyroad.hateoas.core.Action;
import au.com.windyroad.hateoas.core.Parameter;

public abstract class RestAction<T> extends Action<T> {

    private URI address;
    private MediaType invocationFormat = MediaType.APPLICATION_FORM_URLENCODED;

    @Autowired
    private RestTemplate restTemplate;
    private ApplicationContext applicationContext;

    public RestAction(@JsonProperty("name") String identifier) {
        super(identifier);
    }

    public RestAction(String identifier, URI href, Parameter[] fields) {
        super(identifier, fields);
        this.address = href;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Set<String> getParameterKeys() {
        Set<String> rval = new HashSet<>();
        for (Parameter param : getParameters()) {
            rval.add(param.getIdentifier());
        }
        return rval;
    }

    @Override
    public URI getAddress() {
        return address;
    }

}
