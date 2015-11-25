package au.com.windyroad.servicegateway.controller;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
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
import org.springframework.web.client.HttpServerErrorException;
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
        private DeferredResult<ResponseEntity<?>> deferredResult;
        private String target;

        private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
        private Proxy proxy;

        public CBack(DeferredResult<ResponseEntity<?>> deferredResult,
                Proxy proxy, String target) {
            this.deferredResult = deferredResult;
            this.target = target;
            this.proxy = proxy;
        }

        @Override
        public void failed(Exception ex) {
            LOGGER.error("Failure while processing: ", ex);
            try {
                proxy.setEndpoint(target, false);
            } catch (NoSuchMethodException | SecurityException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException | URISyntaxException e) {
                throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED,
                        "TODO");
            }
            deferredResult.setErrorResult(ex);
        }

        @Override
        public void completed(HttpResponse result) {
            try {
                HttpHeaders httpHeaders = getHeaders(result);
                HttpStatus httpStatus = HttpStatus
                        .valueOf(result.getStatusLine().getStatusCode());
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
                responseEntity.getHeaders().set("Server", "ServiceGateway");
                deferredResult.setResult(responseEntity);
                proxy.setEndpoint(target, true);

            } catch (Exception e) {
                LOGGER.error("Failure while processing response:", e);
                deferredResult.setErrorResult(e);
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

        @Override
        public void cancelled() {
            // do nothing
        }
    }

    @Autowired
    CloseableHttpAsyncClient httpAsyncClient;

    @RequestMapping("/proxy/{name}/**")
    public DeferredResult<ResponseEntity<?>> get(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @PathVariable("name") String name)
                    throws NoSuchMethodException, SecurityException,
                    IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException, URISyntaxException {
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<ResponseEntity<?>>();

        Proxy proxy = proxies.getProxy(name);
        if (proxy != null) {
            String url = (String) request.getAttribute(
                    HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
            String restOfTheUrl = url.replace("/proxy/" + name + "/", "");
            String target = proxy.getTarget() + "/" + restOfTheUrl;
            proxy.setEndpoint(restOfTheUrl, false);

            httpAsyncClient.start();
            HttpGet newRequest = new HttpGet(target);
            copyHeaders(request, newRequest);

            addOrAppendToHeader(newRequest, "X-Forwarded-For",
                    request.getRemoteAddr());

            newRequest.addHeader("X-Forwarded-Proto", request.getScheme());

            LOGGER.debug("{ 'event': 'proxyReqeust', 'from': '" + url
                    + "', 'to': '" + target + "' }");
            httpAsyncClient.execute(newRequest,
                    new CBack(deferredResult, proxy, restOfTheUrl));

        } else {
            LOGGER.error("{ 'error': 'proxy not found', 'proxyName' : '" + name
                    + "' }");
            deferredResult.setResult(ResponseEntity.notFound().build());
        }
        return deferredResult;
    }

    void addOrAppendToHeader(HttpGet newRequest, String headerName,
            String headerValue) {
        Header originalHeader = newRequest.getFirstHeader(headerName);
        if (originalHeader == null) {
            newRequest.addHeader(headerName, headerValue);
        } else {
            newRequest.setHeader(headerName,
                    originalHeader.getValue() + ", " + headerValue);
        }
    }

    void copyHeaders(final HttpServletRequest request, HttpGet newRequest) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if ("host".equals(headerName.toLowerCase())) {
                Enumeration<String> headerValues = request
                        .getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    newRequest.addHeader("X-Forwarded-Host",
                            headerValues.nextElement());
                }
            } else {
                Enumeration<String> headerValues = request
                        .getHeaders(headerName);
                while (headerValues.hasMoreElements()) {
                    newRequest.addHeader(headerName,
                            headerValues.nextElement());
                }
            }
        }
    }

}
