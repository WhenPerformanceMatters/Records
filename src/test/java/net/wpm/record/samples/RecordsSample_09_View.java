package net.wpm.record.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wpm.record.Records;

/**
 * The Records.view(record) method is a shortcut for Records.view(blueprint, Records.id(record)) 
 * and is overall faster. It creates a copy of the record view object. Even better is the 
 * optional view() method for a blueprint.
 * 
 * Since a record view can point to different records, a copy of the view object is needed when 
 * storing a reference of it for later use. Keep in mind each view object is a Java object with 
 * all its overhead and costs.
 * 
 * Instead of creating a new copy every time. A already existing view can be reused with the
 * Records.view(otherRecord, Records.id(record)) method or the optional viewAt(record) method 
 * of the blueprint.	
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
				
		// another record
		Sample09 otherObj = Records.of(Sample09.class);
		
		// point a existing record view to another record
		otherView.viewAt(otherObj);
		otherView.setFraction(0.99f);
		
		// prints -> {Fraction: 0.99}
		log.info(otherObj.toString());
	}	

	protected static interface Sample09 {
		
		public float getFraction();
		public void setFraction(float fraction);
		
		// creates a new record view object pointing to this record
		public Sample09 view();
		
		// point the current record view to the given record 
		// Shortcut for record.recordId(otherRecord.recordId())
		public void viewAt(Sample09 at);
	}
}
