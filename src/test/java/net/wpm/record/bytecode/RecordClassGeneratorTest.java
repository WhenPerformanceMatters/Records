package net.wpm.record.bytecode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.wpm.codegen.utils.DefiningClassLoader;
import net.wpm.record.RecordView;
import net.wpm.record.blueprint.BlueprintClass;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintMethod.ActionType;
import net.wpm.record.blueprint.BlueprintVariable;
import net.wpm.record.bytes.UnsafeMemoryAdapter;
import net.wpm.record.model.TestBlueprint;
import net.wpm.record.model.TestBlueprint.SimpleValue;
import net.wpm.reflectasm.ClassAccess;
import net.wpm.reflectasm.FieldAccess;

/**
 * Component Test.
 * Testing internal behaviors.
 * 
 * @author Nico Hezel
 */
public class RecordClassGeneratorTest {

	protected static Class<? extends TestBlueprint> blueprint = TestBlueprint.class;
	
	protected UnsafeMemoryAdapter memoryAccess = UnsafeMemoryAdapter.getInstance();
	
	/**
	 * Create a record view out of the given methods
	 * 
	 * @param methods
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	protected <B> B createRecordView(Class<B> clazz, BlueprintMethod ... methods) throws InstantiationException, IllegalAccessException {
		
		// define the blueprint class
		BlueprintClass blueprintClass = new BlueprintClass(clazz);
		for (BlueprintMethod blueprintMethod : methods)			
			blueprintClass.addMethod(blueprintMethod);
	
		// create the record view class
		RecordClassGenerator classGenerator = new RecordClassGenerator(blueprintClass);
		Class<RecordView> recordViewClass = classGenerator.construct();
		
		// analyze the generated class
		ClassAccess classAccess = ClassAccess.get(recordViewClass);
		FieldAccess fieldAccess = FieldAccess.get(classAccess);
				
		// inform the recordView about the adapter and its buffer 
		fieldAccess.set(null, "memoryAccess", memoryAccess);
		
		// instantiate a record object
		RecordView recordView = recordViewClass.newInstance();
		recordView.setRecordId(memoryAccess.reserve(10));
		return (B)recordView;
	}
	
	@Before
	public void setup() {
		RecordClassGenerator.classLoader = new DefiningClassLoader();
		memoryAccess = UnsafeMemoryAdapter.getInstance();
	}
	
	@After
	public void clean() {
		memoryAccess.releaseAll();
	}
	
	@Test
	public void getRecordIdTest() throws InstantiationException, IllegalAccessException {		
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "recordId", ActionType.GetRecordId)
		);
		long recordId = record.recordId();
		assertNotEquals(0, recordId);
	}

	@Test
	public void setRecordIdTest() throws InstantiationException, IllegalAccessException {		
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "recordId", ActionType.SetRecordId)
		);
		record.recordId(5);
		assertEquals(5, ((RecordView)record).getRecordId());
	}
		
	@Test
	public void recordSizeTest() throws InstantiationException, IllegalAccessException {		
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "recordSize", ActionType.GetRecordSize)
		);
		int size = record.recordSize();
		assertEquals(0, size);
	}
	
	@Test
	public void blueprintIdTest() throws InstantiationException, IllegalAccessException {		
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "blueprintId", ActionType.GetBlueprintId)
		);
		int blueprintId = record.blueprintId();
		assertEquals(-1, blueprintId);
	}
	
	@Test
	@Ignore
	public void viewTest() throws InstantiationException, IllegalAccessException {		
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "view", ActionType.View)
		);
		TestBlueprint otherView = record.view();
		assertNotEquals(record, otherView);
	}
	
	@Test
	@Ignore
	public void viewAtTest() throws InstantiationException, IllegalAccessException {		
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "viewAt", ActionType.ViewAt)
		);
		assertNotEquals(record, null);
	}
	
	@Test
	@Ignore
	public void copyTest() throws InstantiationException, IllegalAccessException {		
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "copy", ActionType.Copy)
		);
		TestBlueprint otherView = record.copy();
		assertNotEquals(record, otherView);
	}
	
	@Test
	@Ignore
	public void copyFromTest() throws InstantiationException, IllegalAccessException {		
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "copyFrom", ActionType.CopyFrom)
		);
		TestBlueprint otherView = record; // TODO new record
		record.copyFrom(record);
		assertNotEquals(record, otherView);
	}
	
	@Test
	public void getValueTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "getNumber", ActionType.GetValue, var)
		);
		int num = record.getNumber();
		assertEquals(0, num);
	}
	
	@Test
	public void getValueAtTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "getNumberAt", ActionType.GetValueAt, var)
		);
		int num = record.getNumberAt(2);
		assertEquals(0, num);
	}
	
	@Test
	public void setValueTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "getNumber", ActionType.GetValue, var),
				new BlueprintMethod(TestBlueprint.class, "setNumber", ActionType.SetValue, var)
		);
		record.setNumber(5);
		int num = record.getNumber();
		assertEquals(5, num);
	}
	
	@Test
	public void setValueAtTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "getNumberAt", ActionType.GetValueAt, var),
				new BlueprintMethod(TestBlueprint.class, "setNumberAt", ActionType.SetValueAt, var)
		);
		record.setNumberAt(2, 7);
		int num = record.getNumberAt(2);
		assertEquals(7, num);
	}
	
	@Test
	public void getValueSizeTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		var.setElementCount(10);
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "getNumberSize", ActionType.GetArraySize, var)
		);
		int num = record.getNumberSize();
		assertEquals(10, num);
	}
	
	@Test
	public void increaseValueTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		var.setElementCount(10);
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "getNumber", ActionType.GetValue, var),
				new BlueprintMethod(TestBlueprint.class, "increaseNumber", ActionType.IncreaseValue, var)
		);
		record.increaseNumber();
		int num = record.getNumber();
		assertEquals(1, num);
	}
	
	@Test
	public void increaseValueByTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		var.setElementCount(10);
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "getNumber", ActionType.GetValue, var),
				new BlueprintMethod(TestBlueprint.class, "increaseNumberBy", ActionType.IncreaseValueBy, var)
		);
		record.increaseNumberBy(3);
		int num = record.getNumber();
		assertEquals(3, num);
	}
	
	@Test
	public void decreaseValueTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		var.setElementCount(10);
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "getNumber", ActionType.GetValue, var),
				new BlueprintMethod(TestBlueprint.class, "decreaseNumber", ActionType.DecreaseValue, var)
		);
		record.decreaseNumber();
		int num = record.getNumber();
		assertEquals(-1, num);
	}
	
	@Test
	public void decreaseValueByTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		var.setElementCount(10);
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "getNumber", ActionType.GetValue, var),
				new BlueprintMethod(TestBlueprint.class, "decreaseNumberBy", ActionType.DecreaseValueBy, var)
		);
		record.decreaseNumberBy(3);
		int num = record.getNumber();
		assertEquals(-3, num);
	}
	
	@Test
	public void getValueWithTest() throws InstantiationException, IllegalAccessException {	
		
		// nested record
		BlueprintVariable simpleValueVar = BlueprintVariable.of(SimpleValue.class, "value", int.class);
		SimpleValue simpleValueRecord = createRecordView(SimpleValue.class,
				new BlueprintMethod(SimpleValue.class, "getValue", ActionType.GetValue, simpleValueVar),
				new BlueprintMethod(SimpleValue.class, "setValue", ActionType.SetValue, simpleValueVar)
		);
		simpleValueRecord.setValue(6);
		assertEquals(6, simpleValueRecord.getValue());
		
		// class including SimpleValue
		BlueprintVariable var = BlueprintVariable.of(blueprint, "SimpleValue", SimpleValue.class, 4);
		var.setElementCount(10);
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "getSimpleValue", ActionType.GetValueWith, var)
		);
		record.getSimpleValue(simpleValueRecord);
		assertEquals(0, simpleValueRecord.getValue());
	}
	
	@Test
	public void getValueWithAtTest() throws InstantiationException, IllegalAccessException {	
		
		// nested record
		BlueprintVariable simpleValueVar = BlueprintVariable.of(SimpleValue.class, "value", int.class);
		SimpleValue simpleValueRecord = createRecordView(SimpleValue.class,
				new BlueprintMethod(SimpleValue.class, "getValue", ActionType.GetValue, simpleValueVar),
				new BlueprintMethod(SimpleValue.class, "setValue", ActionType.SetValue, simpleValueVar)
		);
		simpleValueRecord.setValue(6);
		assertEquals(6, simpleValueRecord.getValue());
		
		// class including SimpleValue
		BlueprintVariable var = BlueprintVariable.of(blueprint, "SimpleValue", SimpleValue.class, 4);
		var.setElementCount(10);
		TestBlueprint record = createRecordView(blueprint,
				new BlueprintMethod(TestBlueprint.class, "getSimpleValueAt", ActionType.GetValueWithAt, var)
		);
		record.getSimpleValueAt(2, simpleValueRecord);
		simpleValueRecord.setValue(7);
		assertEquals(7, simpleValueRecord.getValue());
		record.getSimpleValueAt(3, simpleValueRecord);
		assertEquals(0, simpleValueRecord.getValue());
	}
}
