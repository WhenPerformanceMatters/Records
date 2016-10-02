package net.wpm.record.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * http://www.mkyong.com/java/java-custom-annotations-example/
 * @author Nico
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) //on method level
public @interface Text {
	
	public static enum Encoding { ASCII, UTF8 };

	// character count
	public int maxSize() default 10;
	public Encoding encoding() default Encoding.ASCII;
}
