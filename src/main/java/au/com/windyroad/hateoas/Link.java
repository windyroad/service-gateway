package au.com.windyroad.hateoas;

import java.lang.reflect.Method;
import java.net.URI;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.hateoas.core.DummyInvocationUtils.LastInvocationAware;
import org.springframework.hateoas.core.DummyInvocationUtils.MethodInvocation;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Links represent navigational transitions. In JSON Siren, links are
 * represented as an array inside the entity, such as { "links": [{ "rel": [
 * "self" ], "href": "http://api.x.io/orders/42"}] }
 * 
 * Links may contain the following attributes:
 */
public class Link {

    private String[] rel;
    @JsonProperty("class")
    @Nullable
    private String[] classes;

    private URI href;
    @Nullable
    private String title;

    @Nullable
    private MediaType type;

    @SuppressWarnings("unused")
    private Link() {
    }

    public Link(String[] rel, URI href) {
        this.rel = rel;
        this.href = href;
    }

    public String[] getRel() {
        return rel;
    }

    /**
     * @return the classes
     */
    public String[] getClasses() {
        return classes;
    }

    /**
     * @param classes
     *            the classes to set
     */
    public void setClasses(String[] classes) {
        this.classes = classes;
    }

    /**
     * @return the href
     */
    public URI getHref() {
        return href;
    }

    /**
     * @param href
     *            the href to set
     */
    public void setHref(URI href) {
        this.href = href;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the type
     */
    public MediaType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(MediaType type) {
        this.type = type;
    }

    /**
     * @param rel
     *            the rel to set
     */
    public void setRel(String[] rel) {
        this.rel = rel;
    }

    public static Link linkTo(Object invocationValue) {
        URI location = ControllerLinkBuilder.linkTo(invocationValue).toUri();
        Assert.isInstanceOf(LastInvocationAware.class, invocationValue);
        LastInvocationAware invocations = (LastInvocationAware) invocationValue;

        MethodInvocation invocation = invocations.getLastInvocation();
        Method method = invocation.getMethod();
        return new Link(method.getAnnotation(Rel.class).value(), location);
    }

}
