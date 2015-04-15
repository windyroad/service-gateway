package au.com.windyroad.servicegateway.controller;

import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.windyroad.hateoas.Control;
import au.com.windyroad.hateoas.Rel;
import au.com.windyroad.hateoas.Validation;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;
import cucumber.api.PendingException;

@Controller
@RequestMapping(value = "/admin/proxy")
public class AdminProxiesController {
	public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	Proxies proxies;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@Rel("self")
	public ResponseEntity<?> proxies() throws URISyntaxException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		proxies.addControl(new Control(this.getClass().getMethod("proxies")));
		proxies.addControl(new Control(this.getClass().getMethod("createProxy",
				new Class<?>[] { String.class, String.class })));

		ResponseEntity<Proxies> responseEntity = new ResponseEntity<Proxies>(
				proxies, HttpStatus.OK);
		return responseEntity;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Rel("createProxy")
	public ResponseEntity<?> createProxy(
			@RequestParam("proxyName") @Validation("getCreateProxyProxyNameValidator") String proxyName,
			@RequestParam("endpoint") @Validation("getCreateProxyEndPointValidator") String endpoint)
			throws URISyntaxException, NoSuchMethodException,
			SecurityException, ScriptException {
		if (!isValid(proxyName, "proxyName", getCreateProxyProxyNameValidator())) {
			throw new PendingException("Do validation error stuff here");
		}
		Proxy proxy = proxies.createProxy(proxyName, endpoint);
		URI location = ControllerLinkBuilder.linkTo(
				AdminProxyController.class,
				AdminProxyController.class.getMethod("proxy",
						new Class<?>[] { String.class }), proxyName).toUri();
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
