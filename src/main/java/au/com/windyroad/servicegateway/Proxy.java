package au.com.windyroad.servicegateway;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RestController
public class Proxy {
	public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private static class Callback implements
			ListenableFutureCallback<ResponseEntity<String>> {

		public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

		private DeferredResult<ResponseEntity<?>> deferredResult;

		private Map<String, Boolean> endpoints;

		private String target;

		public Callback(DeferredResult<ResponseEntity<?>> deferredResult,
				String target, Map<String, Boolean> endpoints) {
			this.deferredResult = deferredResult;
			this.endpoints = endpoints;
			this.target = target;
		}

		@Override
		public void onSuccess(ResponseEntity<String> result) {
			deferredResult.setResult(result);
			endpoints.put(target, true);
		}

		@Override
		public void onFailure(Throwable ex) {
			LOGGER.error("Failure while processing", ex);
			endpoints.put(target, false);
		}
	}

	private Map<String, String> proxies = new HashMap<>();
	private Map<String, Boolean> endpoints = new HashMap<>();

	public void createProxy(String targetEndPoint, String proxyPath) {
		targetEndPoint = normaliseUrl(targetEndPoint);
		proxies.put(proxyPath, targetEndPoint);
	}

	@RequestMapping("/proxy/{name}/**")
	public DeferredResult<ResponseEntity<?>> get(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@PathVariable("name") String name) {
		DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<ResponseEntity<?>>();

		AsyncRestTemplate restTemplate = new AsyncRestTemplate();

		if (proxies.containsKey(name)) {
			String url = (String) request
					.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
			String restOfTheUrl = url.replace("/proxy/" + name + "/", "");
			String target = proxies.get(name) + "/" + restOfTheUrl;
			endpoints.put(target, false);
			HttpMethod method = HttpMethod.GET;
			Class<String> responseType = String.class;

			HttpHeaders headers = copyHeaders(request);
			HttpEntity<String> requestEntity = new HttpEntity<String>("params",
					headers);
			ListenableFuture<ResponseEntity<String>> future = restTemplate
					.exchange(target, method, requestEntity, responseType);

			future.addCallback(new Callback(deferredResult, target, endpoints));

		} else {
			deferredResult.setResult(ResponseEntity.notFound().build());
		}
		return deferredResult;
	}

	HttpHeaders copyHeaders(final HttpServletRequest request) {
		HttpHeaders headers = new HttpHeaders();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			headers.put(headerName,
					Collections.list(request.getHeaders(headerName)));
		}
		return headers;
	}

	public boolean endPointExists(String endpoint) {
		endpoint = normaliseUrl(endpoint);
		return endpoints.containsKey(endpoint);
	}

	String normaliseUrl(String endpoint) {
		if (endpoint.startsWith("/")) {
			endpoint = "http://localhost:8080" + endpoint;
		}
		return endpoint;
	}

	public boolean endPointAvailable(String endpoint) {
		endpoint = normaliseUrl(endpoint);
		return endpoints.get(endpoint);
	}
}
