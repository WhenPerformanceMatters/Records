package net.wpm.record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

import net.wpm.record.collection.RecordSequence;
import net.wpm.record.model.TestBlueprint.SimpleValue;


/**
 * Function and component tests for the Records API.
 * 
 * @author Nico Hezel
 */
public class RecordsTest {

	private static SimpleValue record;
	private static int blueprintId;
	private static Class<SimpleValue> blueprint;
	private static RecordAdapter<SimpleValue> recordAdapter;
	
	@Before
	public void setUpBeforeClass() throws Exception {
		blueprint = SimpleValue.class;
		recordAdapter = new RecordAdapter<SimpleValue>(blueprint);
		blueprintId = Records.register(recordAdapter);
		record = recordAdapter.create();
	}

	
	
	// ----------------------------------------------------------------------------------
	// -------------------------------- function tests ----------------------------------
	// ----------------------------------------------------------------------------------

	@Test
	public void arrayByBlueprintTest() {
		RecordSequence<SimpleValue> seq = Records.array(blueprint, 3);
		for (int i = 0; i < 3; i++) 
			seq.get(i).setValue(i);
		
		int sum = 0;
		for (SimpleValue record : seq)
			sum += record.getValue();
		assertEquals(3, sum);
	}
	
	@Test
	public void arrayByBlueprintIdTest() {
		RecordSequence<SimpleValue> seq = Records.array(blueprintId, 3);
		for (int i = 0; i < 3; i++) 
			seq.get(i).setValue(i);
		
		int sum = 0;
		for (SimpleValue record : seq)
			sum += record.getValue();
		assertEquals(3, sum);
	}
	
	@Test
	public void blueprintIdByBlueprintTest() {
		int bId = Records.blueprintId(blueprint);
		assertEquals(blueprintId, bId);
	}
	
	@Test
	public void blueprintIdByRecordTest() {
		int bId = Records.blueprintId(record);
		assertEquals(blueprintId, bId);
	}
	
	@Test
	public void copyTest() {
		SimpleValue record1 = Records.create(blueprintId);
		record1.setValue(5);
		SimpleValue record2 = Records.copy(record1);
		assertNotEquals(Records.id(record1), Records.id(record2));
		assertEquals(record1.getValue(), record2.getValue());	
	}	
	
	@Test
	public void copyFromTest() {
		SimpleValue record1 = Records.create(blueprintId);
		record1.setValue(5);
		SimpleValue record2 = Records.create(blueprintId);
		Records.copy(record1, record2);
		assertNotEquals(Records.id(record1), Records.id(record2));
		assertEquals(record1.getValue(), record2.getValue());	
	}

	@Test
	public void createByBlueprintIdTest() {
		SimpleValue record1 = Records.create(blueprintId);
		assertEquals(0, record1.getValue());
	}
	
	@Test
	public void createByBlueprintTest() {
		SimpleValue record1 = Records.create(blueprint);
		assertEquals(0, record1.getValue());
	}
	
	@Test
	public void createByReuseRecordTest() {
		SimpleValue oldRecord = Records.create(blueprintId);
		oldRecord.setValue(10);
		SimpleValue record1 = Records.create(oldRecord);
		assertEquals(0, record1.getValue());
	}
	
	@Test
	public void avoidOverrideWithReuseConstructorTest() {		
		SimpleValue oldRecord = Records.create(blueprintId);
		long oldId = Records.id(oldRecord);
		SimpleValue newRecord = Records.create(oldRecord);
		long newId = Records.id(newRecord);
		assertNotEquals(oldId, newId);
		
		long oldIdAgain = Records.id(oldRecord);
		assertEquals(oldIdAgain, newId);
	}
	
	@Test
	public void avoidOverrideByConstructionTest() {
		SimpleValue record1 = Records.create(blueprintId);
		SimpleValue record2 = Records.create(blueprintId);
		record1.setValue(10);
		assertNotEquals(record1.getValue(), record2.getValue());
	}

	@Test
	public void createManyTest() {
		SimpleValue[] arr = new SimpleValue[16];
		int blueprintId = Records.register(blueprint);

		// create new records and check if they are empty
		for (int i = 0; i < arr.length; i++) {
			SimpleValue pojo = arr[i] = Records.create(blueprintId);
			int currentInt = pojo.getValue(); 
			assertEquals(0, currentInt);
			pojo.setValue(i);
		}
		
		// check newly defined values
		for (int i = 0; i < arr.length; i++) {
			assertEquals(i, arr[i].getValue());
		}
	}

