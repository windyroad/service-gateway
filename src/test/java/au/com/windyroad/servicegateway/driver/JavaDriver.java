package au.com.windyroad.servicegateway.driver;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import au.com.windyroad.hateoas.core.CreatedLinkedEntity;
import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.MediaTypes;
import au.com.windyroad.servicegateway.Repository;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.model.AdminRootController;
import au.com.windyroad.servicegateway.model.EndpointController;
import au.com.windyroad.servicegateway.model.ProxyController;

@Component
@Profile(value = "default")
public class JavaDriver implements Driver {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ServiceGatewayTestConfiguration config;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CloseableHttpAsyncClient httpAsyncClient;

    private ProxyController currentProxy;

    private EndpointController currentEndpoint;

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

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

    @Autowired
    ApplicationContext context;

    CompletableFuture<AdminRootController> getRoot() throws URISyntaxException {
        return repository.findOne("/admin/proxies").thenApply(entity -> {
            AdminRootController root = (AdminRootController) entity;
            return root;
        });
    }

    @Override
    public void createProxy(String proxyName, String endpoint)
            throws NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, URISyntaxException, InterruptedException,
            ExecutionException {

        this.currentProxy = getRoot().thenApplyAsync(root -> {
            return root.createProxy(proxyName, endpoint);
        }).thenApplyAsync(result -> {
            CreatedLinkedEntity cle = result.join();
            return cle.resolve(ProxyController.class);
        }).get();
    }

    @Override
    public void getUrl(String path) throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(
                new URI("https://localhost:" + config.getPort() + path),
                String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Override
    public void checkEndpointExists(String path, String endpointPath)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, UnsupportedEncodingException,
            URISyntaxException, InterruptedException, ExecutionException {
        this.currentProxy = this.currentProxy.toLinkedEntity()
                .resolve(ProxyController.class);

        Optional<EntityRelationship> optionalEndpoint;
        optionalEndpoint = currentProxy.getEntities().stream().filter(e -> {
            EndpointController endpoint = e.getEntity()
                    .resolve(EndpointController.class);
            return endpoint.getProperties().getTarget().equals(endpointPath);
        }).findAny();

        assertTrue(optionalEndpoint.isPresent());
        currentEndpoint = optionalEndpoint.get().getEntity()
                .resolve(EndpointController.class);

        // CompletableFuture<EndpointController> future = repository
        // .findOne(Endpoint.buildPath(endpointName))
        // .thenApplyAsync(endpoint -> {
        // assertThat(endpoint, notNullValue());
        // return endpoint.resolve(EndpointController.class);
        // });
        // currentEndpoint = future.join();
    }

    @Override
    public void checkCurrentEndpointAvailable() {
        assertTrue(currentEndpoint.getProperties().isAvailable());

    }

}
