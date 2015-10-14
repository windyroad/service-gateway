package au.com.windyroad.hateoas;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

public class HttpLink extends Link {
    URI href;

    @Override
    public URI getHref() {
        return href;
    }

    @Override
    public Entity<?> follow() {
        throw new HttpServerErrorException(HttpStatus.NOT_IMPLEMENTED,
                "TODO: find restTemplate and use it to follow the link");
    }

    //
    //
    // public HttpLink(Method method, URI href, Object... args) {
    // this.rel = method.getAnnotation(Rel.class).value();
    // this.href = href;
    // this.title = computeTitle(method, args);
    //
    // }
    //
    // private String computeTitle(Method method, Object... args) {
    // Title titleAnnotation = method.getAnnotation(Title.class);
    // String title = titleAnnotation == null ? null : titleAnnotation.value();
    // if (title != null) {
    // Parameter[] params = method.getParameters();
    // for (int i = 0; i < args.length; ++i) {
    // PathVariable pathVariable = params[i]
    // .getAnnotation(PathVariable.class);
    // if (pathVariable != null) {
    // title = title.replace("{" + pathVariable.value() + "}",
    // args[0].toString());
    // }
    // }
    // }
    // return title;
    // }

    // public static HttpLink linkTo(Object invocationValue) {
    // URI location = ControllerLinkBuilder.linkTo(invocationValue).toUri();
    // Assert.isInstanceOf(LastInvocationAware.class, invocationValue);
    // LastInvocationAware invocations = (LastInvocationAware) invocationValue;
    //
    // MethodInvocation invocation = invocations.getLastInvocation();
    // Method method = invocation.getMethod();
    //
    // return new HttpLink(method, location, invocation.getArguments());
    // }

}
