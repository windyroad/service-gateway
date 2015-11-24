package au.com.windyroad.servicegateway;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.catalina.startup.Tomcat;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import au.com.windyroad.hateoas.SirenTemplate;
import au.com.windyroad.servicegateway.driver.WebDriverFactory;

@Configuration
public class ServiceGatewayTestConfiguration {
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
    public HttpClient httpClient() throws Exception {
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

    @Autowired
    List<ObjectMapper> objectMappers;

    @Autowired
    @Qualifier("customObjectMapperBuilder")
    Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;

    @Bean
    public HttpMessageConverters messageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(mappingJacksonHttpMessageConverter());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        return new HttpMessageConverters(false, converters);
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJacksonHttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(
                objectMapper);
        return converter;
    }

    @Bean
    public RestTemplate restTemplate() throws Exception {
        RestTemplate restTemplate = new RestTemplate(
                messageConverters().getConverters());
        restTemplate.setRequestFactory(httpClientFactory());

        restTemplate.setInterceptors(
                Arrays.asList(new ClientHttpRequestInterceptor[] {
                        basicAuthHttpRequestIntercepter() }));
        return restTemplate;
    }

    @Bean
    public SirenTemplate sirenTemplate() throws Exception {
        SirenTemplate sirenTemplate = new SirenTemplate(restTemplate());
        return sirenTemplate;
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

}
