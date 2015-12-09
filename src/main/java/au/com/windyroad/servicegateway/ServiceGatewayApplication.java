package au.com.windyroad.servicegateway;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Locale;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.servicegateway.model.AdminRoot;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("au.com.windyroad")
public class ServiceGatewayApplication {

    @Value("${au.com.windyroad.service-gateway.proxy.max.connections.total:100}")
    private int proxyMaxConnectionsTotal;

    @Value("${au.com.windyroad.service-gateway.proxy.max.connections.route:20}")
    private int proxyMaxConnectionsRoute;

    @Value("${au.com.windyroad.service-gateway.proxy.read.timeout.ms:60000}")
    private int proxyReadTimeoutMs;

    @Value("${server.ssl.protocol:TLS}")
    String sslProtocol;

    @Value("${javax.net.ssl.trustStore:}")
    private String trustStore;

    @Value("${javax.net.ssl.trustStorePassword:changeit}")
    private String trustStorePassword;

    @Value("${javax.net.ssl.trustStoreType:JKS}")
    private String trustStoreType;

    public String getTrustStoreLocation() {
        if (StringUtils.hasLength(trustStore)) {
            return trustStore;
        }
        String locationProperty = System
                .getProperty("javax.net.ssl.trustStore");
        if (StringUtils.hasLength(locationProperty)) {
            return locationProperty;
        } else {
            return systemDefaultTrustStoreLocation();
        }
    }

    public String systemDefaultTrustStoreLocation() {
        String javaHome = System.getProperty("java.home");
        FileSystemResource location = new FileSystemResource(
                javaHome + "/lib/security/jssecacerts");
        if (location.exists()) {
            return location.getFilename();
        } else {
            return javaHome + "/lib/security/cacerts";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ServiceGatewayApplication.class, args);
    }

    @Bean
    KeyStore trustStore()
            throws KeyStoreException, IOException, NoSuchAlgorithmException,
            CertificateException, FileNotFoundException {
        KeyStore ks = KeyStore.getInstance(trustStoreType);

        File trustFile = new File(getTrustStoreLocation());
        ks.load(new FileInputStream(trustFile),
                trustStorePassword.toCharArray());
        return ks;
    }

    @Bean
    TrustManagerFactory trustManagerFactory() throws NoSuchAlgorithmException {
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        return tmf;
    }

    @Bean
    public SSLContext sslContext() throws Exception {
        SSLContext sslContext = SSLContext.getInstance(sslProtocol);
        TrustManagerFactory tmf = trustManagerFactory();
        KeyStore ks = trustStore();
        tmf.init(ks);
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;
    }

    @Bean
    public SSLConnectionSocketFactory sslSocketFactory() throws Exception {
        SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(
                sslContext());
        return sf;
    }

    @Bean
    Registry<SchemeIOSessionStrategy> schemeIOSessionStrategyRegistry()
            throws Exception {
        return RegistryBuilder.<SchemeIOSessionStrategy> create()
                .register("http", NoopIOSessionStrategy.INSTANCE)
                .register("https", new SSLIOSessionStrategy(sslContext()))
                .build();
    }

    @Bean
    public NHttpClientConnectionManager nHttpClientConntectionManager()
            throws Exception {
        PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(
                new DefaultConnectingIOReactor(IOReactorConfig.DEFAULT),
                schemeIOSessionStrategyRegistry());
        connectionManager.setMaxTotal(proxyMaxConnectionsTotal);
        connectionManager.setDefaultMaxPerRoute(proxyMaxConnectionsRoute);
        return connectionManager;
    }

    @Bean
    RequestConfig httpClientRequestConfig() {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(proxyReadTimeoutMs).build();
        return config;
    }

    @Bean
    public HttpAsyncClientBuilder httpAsyncClientBuilder() throws Exception {
        return HttpAsyncClientBuilder.create().setSSLContext(sslContext())
                .setConnectionManager(nHttpClientConntectionManager())
                .setDefaultRequestConfig(httpClientRequestConfig());
    }

    @Bean(destroyMethod = "close")
    public CloseableHttpAsyncClient httpAsyncClient() throws Exception {
        CloseableHttpAsyncClient client = httpAsyncClientBuilder().build();
        client.start();
        return client;
    }

    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public String getTrustStoreType() {
        return trustStoreType;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.ENGLISH);
        return slr;
    }

    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:locale/messages");
        messageSource.setCacheSeconds(3600); // refresh cache once per hour
        return messageSource;
    }

    @Autowired
    ApplicationContext context;

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    @Bean
    public EntityWrapper<AdminRoot> proxies() throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        int autowireMode;
        boolean dependencyCheck;
        EntityWrapper<AdminRoot> resolvedEntity = new EntityWrapper<AdminRoot>(
                context, repository, "/admin/proxies", new AdminRoot(),
                "Service Gateway");
        AutowiredAnnotationBeanPostProcessor bpp = new AutowiredAnnotationBeanPostProcessor();
        bpp.setBeanFactory(context.getAutowireCapableBeanFactory());
        bpp.processInjection(resolvedEntity);
        repository.put(resolvedEntity);
        return resolvedEntity;
    }

}
