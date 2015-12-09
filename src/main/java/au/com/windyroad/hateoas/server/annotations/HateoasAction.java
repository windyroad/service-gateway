package au.com.windyroad.hateoas.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.http.HttpMethod;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface HateoasAction {

    HttpMethod nature() default HttpMethod.GET;

}
