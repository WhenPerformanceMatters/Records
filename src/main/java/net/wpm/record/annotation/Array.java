package net.wpm.record.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define the length of the array for all records of the same type.
 * 
 * @author Nico Hezel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //on method level
public @interface Array {
	
	// size of the array
	public int size() default 10;	
}
