package au.com.windyroad.servicegateway.driver;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import au.com.windyroad.servicegateway.TestContext;
import cucumber.api.PendingException;

@Component
@Profile(value = "ui-integration")
public class HtmlDriver extends RestDriver {

	@Autowired
	private WebDriver webDriver;

	@Value("${security.user.name:user}")
	String name;

	@Value("${security.user.password:password}")
	String password;

	@Override
	public void createProxy(TestContext context) throws Exception {
		webDriver.get("https://localhost:" + config.getPort() + "/admin/proxy");
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
