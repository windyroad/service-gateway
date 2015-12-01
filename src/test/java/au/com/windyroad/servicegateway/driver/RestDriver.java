package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.Link;
import au.com.windyroad.hateoas.core.Relationship;
import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.model.Endpoint;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;

@Component
@Profile(value = "integration")
public class RestDriver implements Driver {

    private static class CBack implements FutureCallback<HttpResponse> {
        private DeferredResult<HttpResponse> deferredResult;

        public CBack(DeferredResult<HttpResponse> deferredResult) {
            this.deferredResult = deferredResult;
        }

        @Override
        public void completed(HttpResponse result) {
            deferredResult.setResult(result);
        }

        @Override
        public void failed(Exception ex) {
            deferredResult.setErrorResult(ex);

        }

        @Override
        public void cancelled() {
            // do nothing
        }
    }

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ServiceGatewayTestConfiguration config;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CloseableHttpAsyncClient httpAsyncClient;

    private ResolvedEntity<Proxy> currentProxy;

    private ResolvedEntity<Endpoint> currentEndpoint;

    @Override
    public void clearProxies() {

    }

    @Override
    public void checkPingService(String path) throws Exception {

        URI url = new URI("https://localhost:" + config.getPort() + path);
        DeferredResult<HttpResponse> deferredResult = new DeferredResult<HttpResponse>();
        HttpGet newRequest = new HttpGet(url);
        CBack callback = new CBack(deferredResult);
        Future<HttpResponse> future = httpAsyncClient.execute(newRequest,
                callback);
        HttpResponse response = future.get();
        assertTrue(HttpStatus.valueOf(response.getStatusLine().getStatusCode())
                .is2xxSuccessful());
    }

    @Override
    public void createProxy(String proxyName, String endpoint)
            throws RestClientException, URISyntaxException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {

        ParameterizedTypeReference<ResolvedEntity<Proxies>> type = new ParameterizedTypeReference<ResolvedEntity<Proxies>>() {
        };
        ResponseEntity<ResolvedEntity<Proxies>> response = restTemplate
                .exchange(
                        RequestEntity
                                .get(new URI("https://localhost:"
                                        + config.getPort() + "/admin/proxies"))
                        .build(), type);
        ResolvedEntity<Proxies> proxies = response.getBody();
        Map<String, String> context = new HashMap<>();
        context.put("proxyName", proxyName);
        context.put("endpoint", endpoint);
        Entity<?> createResponse = proxies.getAction("createProxy")
                .invoke(proxies, context);
        ParameterizedTypeReference<ResolvedEntity<Proxy>> proxyType = new ParameterizedTypeReference<ResolvedEntity<Proxy>>() {
        };

        currentProxy = createResponse.resolve(proxyType);
    }

    @Override
    public void get(String path) throws Exception {
        checkPingService(path);
    }

    @Override
    public void checkEndpointExists(String proxyName, String endpointPath)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Link selfLink = currentProxy.getLink(Relationship.SELF);
        assertTrue(selfLink != null);
        ParameterizedTypeReference<ResolvedEntity<Proxy>> type = new ParameterizedTypeReference<ResolvedEntity<Proxy>>() {
        };
        currentProxy = currentProxy.toLinkedEntity().resolve(type);
        Entity<Endpoint> endpoint = currentProxy.getProperties()
                .getEndpoint(endpointPath);

        assertThat(endpoint, notNullValue());
        ParameterizedTypeReference<ResolvedEntity<Endpoint>> endpointType = new ParameterizedTypeReference<ResolvedEntity<Endpoint>>() {
        };
        currentEndpoint = endpoint.resolve(endpointType);
    }

    @Override
    public void checkCurrentEndpointAvailable() {
        assertTrue(currentEndpoint.getProperties().isAvailable());
    }

    String normaliseUrl(String endpoint) {
        if (endpoint.startsWith("/")) {
            endpoint = "https://localhost:" + config.getPort() + endpoint;
        }
        return endpoint;
    }

}
