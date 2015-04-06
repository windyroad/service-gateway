package au.com.windyroad.servicegateway;

import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ServiceGatewayTestConfiguration {

	@Bean
	RestTemplate restTemplate() {
		RestTemplate restTemplate = new TestRestTemplate();
		return restTemplate;
	}
}
