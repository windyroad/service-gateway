package au.com.windyroad.servicegateway.driver;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.AsyncRestTemplate;

import au.com.windyroad.hateoas.client.Resolver;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.model.AdminRootController;

@Component
@Profile(value = "integration")
public class RestDriver extends JavaDriver {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("restTemplateResolver")
    private Resolver resolver;

    @Autowired
    ServiceGatewayTestConfiguration config;

    @Autowired
    AsyncRestTemplate asyncRestTemplate;

    @Override
    CompletableFuture<AdminRootController> getRoot() throws URISyntaxException {
        return resolver.get("/admin/proxies", AdminRootController.class);
    }

}
