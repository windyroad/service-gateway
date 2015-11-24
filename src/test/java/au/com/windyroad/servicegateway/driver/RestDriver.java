package au.com.windyroad.servicegateway.driver;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import au.com.windyroad.hateoas.SirenTemplate;
import au.com.windyroad.hateoas.annotations.Rel;
import au.com.windyroad.hateoas2.Entity;
import au.com.windyroad.hateoas2.EntityRelationship;
import au.com.windyroad.hateoas2.NavigationalRelationship;
import au.com.windyroad.hateoas2.ResolvedEntity;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.model.Proxies;

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

    @Autowired
    SirenTemplate sirenTemplate;

    private Entity currentProxy;

    private Entity currentEndpoint;

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

        ResponseEntity<Proxies> response = restTemplate.exchange(
                RequestEntity.get(new URI("https://localhost:"
                        + config.getPort() + "/admin/proxies")).build(),
                Proxies.class);
        Proxies proxies = response.getBody();
        Map<String, String> context = new HashMap<>();
        context.put("proxyName", proxyName);
        context.put("endpoint", endpoint);
        Entity createResponse = proxies.getAction("createProxy").invoke(proxies,
                context);
        currentProxy = createResponse;
    }

    @Override
    public void get(String path) throws Exception {
        checkPingService(path);
    }

    @Override
    public void checkEndpointExists(String proxyName, String endpointPath) {
        Optional<NavigationalRelationship> selfLink = currentProxy.getLinks()
                .stream().filter(l -> l.hasNature(Rel.SELF)).findAny();
        assertTrue(selfLink.isPresent());
        currentProxy = selfLink.get().getLink().resolve(ResolvedEntity.class);
        Optional<EntityRelationship> endpoint = currentProxy.getEntities()
                .stream().filter(e -> e.getEntity().getProperties()
                        .getProperty("target").equals(endpointPath))
                .findAny();
        assertTrue(endpoint.isPresent());
        currentEndpoint = endpoint.get().getEntity();
    }

    @Override
    public void checkCurrentEndpointAvailable() {
        assertTrue(Boolean.parseBoolean(
                currentEndpoint.getProperties().getProperty("available")));
    }

    String normaliseUrl(String endpoint) {
        if (endpoint.startsWith("/")) {
            endpoint = "https://localhost:" + config.getPort() + endpoint;
        }
        return endpoint;
    }

}
