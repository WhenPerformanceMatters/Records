package net.wpm.record.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A signed or unsigned integer.
 * 
 * @author Nico
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Integer {
		
	/**
	 * https://en.wikipedia.org/wiki/Signedness
	 */
	public enum Signedness { Signed, Unsigned }

	
	/**
	 * Amount of bits to represent the integer
	 * @return
	 */
	public int bit() default 32;	

		
	/**
	 * Is the integer is signed or unsigned
	 * 
	 * @return
	 */
	public Signedness signedness() default Signedness.Signed;
}
