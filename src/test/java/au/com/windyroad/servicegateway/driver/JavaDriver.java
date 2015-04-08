package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import au.com.windyroad.servicegateway.Proxy;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;

@Component
@Profile(value = "default")
public class JavaDriver implements Driver {

	@Autowired
	Proxy proxy;

	@Autowired
	ServiceGatewayTestConfiguration config;

	@Autowired
	TestRestTemplate restTemplate;

	@Override
	public void clearProxies() {

	}

	@Override
	public void checkPingService(String path) throws Exception {
		ResponseEntity<String> response = restTemplate.getForEntity(new URI(
				"https://localhost:" + config.getPort() + path), String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));

	}

	@Override
	public void createProxy(String targetEndPoint, String proxyPath) {
		proxy.createProxy(normaliseUrl(targetEndPoint), proxyPath);
	}

	@Override
	public void get(String path) throws Exception {
		ResponseEntity<String> response = restTemplate.getForEntity(new URI(
				"https://localhost:" + config.getPort() + path), String.class);
		assertTrue(response.getStatusCode().is2xxSuccessful());
	}

	@Override
	public void checkEndpointExists(String endpoint) {
		assertTrue(proxy.endPointExists(normaliseUrl(endpoint)));

	}

	@Override
	public void checkEndpointAvailable(String endpoint) {
		assertTrue(proxy.endPointAvailable(normaliseUrl(endpoint)));
	}

	String normaliseUrl(String endpoint) {
		if (endpoint.startsWith("/")) {
			endpoint = "http://localhost:" + config.getPort() + endpoint;
		}
		return endpoint;
	}

}
