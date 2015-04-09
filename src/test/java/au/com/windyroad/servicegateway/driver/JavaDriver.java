package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import au.com.windyroad.servicegateway.Proxy;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;

@Component
@Profile(value = "default")
public class JavaDriver implements Driver {

	public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	Proxy proxy;

	@Autowired
	ServiceGatewayTestConfiguration config;

	@Autowired
	RestTemplate restTemplate;

	@Override
	public void clearProxies() {

	}

	@Override
	public void checkPingService(String path) throws Exception {
		ResponseEntity<String> response = restTemplate.getForEntity(new URI(
				"https://localhost:" + config.getPort() + path), String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
		LOGGER.info("PING SERVICE CHECKED");
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
			endpoint = "https://localhost:" + config.getPort() + endpoint;
		}
		return endpoint;
	}

}
