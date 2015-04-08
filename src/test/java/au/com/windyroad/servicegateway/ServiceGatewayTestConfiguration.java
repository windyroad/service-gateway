package au.com.windyroad.servicegateway;

import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceGatewayTestConfiguration {

	@Value("${security.user.password}")
	String password;
	private int port;

	@Bean
	public TestRestTemplate restTemplate() {
		TestRestTemplate restTemplate = new TestRestTemplate("user", password);
		return restTemplate;
	}

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
}
