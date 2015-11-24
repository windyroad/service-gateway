package au.com.windyroad.servicegateway.controller;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.windyroad.hateoas.annotations.Rel;
import au.com.windyroad.hateoas.annotations.Title;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;

@Controller
@RequestMapping(value = "/admin/proxies/{proxyName}")
public class AdminProxyController {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Proxies proxies;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Rel("self")
    @Title("Proxy `{proxyName}`")
    public ResponseEntity<?> self(@PathVariable("proxyName") String proxyName)
            throws URISyntaxException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Proxy proxy = proxies.getProxy(proxyName);
        if (proxy == null) {
            return ResponseEntity.notFound().build();
        }
        // proxy.setTitle("Proxy `" + proxyName + "`");
        // TODO: move title to annotation

        ResponseEntity<?> responseEntity = new ResponseEntity<>(proxy,
                HttpStatus.OK);
        return responseEntity;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> onException(Exception e) {
        LOGGER.error("Error processing proxy admin request", e);
        // TODO: respond with correct media type
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @RequestMapping(value = "/{proxyName}", method = RequestMethod.GET, produces = {
            "text/html", "application/xhtml+xml" })
    public String proxiesView() {
        return "/index.html";
    }

}
