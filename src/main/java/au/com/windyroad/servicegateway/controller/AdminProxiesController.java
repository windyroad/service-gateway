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

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gs.collections.impl.block.factory.HashingStrategies;
import com.gs.collections.impl.set.strategy.mutable.UnifiedSetWithHashingStrategy;

import au.com.windyroad.hateoas.Action;
import au.com.windyroad.hateoas.Entity;
import au.com.windyroad.hateoas.Link;
import au.com.windyroad.hateoas.Name;
import au.com.windyroad.hateoas.PresentationType;
import au.com.windyroad.hateoas.Rel;
import au.com.windyroad.servicegateway.model.Proxies;

@Controller
@RequestMapping(value = "/admin/proxies")
public class AdminProxiesController {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Proxies proxies;

    private Set<Action> actions = null;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @Rel("self")
    public ResponseEntity<?> proxies() throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException {

        Entity<?> entity = new Entity<>(proxies, getActions());
        entity.addLink(
                Link.linkTo(methodOn(AdminProxiesController.class).proxies()));
        ResponseEntity<?> responseEntity = new ResponseEntity<>(entity,
                HttpStatus.OK);
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

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @Name("createProxy")
    public ResponseEntity<?> createProxy(
            @RequestParam("proxyName") @PresentationType(PresentationType.TEXT) String proxyName,
            @RequestParam("endpoint") String endpoint)
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
            throw new NotImplementedException(
                    "TODO: Handle duplicate proxy create attempt. 409 or similar");
        }
    }

}
