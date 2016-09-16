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
		Sample10 obj = Records.of(Sample10.class);
		obj.setFraction(0.3f);
		
		// prints -> {Number: 0, Fraction: 0.3}
		System.out.println(obj);
	
		// create a new record and copy the content of existing record
		Sample10 otherObj = Records.copy(obj);
		otherObj.setFraction(0.5f);
		
		// a third record view pointing to the content of the record
		long structId = Records.id(otherObj);
		Sample10 thirdObj = Records.view(Sample10.class, structId);
		
		// prints -> {Number: 0, Fraction: 0.5}
		System.out.println(thirdObj);
		
		// copy the content from the first object to the thirdObj/otherObj 
		thirdObj.copyFrom(obj);
		
		// prints -> {Number: 0, Fraction: 0.3}
		System.out.println(otherObj);			
	}	

	protected static interface Sample10 {
		
		public int getNumber();
		public void setNumber(int number);
		
		public float getFraction();
		public void setFraction(float fraction);
		
		public Sample10 copy();
		public void copyFrom(Sample10 to);
	}
}
