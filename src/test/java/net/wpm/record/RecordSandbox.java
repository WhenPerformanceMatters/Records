package net.wpm.record;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class exists only for test purposes. 
 * 
 * @author Nico Hezel
 */
public class RecordSandbox {

	private static Logger log = LoggerFactory.getLogger(RecordSandbox.class);

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {
		
		Records.register(Bar.class);
		
		Foo foo = Records.of(Foo.class);		
		foo.setInt(5);
		
		Bar bar = foo.getBar();
		bar.setFloat(0.1f);
		
		log.info("Foo: "+foo);	
		
		
		// 
		Foo otherFoo = Records.of(Foo.class);		
		bar = otherFoo.getBar(bar); // reuse bar
		bar.setFloat(0.4f);
		
		log.info("other Foo: "+otherFoo);
		
		
		
		// setBar copies the data
		foo.setBar(bar); 
		log.info("Foo: "+foo);	
		
		// changing bar changes other foo but not foo
		bar.setFloat(0.7f);
		log.info("other Foo: "+otherFoo);
		log.info("Foo: "+foo);	

		
		// bar need to point to foo's data before changes can be made
		foo.getBar(bar).setFloat(1.0f);
		log.info("other Foo: "+otherFoo);
		log.info("Foo: "+foo);
	}

	protected static interface Bar {
		public float getFloat();
		public void setFloat(float number);
	}
	
	protected static interface Foo {
		public int getInt();
		public void setInt(int number);

		public Bar getBar();
		public Bar getBar(Bar with);
		public void setBar(Bar bar);
	}
}