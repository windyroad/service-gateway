package au.com.windyroad.servicegateway;

import javax.annotation.PostConstruct;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.SpringApplicationContextLoader;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import au.com.windyroad.servicegateway.driver.Driver;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@ContextConfiguration(classes = { ServiceGatewayApplication.class,
		ServiceGatewayTestConfiguration.class }, loader = SpringApplicationContextLoader.class)
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = { ServiceGatewayApplication.class,
		ServiceGatewayTestConfiguration.class })
@WebIntegrationTest({ "server.port=0", "management.port=0" })
@TestExecutionListeners({ ServiceGatewayTestExecutionListener.class })
public class StepDefs {

	@Value("${local.server.port}")
	int port;

	@Autowired
	Driver driver;

	@Autowired
	ServiceGatewayTestConfiguration config;

	private Context context = new Context();

	@PostConstruct
	private void initConfig() {
		config.setPort(port);
	}

	@Given("^there are no proxied endpoints listed$")
	public void there_are_no_proxied_endpoints_listed() throws Throwable {
		driver.clearProxies();
	}

	@Given("^a ping service at \"(.*?)\"$")
	public void a_ping_service_at(String path) throws Throwable {
		driver.checkPingService(path);
	}

	@Given("^\"(.*?)\" is proxied at \"(.*?)\"$")
	public void is_proxied_at(String targetEndPoint, String proxyPath)
			throws Throwable {
		driver.createProxy(proxyPath.replace("/proxy/", ""), targetEndPoint);
	}

	@When("^a request is successfully made to \"(.*?)\"$")
	public void a_request_is_successfully_made_to(String path) throws Throwable {
		driver.get(path);
	}

	@Then("^\"(.*?)\" will be listed in the endpoints proxied by \"(.*?)\"$")
	public void will_be_listed_in_the_endpoints_proxied_by(String endpoint,
			String proxy) throws Throwable {
		context.put("proxy", proxy);
		context.put("endpoint", endpoint);
		driver.checkEndpointExists(context);
	}

	@Then("^the endpoint will be shown as available$")
	public void the_endpoint_will_be_shown_as_available() throws Throwable {
		driver.checkEndpointAvailable(context);
	}

}
