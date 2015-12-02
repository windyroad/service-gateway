package au.com.windyroad.servicegateway;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@RequestMapping("/test/ping")
public class Ping {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> get(final HttpServletRequest request)
            throws InterruptedException {
        LOGGER.info("PING!");
        // Map<String, Object> response = new HashMap<>();
        // response.put("parameters", request.getParameterMap());
        // response.put("headers", getHeaders(request));
        // return ResponseEntity.ok(response);
        return ResponseEntity.noContent().build();
    }

    MultiValueMap<String, String> getHeaders(HttpServletRequest request) {
        MultiValueMap<String, String> rval = new LinkedMultiValueMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                rval.add(headerName, headerValues.nextElement());
            }
        }
        return rval;
    }

    Map<String, Object> getAttributes(HttpServletRequest request) {
        Map<String, Object> rval = new HashMap<>();
        Enumeration<String> attributeNames = request.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            rval.put(attributeName, request.getAttribute(attributeName));
        }
        return rval;
    }

}
