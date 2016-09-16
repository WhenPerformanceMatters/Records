package net.wpm.record.samples;

import net.wpm.record.Records;
import net.wpm.record.collection.RecordSequence;

/**
 * 
 * 
 * @author Nico Hezel
 */
public class RecordsSample_12_Sequence {

	public static void main(String[] args) {
		
		// register the blueprint
		int blueprintId = Records.register(Sample12.class);
		
		// create 100 records in sequence and return a collection to access their content
		RecordSequence<Sample12> sampleArray = Records.array(blueprintId, 1000);
		
		// manipulate all of them
		int count = 7;
		for (Sample12 sample : sampleArray) 
			sample.setNumber(count++);
		
		// prints -> {Number: 12, Fraction: 0.0}
		Sample12 obj = sampleArray.get(5);
		System.out.println(obj);
	}

	protected static interface Sample12 {
		
		public int getNumber();
		public void setNumber(int number);
		
		public float getFraction();
		public void setFraction(float fraction);
		
	}
}
