package net.wpm.record.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A real number.
 * 
 * TODO: support half precision
 * 
 * @author Nico Hezel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Decimal {
	
	// https://en.wikipedia.org/wiki/IEEE_floating_point#IEEE_754-2008
	public static enum Precision { Single, Double };
	
	public Precision precision() default Precision.Single;	
}
