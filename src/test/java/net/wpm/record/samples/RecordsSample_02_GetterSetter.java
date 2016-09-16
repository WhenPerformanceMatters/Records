package net.wpm.record.samples;

import net.wpm.record.Records;

/**
 * If the signature of a method in a blueprint matches a typical getter/setter method,  
 * Java Records allocates memory of the size necessary to store the underlying data and
 * defines the get/set-methods to access it.
 * 
 * The entire piece of memory needed to store all the data of a blueprint is called record. 
 * A record is therefore a piece of memory structured in a way defined by the blueprint. 
 * The Records API builds record viewer classes to read and manipulate the records.
 *  
 * Many predefined methods can help to change the value of the variable.
 * 
 * @author Nico Hezel
 *
 */
public class RecordsSample_02_GetterSetter {

	public static void main(String[] args) {
		
		// create a new record and a viewer pointing to it
		Sample02 obj = Records.of(Sample02.class);
		
		// prints -> "Number: 0"
		System.out.println("Number: "+obj.getNumber());
		
		// prints -> "Number: 5"
		obj.setNumber(5);
		System.out.println("Number: "+obj.getNumber());
		
		// prints -> "Number: 6"
		obj.increaseNumber();
		System.out.println("Number: "+obj.getNumber());
		
		// prints -> "Number: 3"
		obj.decreaseNumberBy(3);
		System.out.println("Number: "+obj.getNumber());
		
		// prints -> "Number: 4"
		obj.decreaseNumberBy(-1);
		System.out.println("Number: "+obj.getNumber());
	}

	protected static interface Sample02 {
		
		public int getNumber();
		public void setNumber(int number);
		
		public void increaseNumber();
		public void increaseNumberBy(int amount);
		
		public void decreaseNumber();
		public void decreaseNumberBy(int amount);
	}
}
