package au.com.windyroad.servicegateway;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.catalina.startup.Tomcat;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;

import au.com.windyroad.hateoas.client.AutowiringDeserializer;
import au.com.windyroad.hateoas.client.EntityWrapperProxyDeserializer;
import au.com.windyroad.hateoas.client.SpringBeanHandlerInstantiator;
import au.com.windyroad.hateoas.client.WebDriverFactory;

@Configuration
public class ServiceGatewayTestConfiguration implements
        ApplicationListener<EmbeddedServletContainerInitializedEvent> {
    @Autowired
    ServiceGatewayApplication serviceGatewayApplication;

    private int port;

    @Value("${server.ssl.key-store}")
    String keyStore;

    @Value("${server.ssl.key-store-password}")
    String keyStorePassword;

    @Value("${server.ssl.key-password}")
    String keyPassword;

    @Value("${server.ssl.key-alias}")
    String keyAlias;

    @Value("${au.com.windyroad.service-gateway.ssl.hostname}")
    String sslHostname;

    @Value("${javax.net.ssl.trustStore:build/truststore.jks}")
    private String trustStoreFile;

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Bean
    public TomcatEmbeddedServletContainerFactory tomcatFactory()
            throws Exception {
        serviceGatewayKeyStoreManager();
        return new TomcatEmbeddedServletContainerFactory() {

            @Override
            protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(
                    Tomcat tomcat) {
                return super.getTomcatEmbeddedServletContainer(tomcat);
            }
        };
    }

    @Bean
    public ServiceGatewayKeyStoreManager serviceGatewayKeyStoreManager()
            throws Exception {
        if (serviceGatewayApplication.getTrustStoreLocation().equals(
                serviceGatewayApplication.systemDefaultTrustStoreLocation())) {
            LOGGER.warn(
                    "Trust Store location {} appears to be set to system default. The Self signed cert for testing will not be added and the tests will likely fail.",
                    serviceGatewayApplication.getTrustStoreLocation());
            return new ServiceGatewayKeyStoreManager(keyStore, keyStorePassword,
                    keyPassword, keyAlias, sslHostname, null, null, null);
        }
        return new ServiceGatewayKeyStoreManager(keyStore, keyStorePassword,
                keyPassword, keyAlias, sslHostname,
                serviceGatewayApplication.getTrustStoreLocation(),
                serviceGatewayApplication.getTrustStorePassword(),
                serviceGatewayApplication.getTrustStoreType());
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public URI getBaseUri() {
        return URI.create("https://" + sslHostname + ":" + getPort());
    }

    @Value("${au.com.windyroad.service-gateway.proxy.max.connections.total:100}")
    private int proxyMaxConnectionsTotal;

    @Value("${au.com.windyroad.service-gateway.proxy.max.connections.route:20}")
    private int proxyMaxConnectionsRoute;

    @Value("${au.com.windyroad.service-gateway.proxy.read.timeout.ms:60000}")
    private int proxyReadTimeoutMs;

    @Bean
    public HttpClientBuilder httpClientBuilder() throws Exception {
        HttpClientConnectionManager connectionManager = httpClientConnectionManager();
        RequestConfig config = httpClientRequestConfig();
        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(config)
                .setSSLSocketFactory(
                        serviceGatewayApplication.sslSocketFactory())
                .setSslcontext(serviceGatewayApplication.sslContext())
                .disableRedirectHandling();
    }

    @Bean
    RequestConfig httpClientRequestConfig() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(proxyReadTimeoutMs).build();
        return config;
    }

    @Bean
    Registry<ConnectionSocketFactory> httpConnectionSocketFactoryRegistry()
            throws Exception {
        return RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http",
                        PlainConnectionSocketFactory.getSocketFactory())
                .register("https", serviceGatewayApplication.sslSocketFactory())
                .build();
    }

    @Bean
    HttpClientConnectionManager httpClientConnectionManager() throws Exception {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                httpConnectionSocketFactoryRegistry());
        connectionManager.setMaxTotal(proxyMaxConnectionsTotal);
        connectionManager.setDefaultMaxPerRoute(proxyMaxConnectionsRoute);
        return connectionManager;
    }

    @Bean
    public CloseableHttpClient httpClient() throws Exception {
        return httpClientBuilder().build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpClientFactory()
            throws Exception {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(
                httpClient());
        factory.setReadTimeout(200000);
        return factory;
    }

    @Value("${security.user.name:user}")
    String name;

    @Value("${security.user.password:password}")
    String password;

    @Bean
    public BasicAuthHttpRequestIntercepter basicAuthHttpRequestIntercepter() {
        return new BasicAuthHttpRequestIntercepter(name, password);
    }

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(
                objectMapper);
        return converter;
    }

    @Bean
    public RestTemplate restTemplate() throws Exception {
        RestTemplate restTemplate = new RestTemplate(httpClientFactory());
        List<HttpMessageConverter<?>> messageConverters = restTemplate
                .getMessageConverters();
        for (int i = 0; i < messageConverters.size(); ++i) {
            if (messageConverters
                    .get(i) instanceof MappingJackson2HttpMessageConverter) {
                messageConverters.set(i, mappingJacksonHttpMessageConverter());
            }
        }
        restTemplate.setMessageConverters(messageConverters);
        restTemplate.setInterceptors(
                Arrays.asList(new ClientHttpRequestInterceptor[] {
                        basicAuthHttpRequestIntercepter() }));
        return restTemplate;
    }

    @Bean
    AsyncClientHttpRequestFactory asyncHttpClientFactory() throws Exception {
        HttpComponentsAsyncClientHttpRequestFactory factory = new HttpComponentsAsyncClientHttpRequestFactory(
                httpClient(), asyncHttpClient());
        factory.setReadTimeout(200000);
        return factory;
    }

    @Bean
    CloseableHttpAsyncClient asyncHttpClient() throws Exception {
        return asyncHttpClientBuilder().build();
    }

    @Bean
    HttpAsyncClientBuilder asyncHttpClientBuilder() throws Exception {
        NHttpClientConnectionManager connectionManager = serviceGatewayApplication
                .nHttpClientConntectionManager();
        RequestConfig config = httpClientRequestConfig();
        return HttpAsyncClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setConnectionManagerShared(true)
                .setDefaultRequestConfig(config)
                .setSSLContext(serviceGatewayApplication.sslContext());
    }

    @Bean
    public AsyncRestTemplate asyncRestTemplate() throws Exception {
        AsyncRestTemplate asyncRestTemplate = new AsyncRestTemplate(
                asyncHttpClientFactory(), restTemplate());
        List<HttpMessageConverter<?>> messageConverters = asyncRestTemplate
                .getMessageConverters();
        for (int i = 0; i < messageConverters.size(); ++i) {
            if (messageConverters
                    .get(i) instanceof MappingJackson2HttpMessageConverter) {
                messageConverters.set(i, mappingJacksonHttpMessageConverter());
            }
        }
        asyncRestTemplate.setMessageConverters(messageConverters);
        return asyncRestTemplate;
    }

    @Autowired
    private WebDriverFactory webDriverFactory;

    @Bean(destroyMethod = "quit")
    @Profile("ui-integration")
    public WebDriver webDriver()
            throws ClassNotFoundException, NoSuchMethodException,
            SecurityException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        return webDriverFactory.createWebDriver();
    }

    @Autowired
    private ApplicationContext context;

    @Bean(name = "customObjectMapperBuilder")
    @Primary
    @Profile({ "integration", "ui-integration" })
    Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.applicationContext(context);
        HandlerInstantiator handlerInstantiator = new SpringBeanHandlerInstantiator(
                context);
        // context.getAutowireCapableBeanFactory());
        builder.handlerInstantiator(handlerInstantiator);

        SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(
                    DeserializationConfig config, BeanDescription beanDesc,
                    JsonDeserializer<?> deserializer) {
                return new AutowiringDeserializer(context, deserializer);
            }
        });

        SimpleModule module2 = new SimpleModule();
        module2.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(
                    DeserializationConfig config, BeanDescription beanDesc,
                    JsonDeserializer<?> deserializer) {
                return new EntityWrapperProxyDeserializer(context,
                        deserializer);
            }
        });
        builder.modules(module, module2);

        return builder;
    }

    @Override
    public void onApplicationEvent(
            EmbeddedServletContainerInitializedEvent event) {
        this.port = event.getEmbeddedServletContainer().getPort();
    }

}
