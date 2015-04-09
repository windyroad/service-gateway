package au.com.windyroad.servicegateway;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.catalina.startup.Tomcat;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
public class ServiceGatewayTestConfiguration {

	@Value("${security.user.password}")
	String password;
	private int port;

	@Value("${server.ssl.key-store}")
	String keyStore;

	@Value("${server.ssl.key-store-password}")
	String keyStorePassword;

	@Value("${server.ssl.key-password}")
	String keyPassword;

	public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatFactory()
			throws Exception {
		LOGGER.info("tomcatFactory");
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
		LOGGER.info("serviceGatewayKeyStoreManager");

		return new ServiceGatewayKeyStoreManager(keyStore, keyStorePassword,
				keyPassword);
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
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

	@Bean
	public TestRestTemplate restTemplate() throws Exception {
		TestRestTemplate restTemplate = new TestRestTemplate("user", password);
		restTemplate.setRequestFactory(httpClientFactory());
		return restTemplate;
	}

}
