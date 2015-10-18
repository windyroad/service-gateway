package au.com.windyroad.hateoas;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URI;

import org.eclipse.jdt.annotation.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.core.DummyInvocationUtils.LastInvocationAware;
import org.springframework.hateoas.core.DummyInvocationUtils.MethodInvocation;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import au.com.windyroad.hateoas.annotations.Rel;
import au.com.windyroad.hateoas.annotations.Title;
import au.com.windyroad.hateoas.client.LinkVisitor;

/**
 *
 * Links represent navigational transitions. In JSON Siren, links are
 * represented as an array inside the entity, such as { "links": [{ "rel": [
 * "self" ], "href": "http://api.x.io/orders/42"}] }
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(as = HttpLink.class)
public abstract class Link {

    private String[] rel;
    @JsonProperty("class")
    @Nullable
    private String[] classes;

    @Nullable
    private String title;

    @Nullable
    private MediaType type;

    protected Link() {

    }

    public Link(String[] rel) {
        this.rel = rel;
    }

    public Link(MethodInvocation invocation) {
        Method method = invocation.getMethod();

        this.rel = method.getAnnotation(Rel.class).value();
        this.title = computeTitle(method, invocation.getArguments());
    }

    public Link(Object invocationValue, String notused) {
        Assert.isInstanceOf(LastInvocationAware.class, invocationValue);
        LastInvocationAware invocations = (LastInvocationAware) invocationValue;

        MethodInvocation invocation = invocations.getLastInvocation();
        Method method = invocation.getMethod();

        this.rel = method.getAnnotation(Rel.class).value();
        this.title = computeTitle(method, invocation.getArguments());
    }

    private String computeTitle(Method method, Object... args) {
        Title titleAnnotation = method.getAnnotation(Title.class);
        String title = titleAnnotation == null ? null : titleAnnotation.value();
        if (title != null) {
            Parameter[] params = method.getParameters();
            for (int i = 0; i < args.length; ++i) {
                PathVariable pathVariable = params[i]
                        .getAnnotation(PathVariable.class);
                if (pathVariable != null) {
                    title = title.replace("{" + pathVariable.value() + "}",
                            args[i].toString());
                }
            }
        }
        return title;
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
    public abstract URI getHref();

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

    public abstract <T extends Entity<?>> T follow(Class<T> type);

    public abstract <T extends Entity<?>> T follow(
            ParameterizedTypeReference<T> type);

    public abstract void accept(LinkVisitor linkVisitor);

}
