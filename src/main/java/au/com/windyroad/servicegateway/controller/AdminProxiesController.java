package au.com.windyroad.servicegateway.controller;

import java.net.URI;
import java.net.URISyntaxException;

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
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;

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
			NoSuchMethodException, SecurityException {
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
			@RequestParam("proxyName") String proxyName,
			@RequestParam("endpoint") String endpoint)
			throws URISyntaxException, NoSuchMethodException, SecurityException {
		Proxy proxy = proxies.createProxy(proxyName, endpoint);
		URI location = ControllerLinkBuilder.linkTo(
				AdminProxyController.class,
				AdminProxyController.class.getMethod("proxy",
						new Class<?>[] { String.class }), proxyName).toUri();
		return ResponseEntity.created(location).build();
	}
}
