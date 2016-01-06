package au.com.windyroad.servicegateway.driver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.AsyncRestTemplate;

import au.com.windyroad.hateoas.core.FutureConverter;
import au.com.windyroad.servicegateway.ServiceGatewayTestConfiguration;
import au.com.windyroad.servicegateway.model.AdminRootController;

@Component
@Profile(value = "integration")
public class RestDriver extends JavaDriver {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ServiceGatewayTestConfiguration config;

    @Autowired
    AsyncRestTemplate asyncRestTemplate;

    @Override
    CompletableFuture<AdminRootController> getRoot() {
        URI rootUrl;
        try {
            rootUrl = new URI(
                    "https://localhost:" + config.getPort() + "/admin/proxies");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return FutureConverter.convert(asyncRestTemplate.exchange(rootUrl,
                HttpMethod.GET, null, AdminRootController.class))
                .thenApply(r -> {
                    return r.getBody();
                });
    }

}
