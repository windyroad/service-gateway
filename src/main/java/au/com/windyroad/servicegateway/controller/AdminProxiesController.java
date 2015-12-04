package au.com.windyroad.servicegateway.controller;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.MediaTypes;
import au.com.windyroad.servicegateway.model.ProxiesController;

@Controller
@RequestMapping(value = "/admin/proxies")
public class AdminProxiesController {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProxiesController proxiesController;

    @RequestMapping(method = RequestMethod.GET, produces = {
            MediaTypes.SIREN_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<?> self() {
        return ResponseEntity.ok(proxiesController.self());
    }

    @RequestMapping(method = RequestMethod.GET, produces = { "text/html",
            "application/xhtml+xml" })
    public String proxiesView() {
        return "/index.html";
    }

    @RequestMapping(method = RequestMethod.POST, produces = {
            "application/vnd.siren+json",
            "application/json" }, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<?> post(
            @RequestParam Map<String, String> allRequestParams)
                    throws URISyntaxException, NoSuchMethodException,
                    SecurityException, ScriptException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        au.com.windyroad.hateoas.core.Action action = proxiesController.self()
                .getAction(allRequestParams.get("trigger"));
        Entity result = action.invoke(allRequestParams);
        return ResponseEntity.created(result.getAddress()).build();
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> onException(Exception e) {
        LOGGER.error(e.getLocalizedMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
