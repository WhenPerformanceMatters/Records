package net.wpm.record.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.Test;

import net.wpm.record.RecordAdapter;
import net.wpm.record.RecordView;
import net.wpm.record.model.TestBlueprint.SimpleValue;

/**
 * Component test
 * 
 * @author Nico Hezel
 */
public class RecordSequenceTest {

	private static SimpleValue record;
	private static Class<SimpleValue> blueprint;
	private static RecordAdapter<SimpleValue> recordAdapter;
	
	@Before
	public void setUpBeforeClass() throws Exception {
		blueprint = SimpleValue.class;
		recordAdapter = new RecordAdapter<SimpleValue>(blueprint);
		record = recordAdapter.create();
	}
	
	@Test
	public void sizeTest() {
		long address = ((RecordView)record).getRecordId();
		RecordSequence<SimpleValue> seq = new RecordSequence<>(recordAdapter, address, 10);
		assertEquals(10, seq.size());
	}
	
	@Test
	public void getTest() {
		long address = ((RecordView)record).getRecordId();
		RecordSequence<SimpleValue> seq = new RecordSequence<>(recordAdapter, address, 10);
		SimpleValue record2 = seq.get(5);
		assertNotEquals(record, record2);
	}
	
	@Test
	public void getWithTest() {
		long address = ((RecordView)record).getRecordId();
		RecordSequence<SimpleValue> seq = new RecordSequence<>(recordAdapter, address, 10);
		SimpleValue record2 = seq.get(5, record);
		assertEquals(record, record2);
	}
	
	@Test
	public void forEachTest() {
		long address = ((RecordView)record).getRecordId();
		RecordSequence<SimpleValue> seq = new RecordSequence<>(recordAdapter, address, 10);
		AtomicInteger ai = new AtomicInteger();
		seq.forEach(rec -> rec.setValue(ai.getAndIncrement()));
		
		AtomicInteger sum = new AtomicInteger();
		seq.forEach(rec -> sum.addAndGet(rec.getValue()));

		assertEquals(45, sum.get());
	}
	
	@Test
	public void iteratorTest() {
		long address = ((RecordView)record).getRecordId();
		RecordSequence<SimpleValue> seq = new RecordSequence<>(recordAdapter, address, 10);
		
		// set values
		Iterator<SimpleValue> itr = seq.iterator();
		int counter = 0;
		while(itr.hasNext()) 
			itr.next().setValue(counter++);
		
		// get values
		itr = seq.iterator();
		int sum = 0;
		while(itr.hasNext()) 
			sum += itr.next().getValue();
		
		assertEquals(45, sum);
	}
	
	@Test
	public void iteratableTest() {
		long address = ((RecordView)record).getRecordId();
		RecordSequence<SimpleValue> seq = new RecordSequence<>(recordAdapter, address, 10);
		
		// set values
		int counter = 0;
		for (SimpleValue simpleValue : seq)
			simpleValue.setValue(counter++);
		
		// get values
		int sum = 0;
		for (SimpleValue simpleValue : seq)
			sum += simpleValue.getValue();
		
		assertEquals(45, sum);
	}
}
