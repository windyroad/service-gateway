package au.com.windyroad.servicegateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import au.com.windyroad.servicegateway.Repository;

//@Controller
//@RequestMapping(value = "/admin/proxies/{proxyName}/{target}")
public class AdminEndpointController {

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    // @RequestMapping(method = RequestMethod.GET)
    // @ResponseBody
    // public ResponseEntity<?> self(@PathVariable("proxyName") String
    // proxyName,
    // @PathVariable("target") String target)
    // throws URISyntaxException, NoSuchMethodException,
    // SecurityException, IllegalAccessException,
    // IllegalArgumentException, InvocationTargetException {
    //
    // ProxyEntity proxy = repository.getProxy(proxyName);
    // if (proxy == null) {
    // return ResponseEntity.notFound().build();
    // }
    //
    // EndpointEntity endpoint = repository
    // .getEndpoint(proxy.getProperties().getTarget() + "/" + target);
    //
    // ResponseEntity<?> responseEntity = new ResponseEntity<>(endpoint,
    // HttpStatus.OK);
    // return responseEntity;
    // }

    @RequestMapping(method = RequestMethod.GET, produces = { "text/html",
            "application/xhtml+xml" })
    public String proxiesView() {
        return "/index.html";
    }

}
