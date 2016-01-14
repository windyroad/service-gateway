package au.com.windyroad.servicegateway;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import au.com.windyroad.hateoas.client.Resolver;
import au.com.windyroad.hateoas.core.CreatedLinkedEntity;
import au.com.windyroad.hateoas.core.Entity;
import au.com.windyroad.hateoas.core.EntityRelationship;
import au.com.windyroad.hateoas.core.MediaTypes;
import au.com.windyroad.servicegateway.model.AdminRootController;
import au.com.windyroad.servicegateway.model.EndpointController;
import au.com.windyroad.servicegateway.model.ProxyController;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@ContextConfiguration(classes = { ServiceGatewayApplication.class,
        ServiceGatewayTestConfiguration.class }, loader = SpringApplicationContextLoader.class)
@SpringApplicationConfiguration(classes = { ServiceGatewayApplication.class,
        ServiceGatewayTestConfiguration.class })
@WebIntegrationTest({ "server.port=0", "management.port=0" })
@TestExecutionListeners({ ServiceGatewayTestExecutionListener.class })
public class StepDefs {

    public final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Resolver resolver;

    @Autowired
    CloseableHttpAsyncClient httpAsyncClient;

    @Autowired
    ServiceGatewayTestConfiguration config;

    @Autowired
    RestTemplate restTemplate;

    private static class CBack implements FutureCallback<HttpResponse> {
        private DeferredResult<HttpResponse> deferredResult;

        public CBack(DeferredResult<HttpResponse> deferredResult) {
            this.deferredResult = deferredResult;
        }

        @Override
        public void completed(HttpResponse result) {
            deferredResult.setResult(result);
        }

        @Override
        public void failed(Exception ex) {
            deferredResult.setErrorResult(ex);

        }

        @Override
        public void cancelled() {
            // do nothing
        }
    }

    private ProxyController currentProxy;

    public EndpointController currentEndpoint;

    @Given("^there are no proxied endpoints listed$")
    public void there_are_no_proxied_endpoints_listed() throws Throwable {
    }

    @Given("^a ping service at \"(.*?)\"$")
    public void a_ping_service_at(String path) throws Throwable {
        URI url = new URI("https://localhost:" + config.getPort() + path);
        DeferredResult<HttpResponse> deferredResult = new DeferredResult<HttpResponse>();
        HttpGet newRequest = new HttpGet(url);
        newRequest.addHeader("Accept", MediaTypes.SIREN_JSON_VALUE);
        CBack callback = new CBack(deferredResult);
        Future<HttpResponse> future = httpAsyncClient.execute(newRequest,
                callback);
        HttpResponse response = future.get();
        assertTrue(HttpStatus.valueOf(response.getStatusLine().getStatusCode())
                .is2xxSuccessful());
        LOGGER.info("PING SERVICE CHECKED");
    }

    @Given("^\"(.*?)\" is proxied at \"/proxy/(.*?)\"$")
    public void is_proxied_at(String endpoint, String proxy) throws Throwable {
        currentProxy = getRoot().thenApplyAsync(root -> {
            return root.createProxy(proxy, normaliseUrl(endpoint));
        }).thenApplyAsync(result -> {
            CreatedLinkedEntity cle = result.join();
            return cle.resolve(ProxyController.class);
        }).get();
    }

    String normaliseUrl(String endpoint) {
        if (endpoint.startsWith("/")) {
            endpoint = "https://localhost:" + config.getPort() + endpoint;
        }
        return endpoint;
    }

    @When("^a request is successfully made to \"(.*?)\"$")
    public void a_request_is_successfully_made_to(String path)
            throws Throwable {
        ResponseEntity<String> response = restTemplate.getForEntity(
                new URI("https://localhost:" + config.getPort() + path),
                String.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Then("^\"(.*?)\" will be listed in the endpoints proxied by \"/proxy/(.*?)\"$")
    public void will_be_listed_in_the_endpoints_proxied_by(String endpoint,
            String proxy) throws Throwable {
        currentProxy = currentProxy.reload(ProxyController.class);

        Optional<EntityRelationship> optionalEndpoint;
        Collection<EntityRelationship> entities = currentProxy.getEntities();
        optionalEndpoint = entities.stream().filter(e -> {
            Entity entity = e.getEntity();
            return entity.getTitle().contains(normaliseUrl(endpoint));
        }).findAny();

        assertTrue(optionalEndpoint.isPresent());
        currentEndpoint = optionalEndpoint.get().getEntity()
                .resolve(EndpointController.class);
    }

    @Then("^the endpoint will be shown as available$")
    public void the_endpoint_will_be_shown_as_available() throws Throwable {
        assertTrue(currentEndpoint.getProperties().isAvailable());
    }

    CompletableFuture<AdminRootController> getRoot() throws URISyntaxException {
        return resolver.get("/admin/proxies", AdminRootController.class);
    }
}
