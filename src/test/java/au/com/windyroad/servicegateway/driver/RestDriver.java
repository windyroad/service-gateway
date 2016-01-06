package au.com.windyroad.servicegateway.driver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.core.FutureConverter;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.model.AdminRootController;
import au.com.windyroad.servicegateway.model.Endpoint;
import au.com.windyroad.servicegateway.model.Proxy;

@Component
@Profile(value = "integration")
public class RestDriver extends JavaDriver {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    // @Autowired
    // @Qualifier("clientRepository")
    // Repository repository;

    @Autowired
    ServiceGatewayTestConfiguration config;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    AsyncRestTemplate asyncRestTemplate;

    @Autowired
    CloseableHttpAsyncClient httpAsyncClient;

    private EntityWrapper<Proxy> currentProxy;

    private EntityWrapper<Endpoint> currentEndpoint;

    // @Autowired
    // AdminRootController arc;

    @Autowired
    ApplicationContext appContext;

    @Override
    public void clearProxies() {

    }

    // @Override
    // public void createProxy(String proxyName, String endpoint)
    // throws RestClientException, URISyntaxException,
    // IllegalAccessException, IllegalArgumentException,
    // InvocationTargetException, NoSuchMethodException, SecurityException,
    // InterruptedException, ExecutionException {
    //
    // IAdminRootController arc = (IAdminRootController) appContext
    // .getBean("adminRootController");
    // // arc.createProxy(proxyName, endpoint);
    // CompletableFuture<?> invocationResult = getRestRoot()
    // .thenApplyAsync(root -> {
    // return root.createProxy(proxyName, endpoint).join();
    // });
    //
    // currentProxy = ((Entity) invocationResult.get())
    // .resolve(ProxyController.class);
    //
    //
    // }

    @Override
    CompletableFuture<AdminRootController> getRoot() {
        URI rootUrl;
        try {
            rootUrl = new URI(
                    "https://localhost:" + config.getPort() + "/admin/proxies");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return FutureConverter.convert(asyncRestTemplate.exchange(rootUrl,
                HttpMethod.GET, null, AdminRootController.class))
                .thenApply(r -> {
                    return r.getBody();
                });
    }

    // @Override
    // public void getUrl(String path) throws Exception {
    // checkPingService(path);
    // }
    //
    // @Override
    // public void checkEndpointExists(String proxyName, String endpointPath)
    // throws IllegalAccessException, IllegalArgumentException,
    // InvocationTargetException, UnsupportedEncodingException,
    // URISyntaxException {
    // currentProxy = currentProxy.toLinkedEntity()
    // .resolve(ProxyController.class);
    // Optional<EntityRelationship> optionalEndpoint;
    // optionalEndpoint = currentProxy.getEntities().stream().filter(e -> {
    // EndpointController endpoint = e.getEntity()
    // .resolve(EndpointController.class);
    // return endpoint.getProperties().getTarget().equals(endpointPath);
    // }).findAny();
    //
    // assertTrue(optionalEndpoint.isPresent());
    // currentEndpoint = optionalEndpoint.get().getEntity()
    // .resolve(EndpointController.class);
    // }

    // @Override
    // public void checkCurrentEndpointAvailable() {
    // assertTrue(currentEndpoint.getProperties().isAvailable());
    // }

    String normaliseUrl(String endpoint) {
        if (endpoint.startsWith("/")) {
            endpoint = "https://localhost:" + config.getPort() + endpoint;
        }
        return endpoint;
    }

}
