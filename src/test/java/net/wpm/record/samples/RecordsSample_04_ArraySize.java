package net.wpm.record.samples;

import net.wpm.record.Records;
import net.wpm.record.annotation.Array;

/**
 * When the length of an array is unknown, a get-size method can help.
 * 
 * The constant size of the arrays in a record allows Java Records to 
 * create a method which only returns a single number e.g.
 * 
 * <pre>
 * public final int getNumberSize() { 
 * 	return 5; 
 * }
 * <pre> 
 * 
 * Since all methods implemented by Java Records are final and the 
 * get-size method returns a constant value, the JVM optimizes the 
 * method-call and replaces it with the actual length of the array.
 * 
 * @author Nico Hezel
 *
 */
public class RecordsSample_04_ArraySize {

	public static void main(String[] args) {
		
		// get a record with an array inside
		Sample04 obj = Records.of(Sample04.class);
		
		// getNumberSize() returns a constant value
		for (int i = 0; i < obj.getNumberSize(); i++)
			obj.setNumberAt(i, -i);
		
		// prints -> 0, -1, -2, -3, -4, 
		for (int i = 0; i < obj.getNumberSize(); i++)
			System.out.print(obj.getNumberAt(i) + ", ");
	}

	protected static interface Sample04 {
		
		// get-at signals Java Records an array is needed 
		public int getNumberAt(int index);
		
		// defines the size of the array 
		@Array(size=5)
		public void setNumberAt(int index, int number);
		
		// returns the length of the array
		public int getNumberSize();
	}
}
