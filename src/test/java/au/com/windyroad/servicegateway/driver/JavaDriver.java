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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import au.com.windyroad.hateoas.EmbeddedEntityLink;
import au.com.windyroad.hateoas.Link;
import au.com.windyroad.hateoas.annotations.Rel;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.TestContext;
import au.com.windyroad.servicegateway.model.Endpoint;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;

@Component
@Profile(value = "default")
public class JavaDriver implements Driver {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Proxies proxies;

    @Autowired
    ServiceGatewayTestConfiguration config;

    @Autowired
    RestTemplate restTemplate;

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
    public EmbeddedEntityLink createProxy(TestContext context)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        return proxies.createProxy((String) context.get("proxyName"),
                (String) context.get("endpoint"));
    }

    @Override
    public void get(String path) throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                new URI("https://localhost:" + config.getPort() + path),
                String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Override
    public Link checkEndpointExists(Link proxyLink, String endpointName) {
        Endpoint endpoint = proxyLink.follow(Proxy.class)
                .getEndpoint(endpointName);
        assertThat(endpoint, notNullValue());
        return endpoint.getLink(Rel.SELF).stream().findFirst().get();

    }

    @Override
    public void checkEndpointAvailable(Link endpointLink) {
        assertTrue(endpointLink.follow(Endpoint.class).getProperties()
                .getAvailable());
    }

}
