package net.wpm.record.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wpm.record.Records;

/**
 * The entire content of a record an be copied with the method Records.copy(fromRecord, toRecord)
 * or the optional blueprint method toRecord.copyFrom(fromRecord). If the target record does not
 * exist yet, a simple call to Records.copy(fromRecord) or fromRecord.copy() is enough to create 
 * a new record view pointing to a new record and copying the content over.
 * 
 * @author Nico Hezel
 */
public class RecordsSample_11_Copy {

	private static Logger log = LoggerFactory.getLogger(RecordsSample_11_Copy.class);

	public static void main(String[] args) {
		
		// get a record
		Sample11 obj = Records.of(Sample11.class);
		obj.setFraction(0.3f);
		
		// prints -> {Number: 0, Fraction: 0.3}
		log.info(obj.toString());
	
		// create a new record and copy the content of existing record
		Sample11 otherObj = Records.copy(obj);
		otherObj.setFraction(0.5f);
		
		// copy the content from the first object to the otherObj 
		otherObj.copyFrom(obj);
		
		// prints -> {Number: 0, Fraction: 0.3}
		log.info(otherObj.toString());		
		
		// obj and otherObj are different record views
		if(obj == otherObj)
			log.info("obj and otherObj are different record views ...");
		
		// pointing to different records
		if(Records.id(obj) != Records.id(otherObj))
			log.info("pointing to different records");
	}	

	protected static interface Sample11 {
		
		public int getNumber();
		public void setNumber(int number);
		
		public float getFraction();
		public void setFraction(float fraction);
		
		// allocates memory for a new record, copies the data to the new 
		// record and returns a new record view pointing to the new record
		public Sample11 copy();
		
		// copies the data from the given record to the current record
		public void copyFrom(Sample11 to);
	}
}
