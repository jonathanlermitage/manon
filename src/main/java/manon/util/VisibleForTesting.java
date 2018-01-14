package manon.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/** Indicates that the visibility of a method has been relaxed to make the code testable. */
@Documented
@Target(METHOD)
@Retention(SOURCE)
public @interface VisibleForTesting {
    
    String why() default "";
}
