package au.com.windyroad.servicegateway.controller;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.core.DummyInvocationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.windyroad.hateoas.Entity;
import au.com.windyroad.hateoas.Link;
import au.com.windyroad.hateoas.Rel;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;

@Controller
@RequestMapping(value = "/admin/proxies")
public class AdminProxyController {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Proxies proxies;

    @RequestMapping(value = "/{proxyName}", method = RequestMethod.GET)
    @ResponseBody
    @Rel("self")
    public ResponseEntity<?> proxy(@PathVariable("proxyName") String proxyName)
            throws URISyntaxException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Proxy proxy = proxies.getProxy(proxyName);
        if (proxy == null) {
            return ResponseEntity.notFound().build();
        }
        Entity<Proxy> entity = new Entity<>();
        entity.addLink(Link.linkTo(DummyInvocationUtils
                .methodOn(AdminProxyController.class).proxy(proxyName)));
        ResponseEntity<Proxy> responseEntity = new ResponseEntity<Proxy>(proxy,
                HttpStatus.OK);
        return responseEntity;
    }

}
