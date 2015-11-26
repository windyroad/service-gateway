package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.HashMap;
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
    public void createProxy(String proxyName, String endpoint) {
        webDriver.get(
                "https://localhost:" + config.getPort() + "/admin/proxies");
        WebElement form = (new WebDriverWait(webDriver, 5))
                .until(ExpectedConditions
                        .presenceOfElementLocated(By.name("createProxy")));
        List<WebElement> inputs = form.findElements(By.tagName("input"));
        HashMap<String, String> context = new HashMap<>();
        context.put("proxyName", proxyName);
        context.put("endpoint", endpoint);
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
    }

    @Override
    public void get(String path) throws Exception {
        // just being explicit about calling the rest driver's get
        super.get(path);
    }

    @Override
    public void checkEndpointExists(String proxyName, String endpointPath) {
        webDriver.get(webDriver.getCurrentUrl());
        // need to find the linked entities on the page.
        WebElement entitiesContainer = webDriver.findElement(By.id("entities"));

        List<WebElement> entities = entitiesContainer
                .findElements(By.className("entity"));
        assertThat(entities.size(), equalTo(1));
        assertThat(entities.get(0).getText(),
                equalTo("Endpoint `test/" + endpointPath + '`'));
        entities.get(0).click();
    }

    @Override
    public void checkCurrentEndpointAvailable() {
        webDriver.get(webDriver.getCurrentUrl());
        WebElement available = webDriver
                .findElement(By.id("property:available"));
        assertThat(available.getText(), equalTo("true"));
    }

}
