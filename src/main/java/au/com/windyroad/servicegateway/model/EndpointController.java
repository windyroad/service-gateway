package au.com.windyroad.servicegateway.model;

import org.springframework.stereotype.Component;

import au.com.windyroad.hateoas.server.annotations.HateoasController;
import au.com.windyroad.servicegateway.controller.AdminEndpointController;

@Component
@HateoasController(AdminEndpointController.class)
public class EndpointController {

}
