package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.model.Endpoint;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;

@Component
@Profile(value = "default")
public class JavaDriver implements Driver {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ResolvedEntity<Proxies> proxies;

    @Autowired
    ServiceGatewayTestConfiguration config;

    @Autowired
    RestTemplate restTemplate;

    private ResolvedEntity<Proxy> currentProxy;

    private ResolvedEntity<Endpoint> currentEndpoint;

    @Override
    public void clearProxies() {

    }

    @Override
    public void checkPingService(String path) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost();

        ResponseEntity<String> response = restTemplate.getForEntity(
                new URI("https://localhost:" + config.getPort() + path),
                String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
        LOGGER.info("PING SERVICE CHECKED");
    }

    @Override
    public void createProxy(String proxyName, String endpoint)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        ParameterizedTypeReference<ResolvedEntity<Proxy>> type = new ParameterizedTypeReference<ResolvedEntity<Proxy>>() {
        };

        this.currentProxy = proxies.getProperties()
                .createProxy(proxies, proxyName, endpoint).resolve(type);
    }

    @Override
    public void get(String path) throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                new URI("https://localhost:" + config.getPort() + path),
                String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Override
    public void checkEndpointExists(String path, String endpointName)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Entity<Endpoint> endpoint = currentProxy.getProperties()
                .getEndpoint(endpointName);
        assertThat(endpoint, notNullValue());
        ParameterizedTypeReference<ResolvedEntity<Endpoint>> type = new ParameterizedTypeReference<ResolvedEntity<Endpoint>>() {
        };

        currentEndpoint = endpoint.resolve(type);

    }

    @Override
    public void checkCurrentEndpointAvailable() {
        assertTrue(currentEndpoint.getProperties().isAvailable());

    }

}
