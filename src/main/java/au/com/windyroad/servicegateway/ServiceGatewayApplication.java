package au.com.windyroad.servicegateway;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

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
		return sf;
	}

	@Value("${au.com.windyroad.service-gateway.proxy.max.connections.total}")
	private int proxyMaxConnections = 100;

	@Value("${au.com.windyroad.service-gateway.proxy.max.connections.route}")
	private int DEFAULT_MAX_CONNECTIONS_PER_ROUTE = 5;

	private static final int DEFAULT_READ_TIMEOUT_MILLISECONDS = (60 * 1000);

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
		connectionManager.setMaxTotal(proxyMaxConnections);
		connectionManager
				.setDefaultMaxPerRoute(DEFAULT_MAX_CONNECTIONS_PER_ROUTE);
		return connectionManager;
	}

	@Bean
	RequestConfig httpClientRequestConfig() {
		RequestConfig config = RequestConfig.custom()
				.setConnectTimeout(DEFAULT_READ_TIMEOUT_MILLISECONDS).build();
		return config;
	}

	@Bean
	public HttpAsyncClientBuilder httpAsyncClientBuilder() throws Exception {
		NHttpClientConnectionManager connectionManager = nHttpClientConntectionManager();

		return HttpAsyncClientBuilder.create().setSSLContext(sslContext())
				.setConnectionManager(connectionManager)
				.setDefaultRequestConfig(httpClientRequestConfig());
	}

	@Bean
	public CloseableHttpAsyncClient httpAsyncClient() throws Exception {
		return httpAsyncClientBuilder().build();
	}

}
