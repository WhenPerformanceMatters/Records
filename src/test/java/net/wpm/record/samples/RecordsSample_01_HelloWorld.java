package net.wpm.record.samples;

import net.wpm.record.Records;

/**
 * With the help of Java Records it is possible to create POJO (Plain old Java Object) like 
 * instances of an existing interface. In the Java Records world those interfaces are called 
 * blueprints. They define the structure of the underlying data. 
 * 
 * Most of the functionality is available via the net.wpm.record.Records API. 
 * 
 * In this first example nothing special happens except an instance of an interface is 
 * created and a default method gets called.
 * 
 * @author Nico Hezel
 *
 */
public class RecordsSample_01_HelloWorld {

	public static void main(String[] args) {
		
		// get a instance of an blueprint
		Sample01 obj = Records.of(Sample01.class);
		
		// say: Hello World!
		obj.greet();
	}

	protected static interface Sample01 {
		public default void greet() {
			System.out.println("Hello world!");
		}
	}
}
