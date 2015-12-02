package au.com.windyroad.hateoas.server.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@JsonIgnore
public @interface HateoasChildren {

    String value();
}
