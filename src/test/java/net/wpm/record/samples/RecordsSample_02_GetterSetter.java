package net.wpm.record.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wpm.record.Records;

/**
 * If the signature of a method in a blueprint matches a typical getter/setter method,  
 * Java Records allocates memory of the size necessary to store the underlying data 
 * and defines get/set-methods to access it.
 * 
 * The structured data of a blueprint is called a record. A record is therefore just 
 * a piece of memory structured in a way defined by the blueprint. The Records API 
 * constructs record-view classes and instantiates them to read and manipulate the 
 * records.
 *  
 * Technical speaking there are no variables in a record, but we will still use the
 * term to clarify we talk about a specific part of a record. 
 * 
 * Many predefined optional methods help to change the value of the underlying data.

 * 
 * @author Nico Hezel
 */
public class RecordsSample_02_GetterSetter {

	private static Logger log = LoggerFactory.getLogger(RecordsSample_02_GetterSetter.class);

	public static void main(String[] args) {
		
		// allocates memory for a new record and creates 
		// a record viewer pointing to the new record
		Sample02 obj = Records.of(Sample02.class);
		
		// prints -> "Number: 0"
		log.info("Number: "+obj.getNumber());
		
		// prints -> "Number: 5"
		obj.setNumber(5);
		log.info("Number: "+obj.getNumber());
		
		// prints -> "Number: 6"
		obj.increaseNumber();
		log.info("Number: "+obj.getNumber());
		
		// prints -> "Number: 3"
		obj.decreaseNumberBy(3);
		log.info("Number: "+obj.getNumber());
		
		// prints -> "Number: 4"
		obj.decreaseNumberBy(-1);
		log.info("Number: "+obj.getNumber());
	}

	protected static interface Sample02 {
		
		// all methods access the variable "Number"
		public int getNumber();
		public void setNumber(int number);
		
		// the prefix increase is a shortcut for 
		// setNumber(getNumber() + 1)
		public void increaseNumber();
		public void increaseNumberBy(int amount);
		
		// the prefix increase is a shortcut for 
		// setNumber(getNumber() - 1)
		public void decreaseNumber();
		public void decreaseNumberBy(int amount);
	}
}
