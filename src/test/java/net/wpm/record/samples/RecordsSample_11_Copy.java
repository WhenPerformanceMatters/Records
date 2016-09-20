package net.wpm.record.samples;

import net.wpm.record.Records;

/**
 * 
 * 
 * @author Nico Hezzel
 */
public class RecordsSample_11_Copy {

	public static void main(String[] args) {
		
		// get a record
		Sample11 obj = Records.of(Sample11.class);
		obj.setFraction(0.3f);
		
		// prints -> {Number: 0, Fraction: 0.3}
		System.out.println(obj);
	
		// create a new record and copy the content of existing record
		Sample11 otherObj = Records.copy(obj);
		otherObj.setFraction(0.5f);
		
		// a third record view pointing to the content of the record
		long structId = Records.id(otherObj);
		Sample11 thirdObj = Records.view(Sample11.class, structId);
		
		// prints -> {Number: 0, Fraction: 0.5}
		System.out.println(thirdObj);
		
		// copy the content from the first object to the thirdObj/otherObj 
		thirdObj.copyFrom(obj);
		
		// prints -> {Number: 0, Fraction: 0.3}
		System.out.println(otherObj);			
	}	

	protected static interface Sample11 {
		
		public int getNumber();
		public void setNumber(int number);
		
		public float getFraction();
		public void setFraction(float fraction);
		
		public Sample11 copy();
		public void copyFrom(Sample11 to);
	}
}
