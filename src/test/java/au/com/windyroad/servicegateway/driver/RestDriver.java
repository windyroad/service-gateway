package au.com.windyroad.servicegateway.driver;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.core.FutureConverter;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.model.AdminRootController;
import au.com.windyroad.servicegateway.model.Endpoint;
import au.com.windyroad.servicegateway.model.EndpointController;
import au.com.windyroad.servicegateway.model.IAdminRootController;
import au.com.windyroad.servicegateway.model.Proxy;
import au.com.windyroad.servicegateway.model.ProxyController;

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

    @Override
    public void createProxy(String proxyName, String endpoint)
            throws RestClientException, URISyntaxException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException,
            InterruptedException, ExecutionException {

        IAdminRootController arc = (IAdminRootController) appContext
                .getBean("adminRootController");
        // arc.createProxy(proxyName, endpoint);
        CompletableFuture<?> invocationResult = getRestRoot()
                .thenApplyAsync(root -> {
                    return root.createProxy(proxyName, endpoint).join();
                });

        currentProxy = ((Entity) invocationResult.get())
                .resolve(ProxyController.class);

        /*
         * Enhancer e = new Enhancer();
         * e.setClassLoader(this.getClass().getClassLoader());
         * e.setSuperclass(ProxyController.class); e.setCallback(new
         * MethodInterceptor() {
         * 
         * @Override public Object intercept(Object obj, Method method, Object[]
         * args, MethodProxy proxy) throws Throwable {
         * 
         * Map<String, Object> context = new HashMap<>();
         * 
         * Parameter[] params = method.getParameters(); for (int i = 0; i <
         * params.length; ++i) { context.put(params[i].getName(), args[i]); }
         * CompletableFuture<Entity> xxxResult = (CompletableFuture<Entity>)
         * currentProxy .getAction(method.getName()).invoke(context); return
         * xxxResult; } });
         * 
         * // create proxy using SomeConcreteClass() no-arg constructor
         * ProxyController myProxy = (ProxyController) e.create(); // create
         * proxy using SomeConcreteClass(String) constructor
         * CompletableFuture<UpdatedLinkedEntity> result = myProxy
         * .setEndpoint("foo", false);
         * 
         * result.thenAccept(entityLink -> { assertTrue(entityLink != null);
         * EndpointController ep = entityLink
         * .resolve(EndpointController.class);
         * assertFalse(ep.getProperties().isAvailable()); }).get();
         */
    }

    CompletableFuture<AdminRootController> getRestRoot()
            throws URISyntaxException {
        URI rootUrl = new URI(
                "https://localhost:" + config.getPort() + "/admin/proxies");

        return FutureConverter.convert(asyncRestTemplate.exchange(rootUrl,
                HttpMethod.GET, null, AdminRootController.class))
                .thenApply(r -> {
                    return r.getBody();
                });
    }

    @Override
    public void get(String path) throws Exception {
        checkPingService(path);
    }

    @Override
    public void checkEndpointExists(String proxyName, String endpointPath)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, UnsupportedEncodingException,
            URISyntaxException {
        currentProxy = currentProxy.toLinkedEntity()
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
