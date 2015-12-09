package au.com.windyroad.servicegateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import au.com.windyroad.servicegateway.model.AdminRootController;
import au.com.windyroad.servicegateway.model.ProxyController;

//@Controller
//@RequestMapping(value = "/admin/proxies/{proxyName}")
public class AdminProxyController {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProxyController proxyController;

    @Autowired
    AdminRootController proxiesController;

    // @RequestMapping(method = RequestMethod.GET)
    // @ResponseBody
    // public ResponseEntity<?> self(@PathVariable("proxyName") String
    // proxyName) {
    // ProxyEntity proxy = proxyController.self(proxyName);
    //
    // if (proxy == null) {
    // return ResponseEntity.notFound().build();
    // }
    //
    // ResponseEntity<?> responseEntity = ResponseEntity.ok(proxy);
    // return responseEntity;
    // }

    // @RequestMapping(method = RequestMethod.PUT, produces = {
    // "application/vnd.siren+json",
    // "application/json" }, consumes =
    // MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    // @ResponseBody
    // public ResponseEntity<?> post(@PathVariable("proxyName") String
    // proxyName,
    // @RequestParam MultiValueMap<String, String> queryParams,
    // @RequestBody MultiValueMap<String, String> bodyParams)
    // throws IllegalAccessException, IllegalArgumentException,
    // InvocationTargetException {
    //
    // ProxyEntity proxy = proxyController.self(proxyName);
    // if (proxy == null) {
    // return ResponseEntity.notFound().build();
    // }
    // MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    // params.putAll(queryParams);
    // params.putAll(bodyParams);
    // String actionName = queryParams.getFirst("trigger");
    // if (actionName == null) {
    // actionName = bodyParams.getFirst("trigger");
    // }
    // if (actionName == null) {
    // // todo add body with classes indicating what is missing
    // return ResponseEntity.badRequest().build();
    // }
    // au.com.windyroad.hateoas.core.Action action = proxy
    // .getAction(actionName);
    // action.invoke(params.toSingleValueMap());
    //
    // return ResponseEntity.noContent().location(proxy.getAddress()).build();
    // }

    // @RequestMapping(method = RequestMethod.DELETE, produces = {
    // "application/vnd.siren+json",
    // "application/json" }, consumes =
    // MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    // @ResponseBody
    // public ResponseEntity<?> delete(@PathVariable("proxyName") String
    // proxyName)
    // throws IllegalAccessException, IllegalArgumentException,
    // InvocationTargetException {
    //
    // proxyController.deleteProxy(proxyName);
    // // should we do it this way, or should the proxies controller return a
    // // link to the root?
    // return ResponseEntity.noContent()
    // .location(proxiesController.self().getAddress()).build();
    // }

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