	@Test
	public void recordIdTest() {
		SimpleValue record1 = Records.create(blueprintId);
		assertEquals(Records.id(record1), ((RecordView)record1).getRecordId());
	}
	
	@Test
	public void recordOfBlueprintTest() {
		SimpleValue record1 = Records.of(blueprint);
		assertEquals(0, record1.getValue());
	}
	
	@Test
	public void registeringBlueprintTest() {
		int bId = Records.register(blueprint);
		assertEquals(blueprintId, bId);
	}
	
	@Test
	public void avoidRegisteringAdapterTwiceTest() {
		int bId = Records.register(recordAdapter);
		assertEquals(blueprintId, bId);
	}
	
	@Test
	public void sizeByBlueprintIdTest() {
		assertEquals(4, Records.size(blueprintId));
	}
	
	@Test
	public void sizeByBlueprintTest() {
		assertEquals(4, Records.size(blueprint));
	}
	
	@Test
	public void sizeByRecordTest() {
		assertEquals(4, Records.size(record));
	}

	@Test
	public void viewByRecordTest() {
		SimpleValue record1 = Records.create(blueprintId);
		SimpleValue record2 = Records.view(record1);
		assertEquals(Records.id(record1), Records.id(record2));
		assertNotEquals(record1, record2);
	}
	
	@Test
	public void viewByBlueprintIdTest() {
		SimpleValue record1 = Records.create(blueprintId);
		SimpleValue record2 = Records.view(blueprintId);
		assertNotEquals(Records.id(record1), Records.id(record2));
		assertNotEquals(record1, record2);
	}
	
	@Test
	public void viewByBlueprintTest() {
		SimpleValue record1 = Records.create(blueprintId);
		SimpleValue record2 = Records.view(blueprint);
		assertNotEquals(Records.id(record1), Records.id(record2));
		assertNotEquals(record1, record2);
	}
	
	
	@Test
	public void viewByBlueprintAndRecordIdTest() {
		SimpleValue record1 = Records.create(blueprintId);
		SimpleValue record2 = Records.view(blueprint, Records.id(record1));
		assertEquals(Records.id(record1), Records.id(record2));
		assertNotEquals(record1, record2);
	}
	
	@Test
	public void viewByBlueprintIdAndRecordIdTest() {
		SimpleValue record1 = Records.create(blueprintId);
		SimpleValue record2 = Records.view(blueprintId, Records.id(record1));
		assertEquals(Records.id(record1), Records.id(record2));
		assertNotEquals(record1, record2);
	}
	
	@Test
	public void viewByReuseRecordAndRecordIdTest() {
		SimpleValue record1 = Records.create(blueprintId);
		SimpleValue record2 = Records.create(blueprintId);
		SimpleValue pojo3 = Records.view(record2, Records.id(record1));
		assertEquals(Records.id(record1), Records.id(pojo3));
		assertNotEquals(record1, pojo3);
	}
	
	
	
	// ----------------------------------------------------------------------------------
	// ------------------------------- component tests ----------------------------------
	// ----------------------------------------------------------------------------------
	
	@Test
	public void arrayByAdapterTest() {
		RecordSequence<SimpleValue> seq = Records.array(recordAdapter, 3);
		for (int i = 0; i < 3; i++) 
			seq.get(i).setValue(i);
		
		int sum = 0;
		for (SimpleValue record : seq)
			sum += record.getValue();
		assertEquals(3, sum);
	}

	@Test
	public void createByRecordAdapterTest() {
		SimpleValue record1 = Records.create(recordAdapter);
		assertEquals(0, record1.getValue());
	}
	
	@Test
	public void getRecordAdapterByBlueprintIdTest() {
		RecordAdapter<SimpleValue> adapter = Records.getRecordAdapter(blueprintId);
		assertEquals(recordAdapter, adapter);
	}	
	
	@Test
	public void getRecordAdapterByRecordTest() {
		RecordAdapter<SimpleValue> adapter = Records.getRecordAdapter(record);
		assertEquals(recordAdapter, adapter);
	}
	
	@Test
	public void viewByAdapterAndRecordIdTest() {
		SimpleValue record1 = Records.create(blueprintId);
		SimpleValue record2 = Records.view(recordAdapter, Records.id(record1));
		assertEquals(Records.id(record1), Records.id(record2));
		assertNotEquals(record1, record2);
	}
}
