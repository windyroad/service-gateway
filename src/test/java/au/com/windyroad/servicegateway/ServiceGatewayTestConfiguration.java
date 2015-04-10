package au.com.windyroad.servicegateway;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.catalina.startup.Tomcat;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ServiceGatewayTestConfiguration {

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

	@Value("${server.ssl.protocol:TLS}")
	String sslProtocol;

	@Value("${javax.net.ssl.trustStore:build/truststore.jks}")
	private String trustStoreFile;

	@Value("${javax.net.ssl.trustStorePassword:changeit}")
	private String trustStorePassword;

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
		return new ServiceGatewayKeyStoreManager(keyStore, keyStorePassword,
				keyPassword, keyAlias, sslHostname, trustStoreFile,
				trustStorePassword);
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	@Bean
	public SSLContext sslContext() throws Exception {
		SSLContext sslContext = SSLContext.getInstance(sslProtocol);
		TrustManagerFactory tmf = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		KeyStore ks = KeyStore.getInstance("JKS");
		File trustFile = new File(trustStoreFile);
		ks.load(new FileInputStream(trustFile), null);
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

	private static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 100;

	private static final int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;

	private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);

	@Bean
	public HttpClientBuilder httpClientBuilder() throws Exception {
		HttpClientConnectionManager connectionManager = httpClientConnectionManager();
		RequestConfig config = httpClientRequestConfig();
		return HttpClientBuilder.create()
				.setConnectionManager(connectionManager)
				.setDefaultRequestConfig(config)
				.setSSLSocketFactory(sslSocketFactory())
				.setSslcontext(sslContext()).disableRedirectHandling();
	}

	@Bean
	RequestConfig httpClientRequestConfig() {
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS).build();
		return config;
	}

	@Bean
	Registry<ConnectionSocketFactory> httpConnectionSocketFactoryRegistry()
			throws Exception {
		return RegistryBuilder
				.<ConnectionSocketFactory> create()
				.register("http",
						PlainConnectionSocketFactory.getSocketFactory())
				.register("https", sslSocketFactory()).build();
	}

	@Bean
	HttpClientConnectionManager httpClientConnectionManager() throws Exception {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
				httpConnectionSocketFactoryRegistry());
		connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
		connectionManager
				.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
		return connectionManager;
	}

	@Bean
	public HttpClient httpClient() throws Exception {
		return httpClientBuilder().build();
	}

	@Bean
	public HttpComponentsClientHttpRequestFactory httpClientFactory()
			throws Exception {
		return new HttpComponentsClientHttpRequestFactory(httpClient());
	}

	@Value("${security.user.password}")
	String password;

	@Bean
	public BasicAuthHttpRequestIntercepter basicAuthHttpRequestIntercepter() {
		return new BasicAuthHttpRequestIntercepter(password);
	}

	@Bean
	public RestTemplate restTemplate() throws Exception {
		RestTemplate restTemplate = new RestTemplate(httpClientFactory());
		restTemplate
				.setInterceptors(Arrays
						.asList(new ClientHttpRequestInterceptor[] { basicAuthHttpRequestIntercepter() }));
		return restTemplate;
	}

}
