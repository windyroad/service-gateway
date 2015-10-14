package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import au.com.windyroad.hateoas.SirenTemplate;
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

    @Autowired
    SirenTemplate sirenTemplate;

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

        ParameterizedTypeReference<Proxies> type = new ParameterizedTypeReference<Proxies>() {
        };
        ResponseEntity<Proxies> response = restTemplate
                .exchange(
                        RequestEntity
                                .get(new URI("https://localhost:"
                                        + config.getPort() + "/admin/proxies"))
                        .build(), type);
        Proxies proxies = response.getBody();

        ParameterizedTypeReference<Proxy> proxyType = new ParameterizedTypeReference<Proxy>() {
        };
        ResponseEntity<Proxy> createResponse = sirenTemplate
                .executeForLocation(proxies, "createProxy", context, proxyType);
        context.put("proxy", createResponse.getBody());
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
        Proxy proxy = (Proxy) context.get("proxy");
        assertThat(proxy.getName(), equalTo(context.get("proxyName")));
        assertThat(proxy.getEndpoint((String) context.get("endpoint")),
                notNullValue());
        context.put("proxy", proxy);
    }

    @Override
    public void checkEndpointAvailable(TestContext context) {
        Proxy proxy = (Proxy) context.get("proxy");
        Boolean available = proxy.getEndpoint((String) context.get("endpoint"))
                .getProperties().getAvailable();
        assertTrue(available);
    }

    String normaliseUrl(String endpoint) {
        if (endpoint.startsWith("/")) {
            endpoint = "https://localhost:" + config.getPort() + endpoint;
        }
        return endpoint;
    }

}
