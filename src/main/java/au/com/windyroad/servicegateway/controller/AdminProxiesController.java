package au.com.windyroad.servicegateway.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

import au.com.windyroad.hateoas.Action;
import au.com.windyroad.hateoas.Rel;
import au.com.windyroad.hateoas.Validation;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;

@Controller
@RequestMapping(value = "/admin/proxy")
public class AdminProxiesController {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Proxies proxies;

    private Set<Action> actions = null;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Rel("self")
    public ResponseEntity<?> proxies()
            throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException // throws
                                                                                // URISyntaxException,
    // NoSuchMethodException, SecurityException, IllegalAccessException,
    // IllegalArgumentException, InvocationTargetException
    {
        final Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        proxies.setActions(getActions());
        ResponseEntity<Proxies> responseEntity = new ResponseEntity<Proxies>(
                proxies, HttpStatus.OK);
        return responseEntity;
    }

    private Collection<Action> getActions() throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {
        if (actions == null) {
            actions = new UnifiedSetWithHashingStrategy<>(
                    HashingStrategies.fromFunction(Action::getName));
            for (Method method : this.getClass().getMethods()) {
                if (method.getReturnType().equals(ResponseEntity.class)) {
                    Action action = new Action(method);
                    actions.add(action);
                }
            }
        }
        return actions;
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Rel("createProxy")
    public ResponseEntity<?> createProxy(
            @RequestParam("proxyName") @Validation("getCreateProxyProxyNameValidator") String proxyName,
            @RequestParam("endpoint") @Validation("getCreateProxyEndPointValidator") String endpoint)
                    throws URISyntaxException, NoSuchMethodException,
                    SecurityException, ScriptException {
        if (!isValid(proxyName, "proxyName",
                getCreateProxyProxyNameValidator())) {
            throw new NotImplementedException("Do validation error stuff here");
        }
        Proxy proxy = proxies.createProxy(proxyName, endpoint);
        URI location = ControllerLinkBuilder
                .linkTo(AdminProxyController.class,
                        AdminProxyController.class.getMethod("proxy",
                                new Class<?>[] { String.class }),
                        proxyName)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    private boolean isValid(Object value, String paramName, String validation)
            throws ScriptException {
        ScriptEngineManager factory = new ScriptEngineManager();
        ScriptEngine engine = factory.getEngineByName("JavaScript");
        Boolean result = (Boolean) engine.eval("var " + paramName + " = \""
                + value.toString() + "\";" + validation);
        return result;
    }

    public static String getCreateProxyProxyNameValidator() {
        return "valid = /^[^/\\.]*$/.test(proxyName);";
    }

    public static String getCreateProxyEndPointValidator() {
        return "valid = true;";
    }

}
