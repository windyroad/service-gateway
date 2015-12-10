package au.com.windyroad.servicegateway.controller;

import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.HandlerMapping;

import au.com.windyroad.hateoas.core.Action;
import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.EntityWrapper;
import au.com.windyroad.hateoas.core.MediaTypes;
import au.com.windyroad.servicegateway.Repository;

@Controller
@RequestMapping(value = "/admin/**")
public class RepositoryController {
    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("serverRepository")
    Repository repository;

    @Autowired
    ApplicationContext context;

    @RequestMapping(method = RequestMethod.GET, produces = {
            MediaTypes.SIREN_JSON_VALUE, MediaType.APPLICATION_JSON_VALUE })
    @ResponseBody
    public ResponseEntity<?> self(
            @RequestParam Map<String, Object> allRequestParams,
            final HttpServletRequest request) {
        String url = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        if (!allRequestParams.isEmpty()) {
            url += "?" + request.getQueryString();
        }
        EntityWrapper<?> entity = repository.get(url);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }

        // how do we render the entity according to the request params,
        // which could be used to specific paging, filtering, sorting
        // and partial rendering criteria?
        // don't know. for the time being ignore the params
        return ResponseEntity.ok(entity);
    }

    @RequestMapping(method = RequestMethod.GET, produces = { "text/html",
            "application/xhtml+xml" })
    public String htmlView() {
        return "/index.html";
    }

    @RequestMapping(method = RequestMethod.POST, produces = {
            "application/vnd.siren+json",
            "application/json" }, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<?> post(
            @RequestParam MultiValueMap<String, String> allRequestParams,
            final HttpServletRequest request)
                    throws URISyntaxException, NoSuchMethodException,
                    SecurityException, ScriptException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        String url = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        EntityWrapper<?> entity = repository.get(url);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }

        String actionName = allRequestParams.getFirst("action");
        if (actionName == null) {
            // todo add body with classes indicating what is missing
            return ResponseEntity.badRequest().build();
        }
        Action action = entity.getAction(actionName);
        if (action == null) {
            // todo add body with classes indicating what is missing
            return ResponseEntity.badRequest().build();
        }
        // todo: post actions should have a link return value
        // todo: automatically treat actions that return links as POST actions
        Entity result = action.invoke(allRequestParams.toSingleValueMap());
        return ResponseEntity.created(result.getAddress()).build();
    }

    @RequestMapping(method = RequestMethod.DELETE, produces = {
            "application/vnd.siren+json",
            "application/json" }, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<?> delete(final HttpServletRequest request)
            throws URISyntaxException, NoSuchMethodException, SecurityException,
            ScriptException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        String url = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        EntityWrapper<?> entity = repository.get(url);
        if (entity == null) {
            return ResponseEntity.noContent().build();
        }
        Optional<Action> action = entity.getActions().stream()
                .filter(e -> e.getNature().equals(HttpMethod.DELETE)).findAny();

        if (!action.isPresent()) {
            repository.remove(entity);
        } else {
            Entity result = action.get().invoke(new HashMap<>());
        }
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(method = RequestMethod.PUT, produces = {
            "application/vnd.siren+json",
            "application/json" }, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    @ResponseBody
    public ResponseEntity<?> post(
            @RequestParam MultiValueMap<String, String> queryParams,
            @RequestBody MultiValueMap<String, String> bodyParams,
            final HttpServletRequest request)
                    throws IllegalAccessException, IllegalArgumentException,
                    InvocationTargetException, URISyntaxException {

        String url = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        EntityWrapper<?> entity = repository.get(url);
        if (entity == null) {
            return ResponseEntity.notFound().build();
        }
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.putAll(queryParams);
        params.putAll(bodyParams);
        String actionName = queryParams.getFirst("action");
        if (actionName == null) {
            actionName = bodyParams.getFirst("action");
        }
        if (actionName == null) {
            // todo add body with classes indicating what is missing
            return ResponseEntity.badRequest().build();
        }
        au.com.windyroad.hateoas.core.Action action = entity
                .getAction(actionName);
        if (action == null) {
            // todo add body with classes indicating what is missing
            return ResponseEntity.badRequest().build();
        }

        action.invoke(params.toSingleValueMap());
        // todo: automatically treat actions that return void as PUT actions
        return ResponseEntity.noContent().location(entity.getAddress()).build();
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> onException(Exception e) {
        LOGGER.error(e.getLocalizedMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
