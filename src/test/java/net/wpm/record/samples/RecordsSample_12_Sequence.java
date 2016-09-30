package net.wpm.record.samples;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wpm.record.Records;
import net.wpm.record.collection.RecordSequence;

/**
 * Most of the time POJO objects are not coming alone. Programs deal with thousand or million of them.
 * In Records it is possible to allocate consecutive memory (like a Java array) to store many records.
 * A collection called RecordSequence gives us access to the these new records.
 * 
 * For efficient access the iterator of the collection reuses a single record view while iterating over
 * all records. It is therefore necessary to create a new view if a reference needs to be stored for 
 * later use. 
 * 
 * @author Nico Hezel
 */
public class RecordsSample_12_Sequence {

	private static Logger log = LoggerFactory.getLogger(RecordsSample_12_Sequence.class);

	public static void main(String[] args) {
		
		// register the blueprint
		int blueprintId = Records.register(Sample12.class);
		
		// create 1000 records in sequence and return a collection to access their content
		RecordSequence<Sample12> sampleArray = Records.array(blueprintId, 1000);
		
		// manipulate all of them
		Random rnd = new Random();
		for (Sample12 sample : sampleArray) 
			sample.setNumber(rnd.nextInt(1000));
		
		// find record with highest number		
		Sample12 best = sampleArray.get(0);		// get(index) creates a new view
		for (Sample12 sample : sampleArray)		// the iterator reuses a single view
			if(best.getNumber() < sample.getNumber())
				best.viewAt(sample);
		
		// prints -> {Number: 0 - 1000}
		log.info(best.toString());
	}

	protected static interface Sample12 {
		
		public int getNumber();
		public void setNumber(int number);
		
		// create a new record view pointing to the same record
		public Sample12 view();
		
		// pointing the current record view to the given record
		public void viewAt(Sample12 at);
	}
}
