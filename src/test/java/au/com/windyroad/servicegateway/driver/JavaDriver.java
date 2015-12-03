package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.MediaTypes;
import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.model.Endpoint;
import au.com.windyroad.servicegateway.model.EndpointEntity;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;
import au.com.windyroad.servicegateway.model.ProxyEntity;

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

    @Autowired
    CloseableHttpAsyncClient httpAsyncClient;

    private ResolvedEntity<Proxy> currentProxy;

    private ResolvedEntity<Endpoint> currentEndpoint;

    @Override
    public void clearProxies() {

    }

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

    @Override
    public void checkPingService(String path) throws Exception {
        URI url = new URI("https://localhost:" + config.getPort() + path);
        DeferredResult<HttpResponse> deferredResult = new DeferredResult<HttpResponse>();
        HttpGet newRequest = new HttpGet(url);
        newRequest.addHeader("Accept", MediaTypes.SIREN_JSON_VALUE);
        CBack callback = new CBack(deferredResult);
        Future<HttpResponse> future = httpAsyncClient.execute(newRequest,
                callback);
        HttpResponse response = future.get();
        assertTrue(HttpStatus.valueOf(response.getStatusLine().getStatusCode())
                .is2xxSuccessful());
        LOGGER.info("PING SERVICE CHECKED");
    }

    @Override
    public void createProxy(String proxyName, String endpoint)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException {
        this.currentProxy = proxies.getProperties()
                .createProxy(proxyName, endpoint).resolve(ProxyEntity.class);
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
        Entity endpoint = currentProxy.getProperties()
                .getEndpoint(endpointName);
        assertThat(endpoint, notNullValue());
        currentEndpoint = endpoint.resolve(EndpointEntity.class);

    }

    @Override
    public void checkCurrentEndpointAvailable() {
        assertTrue(currentEndpoint.getProperties().isAvailable());

    }

}
