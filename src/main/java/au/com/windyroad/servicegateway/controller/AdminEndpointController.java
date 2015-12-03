package au.com.windyroad.servicegateway.controller;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.windyroad.hateoas.core.ResolvedEntity;
import au.com.windyroad.servicegateway.Repository;
import au.com.windyroad.servicegateway.model.EndpointEntity;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;
import au.com.windyroad.servicegateway.model.ProxyEntity;

@Controller
@RequestMapping(value = "/admin/proxies/{proxyName}/{target}")
public class AdminEndpointController {

    @Autowired
    ResolvedEntity<Proxies> proxies;

    @Autowired
    Repository repository;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> self(@PathVariable("proxyName") String proxyName,
            @PathVariable("target") String target)
                    throws URISyntaxException, NoSuchMethodException,
                    SecurityException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        ParameterizedTypeReference<ResolvedEntity<Proxy>> type = new ParameterizedTypeReference<ResolvedEntity<Proxy>>() {
        };

        ProxyEntity proxy = repository.getProxy(proxyName);
        if (proxy == null) {
            return ResponseEntity.notFound().build();
        }
        EndpointEntity endpoint = proxy.getProperties().getEndpoint(target)
                .resolve(EndpointEntity.class);

        ResponseEntity<?> responseEntity = new ResponseEntity<>(endpoint,
                HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET, produces = { "text/html",
            "application/xhtml+xml" })
    public String proxiesView() {
        return "/index.html";
    }

}
