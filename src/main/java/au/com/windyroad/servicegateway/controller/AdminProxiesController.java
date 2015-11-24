package au.com.windyroad.servicegateway.controller;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

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

import au.com.windyroad.hateoas.Action;
import au.com.windyroad.hateoas.MediaTypes;
import au.com.windyroad.hateoas2.Entity;
import au.com.windyroad.servicegateway.model.Proxies;

@Controller
@RequestMapping(value = "/admin/proxies")
public class AdminProxiesController {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Proxies proxies;

    private Set<Action> actions = null;

    @RequestMapping(method = RequestMethod.GET, produces = {
            MediaTypes.SIREN_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<?> self() throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        ResponseEntity<?> responseEntity = new ResponseEntity<>(proxies,
                HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET, produces = { "text/html",
            "application/xhtml+xml" })
    public String proxiesView() {
        return "/index.html";
    }

    // private Collection<Action> getActions() throws IllegalAccessException,
    // IllegalArgumentException, InvocationTargetException,
    // NoSuchMethodException, SecurityException {
    // if (actions == null) {
    // actions = new UnifiedSetWithHashingStrategy<>(
    // HashingStrategies.fromFunction(Action::getName));
    // for (Method method : this.getClass().getMethods()) {
    // if (method.getReturnType().equals(ResponseEntity.class)) {
    // Parameter[] parameters = method.getParameters();
    // if (Arrays.stream(parameters)
    // .filter(p -> p
    // .getAnnotation(RequestParam.class) != null)
    // .findFirst().isPresent()) {
    // actions.add(new Action(method));
    // }
    // }
    // }
    // }
    // return actions;
    // }

    @RequestMapping(method = RequestMethod.POST, produces = {
            "application/vnd.siren+json",
            "application/json" }, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<?> post(
            @RequestParam Map<String, String> allRequestParams)
                    // @RequestParam("proxyName") @Title("Proxy Name") String
                    // proxyName,
                    // @RequestParam("endpoint")
                    // @PresentationType(PresentationType.URL)
                    // @Title("Endpoint URL") String endpoint)
                    throws URISyntaxException, NoSuchMethodException,
                    SecurityException, ScriptException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        au.com.windyroad.hateoas2.Action action = proxies
                .getAction(allRequestParams.get("trigger"));
        Entity result = action.invoke(proxies, allRequestParams);
        return ResponseEntity.ok(result);
        // // EmbeddedEntityLink added = proxies.createProxy(proxyName,
        // endpoint);
        // if (added != null) {
        // return ResponseEntity.created(added.getHref()).build();
        // } else {
        // throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED,
        // "TODO: Handle duplicate proxy create attempt. 409 or similar");
        // }
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> onException(Exception e) {
        LOGGER.error(e.getLocalizedMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
