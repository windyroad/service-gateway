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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.HandlerMapping;

import au.com.windyroad.servicegateway.Repository;
import au.com.windyroad.servicegateway.model.ProxyEntity;

@Controller
public class ReverseProxyController {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    au.com.windyroad.servicegateway.model.ProxyController proxyController;

    private static class CBack implements FutureCallback<HttpResponse> {
        private DeferredResult<ResponseEntity<?>> deferredResult;
        private String target;

        private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

        private au.com.windyroad.servicegateway.model.ProxyController proxyController;

        private String proxyName;

        public CBack(DeferredResult<ResponseEntity<?>> deferredResult,
                au.com.windyroad.servicegateway.model.ProxyController proxyController,
                String proxyName, String target) {
            this.deferredResult = deferredResult;
            this.target = target;
            this.proxyName = proxyName;
            this.proxyController = proxyController;
        }

        @Override
        public void failed(Exception ex) {
            LOGGER.error("Failure while processing: ", ex);
            proxyController.setEndpoint(proxyName, target, "false");
            deferredResult.setErrorResult(ex);
        }

        @Override
        public void completed(HttpResponse result) {
            try {
                HttpHeaders httpHeaders = getHeaders(result);
                httpHeaders.set("Server", "ServiceGateway");
                HttpStatus httpStatus = HttpStatus
                        .valueOf(result.getStatusLine().getStatusCode());
                HttpEntity entity = result.getEntity();
                ResponseEntity<InputStreamResource> responseEntity;
                if (entity != null) {
                    InputStreamResource inputStreamResource = new InputStreamResource(
                            entity.getContent());
                    // httpHeaders.setContentLength(Long.parseLong(result
                    // .getFirstHeader("Content-Length").getValue()));
                    responseEntity = new ResponseEntity<InputStreamResource>(
                            inputStreamResource, httpHeaders, httpStatus);
                } else {
                    responseEntity = new ResponseEntity<InputStreamResource>(
                            httpHeaders, httpStatus);
                }
                deferredResult.setResult(responseEntity);
                proxyController.setEndpoint(proxyName, target, "true");

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

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    @Autowired
    ApplicationContext context;

    @RequestMapping("/proxy/{proxyName}/**")
    public DeferredResult<ResponseEntity<?>> get(
            final HttpServletRequest request,
            final HttpServletResponse response,
            @PathVariable("proxyName") String proxyName)
                    throws NoSuchMethodException, SecurityException,
                    IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException, URISyntaxException {
        DeferredResult<ResponseEntity<?>> deferredResult = new DeferredResult<ResponseEntity<?>>();

        ProxyEntity proxy = proxyController.self(proxyName);

        if (proxy != null) {
            String url = (String) request.getAttribute(
                    HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
            String restOfTheUrl = url.replace("/proxy/" + proxyName + "/", "");
            String target = proxy.getProperties().getTarget() + "/"
                    + restOfTheUrl;
            proxyController.setEndpoint(proxyName, target, "false");

            httpAsyncClient.start();
            HttpGet newRequest = new HttpGet(target);
            copyHeaders(request, newRequest);

            addOrAppendToHeader(newRequest, "X-Forwarded-For",
                    request.getRemoteAddr());

            newRequest.addHeader("X-Forwarded-Proto", request.getScheme());

            LOGGER.debug("{ 'event': 'proxyReqeust', 'from': '" + url
                    + "', 'to': '" + target + "' }");
            httpAsyncClient.execute(newRequest, new CBack(deferredResult,
                    proxyController, proxyName, target));

        } else {
            LOGGER.error("{ 'error': 'proxy not found', 'proxyName' : '"
                    + proxyName + "' }");
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
