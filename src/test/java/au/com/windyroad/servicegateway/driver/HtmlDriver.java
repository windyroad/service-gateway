package au.com.windyroad.servicegateway.driver;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import au.com.windyroad.servicegateway.TestContext;
import cucumber.api.PendingException;

@Component
@Profile(value = "ui-integration")
public class HtmlDriver extends RestDriver {

	@Override
	public void createProxy(TestContext context) throws Exception {
		throw new PendingException();
	}

	@Override
	public void get(String path) throws Exception {
		throw new PendingException();
	}

	@Override
	public void checkEndpointAvailable(TestContext context) {
		throw new PendingException();
	}

	@Override
	public void checkEndpointExists(TestContext context) {
		throw new PendingException();
	}
}
