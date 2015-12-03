package au.com.windyroad.servicegateway.controller;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.windyroad.servicegateway.Repository;
import au.com.windyroad.servicegateway.model.ProxiesEntity;
import au.com.windyroad.servicegateway.model.ProxyEntity;

@Controller
@RequestMapping(value = "/admin/proxies/{proxyName}")
public class AdminProxyController {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProxiesEntity proxies;

    @Autowired
    Repository repository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> self(@PathVariable("proxyName") String proxyName)
            throws URISyntaxException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        ProxyEntity proxy = repository.getProxy(proxyName);

        if (proxy == null) {
            return ResponseEntity.notFound().build();
        }

        ResponseEntity<?> responseEntity = new ResponseEntity<>(proxy,
                HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.PUT, produces = {
            "application/vnd.siren+json",
            "application/json" }, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<?> post(@PathVariable("proxyName") String proxyName,
            @RequestParam MultiValueMap<String, String> queryParams,
            @RequestBody MultiValueMap<String, String> bodyParams)
                    throws IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException {

        ProxyEntity proxy = repository.getProxy(proxyName);
        if (proxy == null) {
            return ResponseEntity.notFound().build();
        }
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.putAll(queryParams);
        params.putAll(bodyParams);
        String actionName = queryParams.getFirst("trigger");
        if (actionName == null) {
            actionName = bodyParams.getFirst("trigger");
        }
        if (actionName == null) {
            // todo add body with classes indicating what is missing
            return ResponseEntity.badRequest().build();
        }
        au.com.windyroad.hateoas.core.Action action = proxy
                .getAction(actionName);
        action.invoke(params.toSingleValueMap());

        return ResponseEntity.noContent().location(proxy.getAddress()).build();
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = {
            "application/vnd.siren+json",
            "application/json" }, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<?> delete(@PathVariable("proxyName") String proxyName)
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {

        ProxyEntity proxy = repository.getProxy(proxyName);
        proxy.getProperties().deleteProxy();
        return ResponseEntity.noContent().location(proxies.getAddress())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> onException(Exception e) {
        LOGGER.error("Error processing proxy admin request", e);
        // TODO: respond with correct media type
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @RequestMapping(method = RequestMethod.GET, produces = { "text/html",
            "application/xhtml+xml" })
    public String proxiesView() {
        return "/index.html";
    }

}
