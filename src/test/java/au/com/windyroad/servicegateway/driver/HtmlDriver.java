package au.com.windyroad.servicegateway.driver;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import au.com.windyroad.hateoas.EmbeddedEntityHttpLink;
import au.com.windyroad.hateoas.EmbeddedEntityJavaLink;
import au.com.windyroad.hateoas.HttpLink;
import au.com.windyroad.hateoas.JavaLink;
import au.com.windyroad.hateoas.Link;
import au.com.windyroad.hateoas.client.LinkVisitor;
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
        return new HttpLink(new URI(webDriver.getCurrentUrl()));
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
        proxyLink.accept(new LinkVisitor() {
            @Override
            public void visit(HttpLink link) {
                webDriver.get(link.getHref().toString());
            }

            @Override
            public void visit(
                    EmbeddedEntityJavaLink<?> embeddedEntityJavaLink) {
                throw new HttpServerErrorException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "expected HttpLink, got EmbeddedEntityJavaLink");
            }

            @Override
            public void visit(JavaLink javaLink) {
                throw new HttpServerErrorException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "expected HttpLink, got JavaLink");
            }

            @Override
            public void visit(EmbeddedEntityHttpLink embeddedEntityHttpLink) {
                throw new HttpServerErrorException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "expected HttpLink, got EmbeddedEntityHttpLink");
            }
        });
        // need to find the linked entities on the page.
        WebElement entitiesContainer = webDriver.findElement(By.id("entities"));

        List<WebElement> entities = entitiesContainer
                .findElements(By.className("entity"));
        assertThat(entities.size(), equalTo(1));
        assertThat(entities.get(0).getText(), equalTo("test/" + endpoint));

        try {
            return new HttpLink(new URI(entities.get(0).getAttribute("href")));
        } catch (URISyntaxException e) {
            throw new AssertionError("unexpected exception", e);
        }
    }
}
