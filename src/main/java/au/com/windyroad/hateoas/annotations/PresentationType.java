package au.com.windyroad.hateoas.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PresentationType {
    String value();

    // Soured from
    // http://www.w3.org/TR/html51/semantics.html#the-input-element on
    // 2015/09/21

    public static final String HIDDEN = "hidden";
    public static final String TEXT = "text";
    public static final String SEARCH = "search";
    public static final String TEL = "tel";
    public static final String URL = "url";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String DATETIME = "datetime";
    public static final String DATE = "date";
    public static final String MONTH = "month";
    public static final String WEEK = "week";
    public static final String TIME = "time";
    public static final String NUMBER = "number";
    public static final String RANGE = "range";
    public static final String COLOR = "color";
    public static final String CHECKBOX = "checkbox";
    public static final String RADIO = "radio";
    public static final String FILE = "file";
    public static final String SUBMIT = "submit";
    public static final String IMAGE = "image";
    public static final String RESET = "reset";
    public static final String BUTTON = "button";
}
