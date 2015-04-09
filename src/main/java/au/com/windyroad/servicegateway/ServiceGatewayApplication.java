package au.com.windyroad.servicegateway;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

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
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.conn.NHttpClientConnectionManager;
import org.apache.http.nio.conn.NoopIOSessionStrategy;
import org.apache.http.nio.conn.SchemeIOSessionStrategy;
import org.apache.http.nio.conn.ssl.SSLIOSessionStrategy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ServiceGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceGatewayApplication.class, args);
	}

	@Bean
	public SSLContext sslContext() throws Exception {
		SSLContext sslContext = SSLContext.getInstance("TLS");
		TrustManagerFactory tmf = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		KeyStore ks = KeyStore.getInstance("JKS");
		File trustFile = new File("build/truststore.jks");
		ks.load(new FileInputStream(trustFile), null);
		tmf.init(ks);
		sslContext.init(null, tmf.getTrustManagers(), null);
		return sslContext;
	}

	@Bean
	public SSLConnectionSocketFactory sslSocketFactory() throws Exception {
		SSLConnectionSocketFactory sf = new SSLConnectionSocketFactory(
				sslContext());
		// sf.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
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

	public RestTemplate restTemplate() throws Exception {
		RestTemplate restTemplate = new RestTemplate(httpClientFactory());
		return restTemplate;
	}

	@Bean
	public AsyncRestTemplate asyncRestTemplate() throws Exception {
		return new AsyncRestTemplate(asyncRequestFactory(), restTemplate());
	}

	@Bean
	public HttpAsyncClientBuilder httpAsyncClientBuilder() throws Exception {
		NHttpClientConnectionManager connectionManager = nHttpClientConntectionManager();

		return HttpAsyncClientBuilder.create().setSSLContext(sslContext())
				.setConnectionManager(connectionManager)
				.setDefaultRequestConfig(httpClientRequestConfig());
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
		connectionManager.setMaxTotal(DEFAULT_MAX_TOTAL_CONNECTIONS);
		connectionManager
				.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
		return connectionManager;
	}

	@Bean
	public CloseableHttpAsyncClient httpAsyncClient() throws Exception {
		return httpAsyncClientBuilder().build();
	}

	@Bean
	public AsyncClientHttpRequestFactory asyncRequestFactory() {
		return new HttpComponentsAsyncClientHttpRequestFactory();
	}

}
