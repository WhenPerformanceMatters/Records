package net.wpm.record.samples;

import net.wpm.record.Records;

/**
 * Unions
 * 
 * @author Nico Hezel
 */
public class RecordsSample_13_NestedRecords {

	public static void main(String[] args) {
		
		// register Bar.class before Sample13.class
		Records.register(Bar.class);
		
		// register the blueprint
		int blueprintId = Records.register(Sample13.class);
		
		
		
		// create a new record and get a record view of it
		Sample13 obj = Records.create(blueprintId);
		
		// manipulate it
		obj.setNumber(5);		
		Bar bar = obj.getBar();
		bar.setFraction(0.1f);
		
		// prints -> {Bar: {Fraction: 0.1}, Number: 5}
		System.out.println(obj);
		
		
		
		// create another record and fill it
		Sample13 otherView = Records.create(blueprintId);	
		
		 // reuse the Bar record view
		bar = otherView.getBar(bar);
		bar.setFraction(0.4f);		
		
		// prints -> {Bar: {Fraction: 0.4}, Number: 0}
		System.out.println(otherView);
		
		
		
		// set-record methods copy the content of a record ...
		obj.setBar(bar); 
		
		// prints -> {Bar: {Fraction: 0.4}, Number: 5}
		System.out.println(obj);
				
		// ... and do not store reference to an object
		bar.setFraction(0.7f);
		System.out.println(otherView);	// {Bar: {Fraction: 0.7}, Number: 0}
		System.out.println(obj);		// {Bar: {Fraction: 0.4}, Number: 5}
		
		
		
		// the Bar record view needs to point to the obj's data before changes can be made
		obj.getBar(bar).setFraction(1.0f);
		System.out.println(otherView);	// {Bar: {Fraction: 0.7}, Number: 0}
		System.out.println(obj);		// {Bar: {Fraction: 1.0}, Number: 5}
	}

	protected static interface Bar {
		public float getFraction();
		public void setFraction(float fraction);
	}
	
	protected static interface Sample13 {
		
		public int getNumber();
		public void setNumber(int number);
		
		public Bar getBar();
		public Bar getBar(Bar with);
		public void setBar(Bar bar);		
	}
}
