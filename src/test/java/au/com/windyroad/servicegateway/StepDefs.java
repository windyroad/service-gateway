package au.com.windyroad.servicegateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;

import au.com.windyroad.servicegateway.driver.Driver;
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

    @Value("${local.server.port}")
    int port;

    @Autowired
    Driver driver;

    @Given("^there are no proxied endpoints listed$")
    public void there_are_no_proxied_endpoints_listed() throws Throwable {
        driver.clearProxies();
    }

    @Given("^a ping service at \"(.*?)\"$")
    public void a_ping_service_at(String path) throws Throwable {
        driver.checkPingService(path);
    }

    @Given("^\"(.*?)\" is proxied at \"/proxy/(.*?)\"$")
    public void is_proxied_at(String endpoint, String proxy) throws Throwable {
        driver.createProxy(proxy, normaliseUrl(endpoint));
    }

    String normaliseUrl(String endpoint) {
        if (endpoint.startsWith("/")) {
            endpoint = "https://localhost:" + port + endpoint;
        }
        return endpoint;
    }

    @When("^a request is successfully made to \"(.*?)\"$")
    public void a_request_is_successfully_made_to(String path)
            throws Throwable {
        driver.getUrl(path);
    }

    @Then("^\"(.*?)\" will be listed in the endpoints proxied by \"/proxy/(.*?)\"$")
    public void will_be_listed_in_the_endpoints_proxied_by(String endpoint,
            String proxy) throws Throwable {
        driver.checkEndpointExists(proxy, normaliseUrl(endpoint));
    }

    @Then("^the endpoint will be shown as available$")
    public void the_endpoint_will_be_shown_as_available() throws Throwable {
        driver.checkCurrentEndpointAvailable();
    }

}
