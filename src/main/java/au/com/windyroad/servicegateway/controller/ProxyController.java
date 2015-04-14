package au.com.windyroad.servicegateway.controller;

import java.io.IOException;
import java.util.Enumeration;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.HandlerMapping;

import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;

@Controller
public class ProxyController {
	public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	Proxies proxies;

	private static class CBack implements FutureCallback<HttpResponse> {
		private CloseableHttpAsyncClient httpAsyncClient;
		private DeferredResult<ResponseEntity<?>> deferredResult;
		private String target;

		private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
		private Proxy proxy;

		public CBack(CloseableHttpAsyncClient httpAsyncClient,
				DeferredResult<ResponseEntity<?>> deferredResult, Proxy proxy,
				String target) {
			this.httpAsyncClient = httpAsyncClient;
			this.deferredResult = deferredResult;
			this.target = target;
			this.proxy = proxy;
		}

		@Override
		public void failed(Exception ex) {
			LOGGER.error("Failure while processing: ", ex);
			proxy.addEndpoint(target, false);
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
				proxy.addEndpoint(target, true);

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

	@Autowired
	CloseableHttpAsyncClient httpAsyncClient;

	@RequestMapping("/proxy/{name}/**")
	public DeferredResult<ResponseEntity<?>> get(
			final HttpServletRequest request,
			final HttpServletResponse response,
			@PathVariable("name") String name) {
		DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<ResponseEntity<?>>();

		Proxy proxy = proxies.getProxy(name);
		if (proxy != null) {
			String url = (String) request
					.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
			String restOfTheUrl = url.replace("/proxy/" + name + "/", "");
			String target = proxy.getTarget() + "/" + restOfTheUrl;
			proxy.addEndpoint(target, false);

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
					deferredResult, proxy, target));

		} else {
			LOGGER.error("{ 'error': 'proxy not found', 'proxyName' : '" + name
					+ "' }");
			deferredResult.setResult(ResponseEntity.notFound().build());
		}
		return deferredResult;
	}

}
