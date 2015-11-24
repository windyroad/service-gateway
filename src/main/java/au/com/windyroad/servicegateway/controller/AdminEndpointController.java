package au.com.windyroad.servicegateway.controller;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.windyroad.hateoas.annotations.Rel;
import au.com.windyroad.hateoas.annotations.Title;
import au.com.windyroad.servicegateway.model.Endpoint;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;

@Controller
@RequestMapping(value = "/admin/proxies/{proxyName}/{target}")
public class AdminEndpointController {

    @Autowired
    Proxies proxies;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Rel("self")
    @Title("Endpoint `{proxyName}/{target}`")
    public ResponseEntity<?> self(@PathVariable("proxyName") String proxyName,
            @PathVariable("target") String target)
                    throws URISyntaxException, NoSuchMethodException,
                    SecurityException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        // TODO: get the proxy using http.
        Proxy proxy = proxies.getProxy(proxyName);
        if (proxy == null) {
            return ResponseEntity.notFound().build();
        }
        Endpoint endpoint = proxy.getEndpoint(target);

        ResponseEntity<?> responseEntity = new ResponseEntity<>(endpoint,
                HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(value = "/{proxyName}/{target}", method = RequestMethod.GET, produces = {
            "text/html", "application/xhtml+xml" })
    public String proxiesView() {
        return "/index.html";
    }

}
