package au.com.windyroad.servicegateway.controller;

import static org.springframework.hateoas.core.DummyInvocationUtils.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpServerErrorException;

import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

import au.com.windyroad.hateoas.Action;
import au.com.windyroad.hateoas.Link;
import au.com.windyroad.hateoas.annotations.Name;
import au.com.windyroad.hateoas.annotations.PresentationType;
import au.com.windyroad.hateoas.annotations.Rel;
import au.com.windyroad.hateoas.annotations.Title;
import au.com.windyroad.servicegateway.model.Proxies;

@Controller
@RequestMapping(value = "/admin/proxies")
public class AdminProxiesController {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Proxies proxies;

    private Set<Action> actions = null;

    @RequestMapping(method = RequestMethod.GET, produces = {
            "application/vnd.siren+json", "application/json" })
    @ResponseBody
    @Rel("self")
    @Title("Proxies")
    public ResponseEntity<?> proxies() throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {

        proxies.setActions(getActions());
        proxies.addLink(
                Link.linkTo(methodOn(AdminProxiesController.class).proxies()));
        proxies.setTitle("Proxies");
        // TODO: move title to annotation
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.VARY, "Accept");
        headers.add("CustomHeader", "Accept");
        ResponseEntity<?> responseEntity = new ResponseEntity<>(proxies,
                headers, HttpStatus.OK);
        return responseEntity;
    }

    @RequestMapping(method = RequestMethod.GET, produces = { "text/html",
            "application/xhtml+xml" })
    public String proxiesView() {
        return "/index.html";
    }

    private Collection<Action> getActions() throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        if (actions == null) {
            actions = new UnifiedSetWithHashingStrategy<>(
                    HashingStrategies.fromFunction(Action::getName));
            for (Method method : this.getClass().getMethods()) {
                if (method.getReturnType().equals(ResponseEntity.class)) {
                    Parameter[] parameters = method.getParameters();
                    if (Arrays.stream(parameters)
                            .filter(p -> p
                                    .getAnnotation(RequestParam.class) != null)
                            .findFirst().isPresent()) {
                        actions.add(new Action(method));
                    }
                }
            }
        }
        return actions;
    }

    @RequestMapping(method = RequestMethod.POST, produces = {
            "application/vnd.siren+json", "application/json" })
    @ResponseBody
    @Name("createProxy")
    @Title("Create Proxy")
    public ResponseEntity<?> createProxy(
            @RequestParam("proxyName") @Title("Proxy Name") String proxyName,
            @RequestParam("endpoint") @PresentationType(PresentationType.URL) @Title("Endpoint URL") String endpoint)
                    throws URISyntaxException, NoSuchMethodException,
                    SecurityException, ScriptException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {

        boolean added = proxies.createProxy(proxyName, endpoint);
        if (added) {
            URI location = Link.linkTo(
                    methodOn(AdminProxyController.class).proxy(proxyName))
                    .getHref();
            return ResponseEntity.created(location).build();
        } else {
            throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED,
                    "TODO: Handle duplicate proxy create attempt. 409 or similar");
        }
    }

    @RequestMapping("/")
    String index(@PathVariable String page) {
        return "index";
    }
}
