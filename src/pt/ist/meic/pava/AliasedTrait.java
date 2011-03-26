package pt.ist.meic.pava;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface AliasedTrait {
	Class<?> trait();
	String[] aliases() default {};
}
