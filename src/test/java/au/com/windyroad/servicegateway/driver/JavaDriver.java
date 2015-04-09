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
		// HttpClient client = new DefaultHttpClient();
		// SSLContext sslContext = SSLContext.getInstance("TLS");
		//
		// TrustManagerFactory tmf = TrustManagerFactory
		// .getInstance(TrustManagerFactory.getDefaultAlgorithm());
		// KeyStore ks = KeyStore.getInstance("JKS");
		// File trustFile = new File("build/truststore.jks");
		// ks.load(new FileInputStream(trustFile), null);
		// tmf.init(ks);
		// sslContext.init(null, tmf.getTrustManagers(), null);
		// SSLSocketFactory sf = new SSLSocketFactory(sslContext);
		// sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		// Scheme scheme = new Scheme("https", sf, config.getPort());
		// client.getConnectionManager().getSchemeRegistry().register(scheme);
		// HttpGet httpGet = new HttpGet("https://localhost:" + config.getPort()
		// + "/health");
		// HttpResponse httpResponse = client.execute(httpGet);

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
