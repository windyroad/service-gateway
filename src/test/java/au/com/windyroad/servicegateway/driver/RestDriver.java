package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import au.com.windyroad.hateoas.Action;
import au.com.windyroad.hateoas.Entity;
import au.com.windyroad.hateoas.Field;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.TestContext;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;

@Component
@Profile(value = "integration")
public class RestDriver implements Driver {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Proxies proxy;

    @Autowired
    ServiceGatewayTestConfiguration config;

    @Autowired
    RestTemplate restTemplate;

    @Override
    public void clearProxies() {

    }

    @Override
    public void checkPingService(String path) throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                new URI("https://localhost:" + config.getPort() + path),
                String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
        LOGGER.info("PING SERVICE CHECKED");
    }

    @Override
    public void createProxy(TestContext context) throws Exception {

        ParameterizedTypeReference<Entity<Proxies>> type = new ParameterizedTypeReference<Entity<Proxies>>() {
        };
        ResponseEntity<Entity<Proxies>> response = restTemplate
                .exchange(
                        RequestEntity
                                .get(new URI("https://localhost:"
                                        + config.getPort() + "/admin/proxies"))
                        .build(), type);
        Entity<Proxies> proxy = response.getBody();

        Action createProxy = proxy.getAction("createProxy");
        assertThat(createProxy, notNullValue());

        MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
        for (Field field : createProxy.getFields()) {
            Object value = field.getValue();
            if (field.getType() != "hidden") {
                value = context.get(field.getName());
            }
            params.add(field.getName(), value);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<?> request = new HttpEntity<>(params, headers);
        URI location = restTemplate.postForLocation(createProxy.getHref(),
                request);
        context.put("proxy.location", location);
    }

    @Override
    public void get(String path) throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                new URI("https://localhost:" + config.getPort() + path),
                String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Override
    public void checkEndpointExists(TestContext context) {
        ResponseEntity<Proxy> response = restTemplate
                .getForEntity((URI) context.get("proxy.location"), Proxy.class);
        Proxy proxy = response.getBody();
        assertThat(proxy.getName(), equalTo(context.get("proxyName")));
        assertThat(proxy.getEndpoint((String) context.get("endpoint")),
                notNullValue());
        context.put("proxy", proxy);
    }

    @Override
    public void checkEndpointAvailable(TestContext context) {
        Proxy proxy = (Proxy) context.get("proxy");
        Boolean available = proxy.getEndpoint((String) context.get("endpoint"));
        assertTrue(available);
    }

    String normaliseUrl(String endpoint) {
        if (endpoint.startsWith("/")) {
            endpoint = "https://localhost:" + config.getPort() + endpoint;
        }
        return endpoint;
    }

}
