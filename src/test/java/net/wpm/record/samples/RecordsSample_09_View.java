package net.wpm.record.samples;

import net.wpm.record.Records;

/**
 * The Records.view(record) method is a shortcut for Records.get(blueprint, Records.id(record))
 * and is overall faster. Even better is the optional view() method for a blueprint.
 * 
 * When storing records as a variable in another class, a separate view object is helpful.
 * Keep in mind each view is a real Java object with all its overhead and costs 24 bytes of memory.
 * 
 * @author Nico
 */
public class RecordsSample_09_View {

	public static void main(String[] args) {
		
		// get a record
		Sample11 obj = Records.of(Sample11.class);
		obj.setFraction(0.3f);
		
		// prints -> {Fraction: 0.3}
		System.out.println(obj);
	
		// create a separate record view pointing to the same content
		Sample11 otherView = Records.view(obj);
		otherView.setFraction(0.5f);	
		
		// prints -> {Fraction: 0.5}
		System.out.println(obj);
		
		// another record view pointing to the same content
		Sample11 thirdView = otherView.view();
		thirdView.setFraction(0.1f);	
		
		// prints -> {Fraction: 0.1}
		System.out.println(obj);
	}	

	protected static interface Sample11 {
		
		public float getFraction();
		public void setFraction(float fraction);
		
		public Sample11 view();
	}
}
