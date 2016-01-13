package au.com.windyroad.servicegateway.driver;

import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import au.com.windyroad.hateoas.client.Resolver;
import au.com.windyroad.servicegateway.model.AdminRootController;

@Component
@Profile(value = "ui-integration")
public class HtmlDriver extends RestDriver {

    @Autowired
    @Qualifier("webDriverResolver")
    private Resolver resolver;

    @Value("${security.user.name:user}")
    String name;

    @Value("${security.user.password:password}")
    String password;

    @Override
    CompletableFuture<AdminRootController> getRoot() throws URISyntaxException {
        return resolver.get("/admin/proxies", AdminRootController.class);
    }

}
