package net.wpm.record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

import net.wpm.record.collection.RecordSequence;
import net.wpm.record.model.TestBlueprint.SimpleValue;

public class RecordAdapterTest {

	private static SimpleValue record;
	private static Class<SimpleValue> blueprint;
	private static RecordAdapter<SimpleValue> recordAdapter;
	
	@Before
	public void setUpBeforeClass() throws Exception {
		blueprint = SimpleValue.class;
		recordAdapter = new RecordAdapter<SimpleValue>(blueprint);
		record = recordAdapter.create();
	}

	
	
	// ----------------------------------------------------------------------------------
	// -------------------------------- function tests ----------------------------------
	// ----------------------------------------------------------------------------------

	@Test
	public void arrayTest() {		
		RecordSequence<SimpleValue> seq = recordAdapter.array(3);
		for (int i = 0; i < 3; i++) 
			seq.get(i).setValue(i);
		
		int sum = 0;
		for (SimpleValue record : seq)
			sum += record.getValue();
		assertEquals(3, sum);
	}
	
	@Test
	public void copyTest() {
		record.setValue(5);
		SimpleValue record2 = recordAdapter.copy(record);
		assertEquals(record.getValue(), record2.getValue());	
		
		long rid = ((RecordView)record).getRecordId();
		long rid2 = ((RecordView)record2).getRecordId();
		assertNotEquals(rid, rid2);
	}	
	
	@Test
	public void copyFromTest() {
		record.setValue(5);
		SimpleValue record2 = recordAdapter.create();
		recordAdapter.copy(record, record2);
		assertEquals(record.getValue(), record2.getValue());	
		
		long rid = ((RecordView)record).getRecordId();
		long rid2 = ((RecordView)record2).getRecordId();
		assertNotEquals(rid, rid2);
	}
	
	@Test
	public void createTest() {
		SimpleValue record1 = recordAdapter.create();
		assertEquals(0, record1.getValue());
	}

	@Test
	public void createByReuseRecordTest() {
		SimpleValue oldRecord = recordAdapter.create();
		oldRecord.setValue(10);
		SimpleValue record1 = (SimpleValue)recordAdapter.create((RecordView)oldRecord);
		assertEquals(0, record1.getValue());
	}
		
	@Test
	public void blueprintTest() {
		Class<?> blu = recordAdapter.getBlueprint();
		assertEquals(blueprint, blu);
	}
	
	@Test
	public void blueprintIdTest() {
		int bId = recordAdapter.getBlueprintId();
		assertEquals(0, bId);
	}
	
	@Test
	public void recordSizeTest() {
		int size = recordAdapter.getRecordSize();
		assertEquals(4, size);
	}
	
	@Test
	public void recordClassTest() {
		Class<?> rClass =  recordAdapter.getRecordClass();
		assertEquals(record.getClass(), rClass);
	}
	
	@Test
	public void newInstanceTest() {
		RecordView recordView = recordAdapter.newInstance();
		assertEquals(-1, recordView.getBlueprintId());
		assertEquals(0, recordView.getRecordId());
	}

	@Test
	public void viewByRecordTest() {
		SimpleValue record2 = recordAdapter.view(record);
		assertNotEquals(record, record2);
		
		long rid = ((RecordView)record).getRecordId();
		long rid2 = ((RecordView)record2).getRecordId();
		assertEquals(rid, rid2);
	}
	
	@Test
	public void viewByRecordIdTest() {
		long id = ((RecordView)record).getRecordId();
		SimpleValue record2 = recordAdapter.view(id);
		assertNotEquals(record, record2);
		
		long rid = ((RecordView)record).getRecordId();
		long rid2 = ((RecordView)record2).getRecordId();
		assertEquals(rid, rid2);
	}
	
	@Test
	public void setBlueprintIdTest() {
		recordAdapter.setBlueprintId(5);
		long bid = ((RecordView)record).getBlueprintId();
		assertEquals(5, bid);
		
		recordAdapter.setBlueprintId(-1);
	}
	
	
	// ----------------------------------------------------------------------------------
	// ------------------------------- component tests ----------------------------------
	// ----------------------------------------------------------------------------------
	
	@Test
	public void nextIdTest() {
		long id1 = recordAdapter.nextId();
		long id2 = recordAdapter.nextId();
		assertEquals(4, id2-id1);
	}
}
