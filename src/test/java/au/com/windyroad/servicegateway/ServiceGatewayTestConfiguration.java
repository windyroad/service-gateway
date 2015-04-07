package au.com.windyroad.servicegateway;

import org.springframework.beans.factory.annotation.Value;
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

	public void setPort(int port) {
		this.port = port;
	}

	public int getPort() {
		return port;
	}
}
