package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URI;

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
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResult.DeferredResultHandler;

import au.com.windyroad.hateoas.HttpLink;
import au.com.windyroad.hateoas.Link;
import au.com.windyroad.hateoas.SirenTemplate;
import au.com.windyroad.hateoas.annotations.Rel;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.TestContext;
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

    @Autowired
    SirenTemplate sirenTemplate;

    @Override
    public void clearProxies() {

    }

    @Override
    public void checkPingService(String path) throws Exception {

        URI url = new URI("https://localhost:" + config.getPort() + path);
        DeferredResult<HttpResponse> deferredResult = new DeferredResult<HttpResponse>();
        httpAsyncClient.start();
        HttpGet newRequest = new HttpGet(url);
        CBack callback = new CBack(deferredResult);
        httpAsyncClient.execute(newRequest, callback);
        deferredResult.setResultHandler(new DeferredResultHandler() {

            @Override
            public void handleResult(Object result) {
                HttpResponse response = (HttpResponse) result;
                assertThat(response.getStatusLine().getStatusCode(),
                        equalTo(HttpStatus.NO_CONTENT.value()));
                LOGGER.info("PING SERVICE CHECKED");
            }
        });
    }

    @Override
    public Link createProxy(TestContext context) throws Exception {

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
        return createResponse.getBody().getLink(Rel.SELF).stream().findFirst()
                .get();
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
        ((HttpLink) proxyLink).setRestTemplate(restTemplate);
        Proxy proxy = proxyLink.follow(Proxy.class);
        proxy.setRestTemplate(restTemplate);
        Endpoint endpoint = proxy.getEndpoint(endpointName);
        assertThat(endpoint, notNullValue());
        return endpoint.getLink(Rel.SELF).stream().findFirst().get();
    }

    @Override
    public void checkEndpointAvailable(Link endpointLink) {
        ((HttpLink) endpointLink).setRestTemplate(restTemplate);
        assertTrue(endpointLink.follow(Endpoint.class).getProperties()
                .getAvailable());
    }

    String normaliseUrl(String endpoint) {
        if (endpoint.startsWith("/")) {
            endpoint = "https://localhost:" + config.getPort() + endpoint;
        }
        return endpoint;
    }

}
