package net.wpm.record.samples;

import net.wpm.record.Records;
import net.wpm.record.annotation.Array;

/**
 * Dealing with arrays inside a blueprint is challenging for Java Records. 
 * It allocates a fixed amount of memory for every record of the same type. 
 * Those requiring the arrays to be always at equal length.
 * 
 * An annotation at one of the blueprint method can specify the length of 
 * the array. Leaving out such information creates an array of length one.
 * 
 * @author Nico Hezel
 *
 */
public class RecordsSample_03_Array {

	public static void main(String[] args) {
		
		// get a record with an array inside
		Sample03 obj = Records.of(Sample03.class);
		
		// writes the number 5 in the first index of the array
		obj.setNumber(5);
		
		// prints -> "Number: 5"
		System.out.println("Number: "+obj.getNumberAt(0));
		
		// prints -> "Number: 7"
		obj.setNumberAt(2, 7);
		System.out.println("Number: "+obj.getNumberAt(2));
		
		// prints -> "Still: 5"
		System.out.println("Still: "+obj.getNumber());
	}

	protected static interface Sample03 {
		
		// get-at signals Java Records an array is needed 
		public int getNumberAt(int index);
		
		// defines the size of the array 
		@Array(size=5)
		public void setNumberAt(int index, int number);
		
		// accesses the first element of the numbers array
		public int getNumber();
		
		// writes the first element of the array
		public void setNumber(int number);
	}
}
