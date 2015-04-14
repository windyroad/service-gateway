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
import au.com.windyroad.servicegateway.resource.ProxyResource;

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
		ProxyResource proxyResource = new ProxyResource(proxies);
		proxyResource.addControl(new Control(this.getClass().getMethod(
				"proxies")));
		proxyResource.addControl(new Control(this.getClass().getMethod(
				"createProxy", new Class<?>[] { String.class, String.class })));

		ResponseEntity<ProxyResource> responseEntity = new ResponseEntity<ProxyResource>(
				proxyResource, HttpStatus.OK);
		return responseEntity;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	@Rel("createProxy")
	public ResponseEntity<?> createProxy(
			@RequestParam("targetEndPoint") String targetEndPoint,
			@RequestParam("proxyPath") String proxyPath)
			throws URISyntaxException, NoSuchMethodException, SecurityException {
		Proxy proxy = proxies.createProxy(proxyPath, targetEndPoint);
		URI location = ControllerLinkBuilder.linkTo(
				AdminProxyController.class,
				AdminProxyController.class.getMethod("proxy",
						new Class<?>[] { String.class }), proxyPath).toUri();
		return ResponseEntity.created(location).build();
	}
}
