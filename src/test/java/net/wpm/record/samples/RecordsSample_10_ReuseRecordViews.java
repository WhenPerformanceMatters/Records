package net.wpm.record.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wpm.record.Records;

/**
 * Calling Structs.of(...) multiple times creates many record view instances 
 * and allocates the needed amount of memory for the underlying records. At
 * the same time it diminishes one of the advantages of Java Records. A single
 * record view instance is enough to access different records, reducing the 
 * produced garbage.
 * 
 * 
 * @author Nico Hezel
 */
public class RecordsSample_10_ReuseRecordViews {

	private static Logger log = LoggerFactory.getLogger(RecordsSample_10_ReuseRecordViews.class);

	public static void main(String[] args) {
		
		// get a record view, our view of memory
		Sample10 obj = Records.of(Sample10.class);
		obj.setNumber(5);
		obj.setFraction(4.3f);
		
		// reuse the record view, point to a new record 
		Sample10 otherObj = Records.create(obj);
		otherObj.setNumber(-7);
		otherObj.setFraction(1.23f);
		
		// prints -> {Number: -7, Fraction: 1.23}
		log.info(otherObj.toString());
		
		// obj and otherObj are record view
		if(obj == otherObj)
			log.info("obj and otherObj are the same object");
		
	}

	protected static interface Sample10 {
		
		public int getNumber();
		public void setNumber(int number);
		
		public float getFraction();
		public void setFraction(float fraction);
		
	}
}
