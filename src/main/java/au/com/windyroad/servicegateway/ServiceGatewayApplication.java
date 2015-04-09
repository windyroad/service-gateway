package au.com.windyroad.servicegateway;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.client.HttpAsyncClient;
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

	@Bean
	public HttpClientBuilder httpClientBuilder() throws Exception {
		return HttpClientBuilder.create()
				.setSSLSocketFactory(sslSocketFactory())
				.setSslcontext(sslContext()).disableRedirectHandling();
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
		return HttpAsyncClientBuilder.create().setSSLContext(sslContext());
	}

	@Bean
	public HttpAsyncClient httpAsyncClient() throws Exception {
		return httpAsyncClientBuilder().build();
	}

	@Bean
	public AsyncClientHttpRequestFactory asyncRequestFactory() {
		return new HttpComponentsAsyncClientHttpRequestFactory();
	}

}
