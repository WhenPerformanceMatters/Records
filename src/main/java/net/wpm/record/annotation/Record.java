package net.wpm.record.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * http://www.mkyong.com/java/java-custom-annotations-example/
 * 
 * @author Nico
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE) //on class level
public @interface Record {

	public boolean AutoRegister() default true;
	public Class<? extends net.wpm.record.RecordView> RecordClass();
}
