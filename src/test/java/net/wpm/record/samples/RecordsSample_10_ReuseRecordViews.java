package net.wpm.record.samples;

import net.wpm.record.Records;

/**
 * Calling Structs.of(...) multiple times creates many record view instances 
 * and allocates the needed amount of memory for the underlying records. At
 * the same time it diminishes one of the advantages of Java Records. A single
 * record view instance is enough to access different records, reducing the 
 * produced garbage.
 * 
 * @author Nico Hezel
 */
public class RecordsSample_10_ReuseRecordViews {

	public static void main(String[] args) {
		
		// get a record view, our view of memory
		Sample06 obj = Records.of(Sample06.class);
		obj.setNumber(5);
		obj.setFraction(4.3f);
		
		// reuse the record view, point to a new record 
		Sample06 otherObj = Records.create(obj);
		otherObj.setNumber(-7);
		otherObj.setFraction(1.23f);
		
		// prints -> {Number: -7, Fraction: 1.23}
		System.out.println(otherObj);
		
		// obj and otherObj are record view
		if(obj == otherObj)
			System.out.println("obj and otherObj are the same object");
		
	}

	protected static interface Sample06 {
		
		public int getNumber();
		public void setNumber(int number);
		
		public float getFraction();
		public void setFraction(float fraction);
		
	}
}
