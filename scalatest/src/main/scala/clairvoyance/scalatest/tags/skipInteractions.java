package clairvoyance.scalatest.tags;

import org.scalatest.TagAnnotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Inherited
@TagAnnotation
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface skipInteractions {
}
