package au.com.windyroad.servicegateway;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.HandlerMapping;

@Component
@RestController
public class Proxy {
	public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private static class CBack implements FutureCallback<HttpResponse> {
		private CloseableHttpAsyncClient httpAsyncClient;
		private DeferredResult<ResponseEntity<?>> deferredResult;
		private String target;
		private Map<String, Boolean> endpoints;

		private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

		public CBack(CloseableHttpAsyncClient httpAsyncClient,
				DeferredResult<ResponseEntity<?>> deferredResult,
				String target, Map<String, Boolean> endpoints) {
			this.httpAsyncClient = httpAsyncClient;
			this.deferredResult = deferredResult;
			this.target = target;
			this.endpoints = endpoints;
		}

		@Override
		public void failed(Exception ex) {
			LOGGER.error("Failure while processing: ", ex);
			endpoints.put(target, false);
			deferredResult.setErrorResult(ex);
			close();
		}

		@Override
		public void completed(HttpResponse result) {
			try {
				HttpHeaders httpHeaders = getHeaders(result);
				HttpStatus httpStatus = HttpStatus.valueOf(result
						.getStatusLine().getStatusCode());
				HttpEntity entity = result.getEntity();
				ResponseEntity<InputStreamResource> responseEntity;
				if (entity != null) {
					InputStreamResource inputStreamResource = new InputStreamResource(
							entity.getContent());

					responseEntity = new ResponseEntity<InputStreamResource>(
							inputStreamResource, httpHeaders, httpStatus);
				} else {
					responseEntity = new ResponseEntity<InputStreamResource>(
							httpHeaders, httpStatus);
				}
				deferredResult.setResult(responseEntity);
				endpoints.put(target, true);

			} catch (Exception e) {
				LOGGER.error("Failure while processing response:", e);
				deferredResult.setErrorResult(e);
			} finally {
				close();
			}
		}

		HttpHeaders getHeaders(HttpResponse result) {
			HttpHeaders httpHeaders = new HttpHeaders();
			HeaderIterator headerIterator = result.headerIterator();
			while (headerIterator.hasNext()) {
				Header header = headerIterator.nextHeader();
				httpHeaders.add(header.getName(), header.getValue());
			}
			return httpHeaders;
		}

		void close() {
			try {
				httpAsyncClient.close();
			} catch (IOException e) {
				LOGGER.error("Failure while closing:", e);
			}
		}

		@Override
		public void cancelled() {
			close();
		}
	}

	private Map<String, String> proxies = new HashMap<>();
	private Map<String, Boolean> endpoints = new HashMap<>();

	public void createProxy(String targetEndPoint, String proxyPath) {
		proxies.put(proxyPath, targetEndPoint);
	}

	@Autowired
	CloseableHttpAsyncClient httpAsyncClient;

	@RequestMapping("/proxy/{name}/**")
	public DeferredResult<ResponseEntity<?>> get(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@PathVariable("name") String name) {
		DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<ResponseEntity<?>>();

		if (proxies.containsKey(name)) {
			String url = (String) request
					.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
			String restOfTheUrl = url.replace("/proxy/" + name + "/", "");
			String target = proxies.get(name) + "/" + restOfTheUrl;
			endpoints.put(target, false);

			httpAsyncClient.start();
			HttpGet newRequest = new HttpGet(target);
			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				Enumeration<String> headerValues = request
						.getHeaders(headerName);
				while (headerValues.hasMoreElements()) {
					newRequest
							.addHeader(headerName, headerValues.nextElement());
				}
			}
			LOGGER.debug("{ 'event': 'proxyReqeust', 'from': '" + url
					+ "', 'to': '" + target + "' }");
			httpAsyncClient.execute(newRequest, new CBack(httpAsyncClient,
					deferredResult, target, endpoints));

		} else {
			LOGGER.error("{ 'error': 'proxy not found', 'proxyName' : '" + name
					+ "' }");
			deferredResult.setResult(ResponseEntity.notFound().build());
		}
		return deferredResult;
	}

	public boolean endPointExists(String endpoint) {
		return endpoints.containsKey(endpoint);
	}

	public boolean endPointAvailable(String endpoint) {
		return endpoints.get(endpoint);
	}
}
