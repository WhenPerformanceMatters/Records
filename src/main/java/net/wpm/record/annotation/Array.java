package net.wpm.record.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * http://www.mkyong.com/java/java-custom-annotations-example/
 * 
 * @author Nico Hezel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //on method level
public @interface Array {
	
	// size of the array
	public int size() default 10;	
}
