package net.wpm.record.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wpm.record.Records;

/**
 * The Records.view(record) method is a shortcut for Records.get(blueprint, Records.id(record))
 * and is overall faster. Even better is the optional view() method for a blueprint.
 * 
 * When storing records as a variable in another class, a separate view object is helpful.
 * Keep in mind each view is a real Java object with all its overhead and costs 24 bytes of memory.
 * 
 * TODO: add public void view(Sample06);	
 *  
 * @author Nico Hezel
 */
public class RecordsSample_09_View {

	private static Logger log = LoggerFactory.getLogger(RecordsSample_09_View.class);

	public static void main(String[] args) {
		
		// get a record
		Sample09 obj = Records.of(Sample09.class);
		obj.setFraction(0.3f);
		
		// prints -> {Fraction: 0.3}
		log.info(obj.toString());
	
		// create a separate record view pointing to the same content
		Sample09 otherView = Records.view(obj);
		otherView.setFraction(0.5f);	
		
		// prints -> {Fraction: 0.5}
		log.info(obj.toString());
		
		// another record view pointing to the same content
		Sample09 thirdView = otherView.view();
		thirdView.setFraction(0.1f);	
		
		// prints -> {Fraction: 0.1}
		log.info(obj.toString());
	}	

	protected static interface Sample09 {
		
		public float getFraction();
		public void setFraction(float fraction);
		
		public Sample09 view();
	}
}
