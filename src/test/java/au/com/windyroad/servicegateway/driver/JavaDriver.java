package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import au.com.windyroad.servicegateway.Proxy;

@Component
@Profile(value = "default")
public class JavaDriver implements Driver {

	@Autowired
	Proxy proxy;

	@Value("${server.port}")
	int port;

	@Autowired
	RestTemplate restTemplate;

	@Override
	public void clearProxies() {

	}

	@Override
	public void checkPingService(String path) throws Exception {
		ResponseEntity<String> response = restTemplate.getForEntity(new URI(
				"http://localhost:" + port + path), String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));

	}

	@Override
	public void createProxy(String targetEndPoint, String proxyPath) {
		proxy.createProxy(targetEndPoint, proxyPath);
	}

	@Override
	public void get(String path) throws Exception {
		ResponseEntity<String> response = restTemplate.getForEntity(new URI(
				"http://localhost:" + port + path), String.class);
		assertTrue(response.getStatusCode().is2xxSuccessful());
	}

	@Override
	public void checkEndpointExists(String endpoint) {
		assertTrue(proxy.endPointExists(endpoint));

	}

	@Override
	public void checkEndpointAvailable(String endpoint) {
		assertTrue(proxy.endPointAvailable(endpoint));
	}

}
