package au.com.windyroad.servicegateway.controller;

import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import au.com.windyroad.hateoas.Control;
import au.com.windyroad.hateoas.Rel;
import au.com.windyroad.hateoas.Resource;
import au.com.windyroad.servicegateway.model.Proxies;
import au.com.windyroad.servicegateway.model.Proxy;

@Controller
@RequestMapping(value = "/admin/proxy")
public class AdminProxyController {
	public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	Proxies proxies;

	@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	@ResponseBody
	@Rel("self")
	public ResponseEntity<?> proxy(@PathVariable("name") String name)
			throws URISyntaxException, NoSuchMethodException, SecurityException {
		Resource<Proxy> proxyResource = new Resource<Proxy>(
				proxies.getProxy(name));
		if (proxyResource.getContent() == null) {
			return ResponseEntity.notFound().build();
		}
		proxyResource
				.addControl(new Control(this.getClass().getMethod("proxy")));
		ResponseEntity<Resource<Proxy>> responseEntity = new ResponseEntity<Resource<Proxy>>(
				proxyResource, HttpStatus.OK);
		return responseEntity;
	}

}
