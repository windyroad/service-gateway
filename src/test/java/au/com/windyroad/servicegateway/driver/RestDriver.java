package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URI;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import au.com.windyroad.hateoas.Control;
import au.com.windyroad.servicegateway.Context;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;

@Component
@Profile(value = "integration")
public class RestDriver implements Driver {

	public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	Proxies proxy;

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
	public void createProxy(Context context) throws Exception {

		ResponseEntity<Proxies> response = restTemplate.getForEntity(new URI(
				"https://localhost:" + config.getPort() + "/admin/proxy"),
				Proxies.class);
		Proxies proxy = response.getBody();

		Control createProxy = proxy.getControl("createProxy");
		assertThat(createProxy, notNullValue());

		MultiValueMap<String, Object> params = new LinkedMultiValueMap<String, Object>();
		for (Entry<String, Class<?>> param : createProxy.getParams().entrySet()) {
			Object value = context.get(param.getKey());
			if (param.getValue().isAssignableFrom(value.getClass())) {
				params.add(param.getKey(), value);
			}
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		HttpEntity<?> request = new HttpEntity<>(params, headers);
		URI location = restTemplate.postForLocation(createProxy.getHref(),
				request);
		context.put("proxy.location", location);
	}

	@Override
	public void get(String path) throws Exception {
		ResponseEntity<String> response = restTemplate.getForEntity(new URI(
				"https://localhost:" + config.getPort() + path), String.class);
		assertTrue(response.getStatusCode().is2xxSuccessful());
	}

	@Override
	public void checkEndpointExists(Context context) {
		ResponseEntity<Proxy> response = restTemplate.getForEntity(
				(URI) context.get("proxy.location"), Proxy.class);
		Proxy proxy = response.getBody();
		context.put("proxy", proxy);
	}

	@Override
	public void checkEndpointAvailable(Context context) {
		Proxy proxy = (Proxy) context.get("proxy");
		Boolean available = proxy.getEndpoint((String) context.get("endpoint"));
		assertTrue(available);
	}

	String normaliseUrl(String endpoint) {
		if (endpoint.startsWith("/")) {
			endpoint = "https://localhost:" + config.getPort() + endpoint;
		}
		return endpoint;
	}

}