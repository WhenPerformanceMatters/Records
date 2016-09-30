package net.wpm.record.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wpm.record.Records;

/**
 * Calling Records.of(...) multiple times creates many record view instances 
 * and allocates the needed amount of memory for the underlying records. At
 * the same time it diminishes one of Java Records advantages. A single
 * record view instance is enough to access different records, reducing the 
 * produced garbage.
 * 
 * It is important to always keep the record id or a reference of a record
 * to avoid memory leaks.
 * 
 * @author Nico Hezel
 */
public class RecordsSample_10_ReuseRecordViews {

	private static Logger log = LoggerFactory.getLogger(RecordsSample_10_ReuseRecordViews.class);

	public static void main(String[] args) {
		
		// get a record view, our view of memory
		Sample10 obj = Records.of(Sample10.class);
		obj.setNumber(5);
		long objId = Records.id(obj);
		
		// prints -> {Number: 5}
		log.info(obj.toString());
		
		// reuse the record view, point to a new record 
		Sample10 otherObj = Records.create(obj);
		otherObj.setNumber(-7);
		long otherObjId = Records.id(otherObj);

		// prints -> {Number: -7}
		log.info(obj.toString());
		
		// obj and otherObj are the same object
		if(obj == otherObj)
			log.info("obj and otherObj are the same object");
		
		// but have seen different records over time
		if(objId != otherObjId)
			log.info("objId and otherObjId are different records");
	}

	protected static interface Sample10 {		
		public int getNumber();
		public void setNumber(int number);	
	}
}
