package net.wpm.record;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import net.wpm.record.RecordAdapter;
import net.wpm.record.RecordView;
import net.wpm.record.Records;
import net.wpm.record.blueprint.TestPojo;

/**
 * Unit tests for some API methods.
 * 
 * @author Nico
 *
 */
public class RecordsTest {

	private static int blueprintId;
	private static Class<TestPojo> blueprint;
	private static RecordAdapter<TestPojo> recordAdapter;
	
	@Before
	public void setUpBeforeClass() throws Exception {
		blueprint = TestPojo.class;
		recordAdapter = new RecordAdapter<TestPojo>(blueprint);
		blueprintId = Records.register(recordAdapter);
	}

	@Test
	public void duplicateRegistrationTest() {
		int newBlueprintId = Records.register(recordAdapter);
		assertEquals("Registration with old blueprint id "+blueprintId+" resulted in a different id "+newBlueprintId, blueprintId, newBlueprintId);
	}
	
	@Test
	public void getRecordAdapterByBlueprintIdTest() {
		RecordAdapter<TestPojo> adapter = Records.getRecordAdapter(blueprintId);
		assertEquals(recordAdapter, adapter);
	}
	

	
	@Test
	public void getRecordAdapterByBlueprintTest() {
		final int blueprintId = Records.blueprintId(blueprint);
		RecordAdapter<TestPojo> adapter = Records.getRecordAdapter(blueprintId);
		assertEquals(recordAdapter, adapter);
	}
	
	@Test
	public void avoidRegisteringTwiceTest() {
		int bId = Records.register(recordAdapter);
		assertEquals(blueprintId, bId);
	}
	
	@Test
	public void recordOfBlueprintTest() {
		TestPojo pojo1 = Records.of(blueprint);
		assertEquals(0, pojo1.getInt1());
	}
	
	@Test
	public void createByBlueprintIdTest() {
		TestPojo pojo1 = Records.create(blueprintId);
		assertEquals(0, pojo1.getInt1());
	}
	
	@Test
	public void createByBlueprintTest() {
		TestPojo pojo1 = Records.create(blueprint);
		assertEquals(0, pojo1.getInt1());
	}
	
	@Test
	public void createByRecordAdapterTest() {
		TestPojo pojo1 = Records.create(recordAdapter);
		assertEquals(0, pojo1.getInt1());
	}
	
	@Test
	public void createByReuseRecordTest() {
		TestPojo oldRecord = Records.create(blueprintId);
		TestPojo pojo1 = Records.create(oldRecord);
		assertEquals(0, pojo1.getInt1());
	}
	
	@Test
	public void avoidOverrideWithReuseConstructorTest() {
		TestPojo oldrecord = Records.create(blueprintId);
		oldrecord.setInt1(10);
		TestPojo pojo1 = Records.create(oldrecord);
		assertEquals("Reused an old record "+oldrecord.getInt1()+" with the new value "+pojo1.getInt1(), pojo1.getInt1(), oldrecord.getInt1());
	}
	
	@Test
	public void avoidOverrideByConstructionTest() {
		TestPojo pojo1 = Records.create(blueprintId);
		TestPojo pojo2 = Records.create(blueprintId);
		pojo1.setInt1(10);
		assertFalse("Defined only Pojo1 as "+pojo1.getInt1()+" but got Pojo2 with value "+pojo2.getInt1(), pojo1.getInt1() == pojo2.getInt1());
	}

	@Test
	public void createManyTest() {
		TestPojo[] arr = new TestPojo[16];
		
		// create new records and check if they are empty
		for (int i = 0; i < arr.length; i++) {
			TestPojo pojo = arr[i] = Records.create(blueprintId);
			int currentInt = pojo.getInt1(); 
			assertEquals(0, currentInt);
			pojo.setInt1(i);
		}
		
		// check newly defined values
		for (int i = 0; i < arr.length; i++) {
			assertEquals(i, arr[i].getInt1());
		}
	}
	
	@Test
	public void recordIdTest() {
		TestPojo pojo1 = Records.create(blueprintId);
		assertEquals(Records.id(pojo1), ((RecordView)pojo1).getRecordId());
	}
	
	@Test
	public void getRecordByBlueprintTest() {
		TestPojo pojo1 = Records.create(blueprintId);
		TestPojo pojo2 = Records.view(blueprint, Records.id(pojo1));
		assertEquals(Records.id(pojo1), Records.id(pojo2));
		assertFalse("Record "+pojo1+" is the same as "+pojo2, pojo1 == pojo2);
	}
}
