package net.wpm.record.samples;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wpm.record.Records;

/**
 * The record only stores the data specified by the blueprint.
 * There is no overhead like the 16 bytes for a Java object header.
 * 
 * To get the exact size of a record, either Records.size(...) or 
 * the optional blueprint method recordSize() can be called.
 *  
 * @author Nico Hezel
 */
public class RecordsSample_08_RecordSize {
	
	private static Logger log = LoggerFactory.getLogger(RecordsSample_08_RecordSize.class);

	public static void main(String[] args) {
		
		// get a record view to a new record
		Sample08 obj1 = Records.of(Sample08.class);
		
		// size of the record in bytes 
		int sizeInBytes = obj1.recordSize(); // same as Records.size(obj1)
				
		// float (4 bytes) + int (4 bytes) = 8 bytes
		if(8 == sizeInBytes && Records.size(Sample08.class) == 8)
			log.info("The content is 8 byte long");	
	}
	

	protected static interface Sample08 {
		
		public int getNumber();
		public void setNumber(int number);
		
		public float getFraction();
		public void setFraction(float fraction);
		
		// size of the record in bytes
		public int recordSize();
		
		// id of the record
		public long recordId();
	}
}
