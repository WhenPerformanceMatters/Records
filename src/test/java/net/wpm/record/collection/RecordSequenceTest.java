package net.wpm.record.collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import net.wpm.record.RecordAdapter;
import net.wpm.record.model.TestBlueprint.SimpleValue;

/**
 * Component test
 * 
 * @author Nico Hezel
 */
public class RecordSequenceTest {

	private static Class<SimpleValue> blueprint;
	private static RecordAdapter<SimpleValue> recordAdapter;
	
	private static SimpleValue record;
	private static RecordSequence<SimpleValue> seq;
	
	@Before
	public void setUpBefore() throws Exception {
		blueprint = SimpleValue.class;
		recordAdapter = new RecordAdapter<SimpleValue>(blueprint);
		
		record = recordAdapter.create();
		seq = recordAdapter.array(10);
	}
	
	@After
	public void setupAfterClass() throws Exception {
		recordAdapter.releaseAll();
	}
	
	@Test
	public void sizeTest() {
		assertEquals(10, seq.size());
	}
	
	@Test
	public void getTest() {
		SimpleValue record5 = seq.get(5);
		assertNotEquals(record, record5);
	}
	
	@Test
	public void setTest() {
		record.setValue(77);
		seq.set(3, record);
		
		SimpleValue record3 = seq.get(3);
		assertEquals(77, record3.getValue());
		assertNotEquals(record, record3);
	}
	
	@Test
	public void getWithTest() {
		SimpleValue record5 = seq.get(5, record);
		assertEquals(record, record5);
	}
	
	@Test
	public void forEachTest() {
		AtomicInteger ai = new AtomicInteger();
		seq.forEach(rec -> rec.setValue(ai.getAndIncrement()));
		
		AtomicInteger sum = new AtomicInteger();
		seq.forEach(rec -> sum.addAndGet(rec.getValue()));

		assertEquals(45, sum.get());
	}
	
	@Test
	public void iteratorTest() {
		
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
