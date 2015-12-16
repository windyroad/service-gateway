package au.com.windyroad.servicegateway.driver;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
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
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.model.AdminRoot;
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

    private Map<String, String> context = new HashMap<>();

    @Override
    public void clearProxies() {

    }

    @Override
    public void createProxy(String proxyName, String endpoint)
            throws RestClientException, URISyntaxException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException,
            InterruptedException, ExecutionException {
        context.put("proxyName", proxyName);
        context.put("endpoint", endpoint);

        CompletableFuture<?> invocationResult = getRoot()
                .thenApplyAsync(root -> {
                    return root.getAction("createProxy");
                }).thenApplyAsync(action -> {
                    CompletableFuture<?> result = action.invoke(context);
                    return result.join();
                });

        currentProxy = ((Entity) invocationResult.get())
                .resolve(Proxy.wrapperType());

        Enhancer e = new Enhancer();
        e.setClassLoader(this.getClass().getClassLoader());
        e.setSuperclass(Proxy.class);
        e.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args,
                    MethodProxy proxy) throws Throwable {
                HateoasAction hateoasAction = method
                        .getAnnotation(HateoasAction.class);
                if (hateoasAction != null) {
                    Parameter[] params = method.getParameters();
                    for (int i = 0; i < params.length; ++i) {
                        context.put(params[i].getName(), args[i].toString());
                    }
                    CompletableFuture<Entity> xxxResult = (CompletableFuture<Entity>) currentProxy
                            .getAction(method.getName()).invoke(context);
                    return xxxResult.get().resolve(Proxy.wrapperType());

                } else {
                    throw new RuntimeException("`" + method.getName()
                            + "` is not remotely callable");
                }
            }
        });
        // create proxy using SomeConcreteClass() no-arg constructor
        Proxy myProxy = (Proxy) e.create();
        // create proxy using SomeConcreteClass(String) constructor
        // myProxy.setEndpoint("foo", "false");

    }

    CompletableFuture<EntityWrapper<AdminRoot>> getRoot()
            throws URISyntaxException {
        URI rootUrl = new URI(
                "https://localhost:" + config.getPort() + "/admin/proxies");

        return FutureConverter
                .convert(asyncRestTemplate.exchange(rootUrl, HttpMethod.GET,
                        null, AdminRoot.wrapperType()))
                .thenApply(r -> r.getBody());
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
        currentProxy = currentProxy.refresh();
        Optional<EntityRelationship> optionalEndpoint;
        optionalEndpoint = currentProxy.getEntities().stream().filter(e -> {
            EntityWrapper<Endpoint> endpoint = e.getEntity()
                    .resolve(Endpoint.wrapperType());
            return endpoint.getProperties().getTarget().equals(endpointPath);
        }).findAny();

        assertTrue(optionalEndpoint.isPresent());
        currentEndpoint = optionalEndpoint.get().getEntity()
                .resolve(Endpoint.wrapperType());
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
