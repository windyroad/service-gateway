package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.server.annotations.HateoasAction;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.model.EndpointEntity;
import au.com.windyroad.servicegateway.model.ProxiesEntity;
import au.com.windyroad.servicegateway.model.Proxy;
import au.com.windyroad.servicegateway.model.ProxyEntity;

@Component
@Profile(value = "integration")
public class RestDriver extends JavaDriver {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ServiceGatewayTestConfiguration config;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CloseableHttpAsyncClient httpAsyncClient;

    private ProxyEntity currentProxy;

    private EndpointEntity currentEndpoint;

    private Map<String, String> context = new HashMap<>();

    @Override
    public void clearProxies() {

    }

    @Override
    public void createProxy(String proxyName, String endpoint)
            throws RestClientException, URISyntaxException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException,
            SecurityException {
        context.put("proxyName", proxyName);
        context.put("endpoint", endpoint);

        currentProxy = getRoot().getAction("createProxy").invoke(context)
                .resolve(ProxyEntity.class);

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
                    return currentProxy.getAction(method.getName())
                            .invoke(context).resolve(ProxyEntity.class);

                } else {
                    throw new RuntimeException("`" + method.getName()
                            + "` is not remotely callable");
                }
            }
        });
        // create proxy using SomeConcreteClass() no-arg constructor
        Proxy myProxy = (Proxy) e.create();
        // create proxy using SomeConcreteClass(String) constructor
        myProxy.setEndpoint("foo", "false");

    }

    ProxiesEntity getRoot() throws URISyntaxException {
        URI rootUrl = new URI(
                "https://localhost:" + config.getPort() + "/admin/proxies");

        return restTemplate.getForEntity(rootUrl, ProxiesEntity.class)
                .getBody();
    }

    @Override
    public void get(String path) throws Exception {
        checkPingService(path);
    }

    @Override
    public void checkEndpointExists(String proxyName, String endpointPath)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        currentProxy = currentProxy.refresh();
        Entity endpoint = currentProxy.getProperties()
                .getEndpoint(endpointPath);

        assertThat(endpoint, notNullValue());
        currentEndpoint = endpoint.resolve(EndpointEntity.class);
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
