package net.wpm.record.samples;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wpm.record.RecordAdapter;
import net.wpm.record.Records;
import net.wpm.record.collection.RecordSequence;

public class RecordsPerf_01_Access {

	private static Logger log = LoggerFactory.getLogger(RecordsPerf_01_Access.class);
			
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {

		// access to the records is possible via the Records API or the Record Adapter
		RecordAdapter<Foo> recordAdapter = new RecordAdapter<Foo>(Foo.class);
		int classId = Records.register(recordAdapter);
		
		// create three new records
		Foo obj = Records.create(Foo.class);
		Foo otherObj = Records.create(classId); 	// faster
		Foo anotherObj = recordAdapter.create();	// the fastest
		
		// a record id
		long id = Records.id(obj);
		
		// four record views pointing to the same record
		Foo sameObj1 = Records.view(Foo.class, id);
		Foo sameObj2 = Records.view(classId, id);	// fast
		Foo sameObj3 = recordAdapter.view(id);		// faster
		Foo sameObj4 = obj.view();					// the fastest
		
		// re-use the record view
		long[] objId = new long[] { sameObj1.recordId(), otherObj.recordId(), anotherObj.recordId() };
		for (int i = 0; i < objId.length; i++) {
			
			// point to another record
			sameObj2 = Records.view(sameObj2, objId[i]); 	// faster
			sameObj3.recordId(objId[i]);					// the fastest
			
			// change something
			sameObj2.setInt1(i + 3);
		}
		
		// prints -> "Read int1 3"
		log.info("Read int1 "+sameObj4.getInt1());
		
		// even better is using a sequence of records
		RecordSequence<Foo> fooList = Records.array(Foo.class, 10);
		int counter = 0;
		for (Foo foo : fooList) 
			foo.setInt1(counter++);
		
		// read the data
		long sum = 0;
		for (Foo foo : fooList) 
			sum += foo.getInt1();
		log.info("sum "+sum);		
	}
	
	public static interface Foo {
		
		public byte getByte();
		public void setByte(byte number);
		
		public int getInt1();
		public void setInt1(int number);

		public int getInt2();
		public void setInt2(int number);
		
		public long getLong();
		public void setLong(long number);
		
		public Foo view();
		
		public long recordId();
		public void recordId(long recordId);
	}
}