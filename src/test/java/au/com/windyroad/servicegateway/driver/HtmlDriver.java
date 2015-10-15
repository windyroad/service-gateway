package au.com.windyroad.servicegateway.driver;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import au.com.windyroad.hateoas.Link;
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
    public Link createProxy(TestContext context) throws Exception {
        webDriver.get(
                "https://localhost:" + config.getPort() + "/admin/proxies");
        WebElement form = (new WebDriverWait(webDriver, 5))
                .until(ExpectedConditions
                        .presenceOfElementLocated(By.name("createProxy")));
        List<WebElement> inputs = form.findElements(By.tagName("input"));
        for (WebElement input : inputs) {
            String inputName = input.getAttribute("name");
            if (inputName != null) {
                Object value = context.get(inputName);
                if (value != null) {
                    input.sendKeys(value.toString());
                }
            }
        }
        form.findElement(By.cssSelector("button[type='submit']")).click();
        WebElement newPage = (new WebDriverWait(webDriver, 5))
                .until(ExpectedConditions
                        .presenceOfElementLocated(By.className("Proxy")));
        return null;
    }

    @Override
    public void get(String path) throws Exception {
        // just being explicit about calling the rest driver's get
        super.get(path);
    }

    @Override
    public void checkEndpointAvailable(Link endpointLink) {
        throw new PendingException();
    }

    @Override
    public Link checkEndpointExists(Link proxyLink, String endpoint) {
        WebElement properties = webDriver.findElement(By.id("properties"));

        String location = properties.findElement(By.id("property:target"))
                .getText();
        throw new PendingException("TODO check endpoint");
    }
}
