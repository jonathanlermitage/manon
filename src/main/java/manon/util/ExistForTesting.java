package manon.util;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

/** Indicates that the annotated element has been created to make the code testable. */
@SuppressWarnings("unused")
@Documented
@Target({TYPE, FIELD, METHOD})
@Retention(SOURCE)
public @interface ExistForTesting {
    
    String why() default "";
}
