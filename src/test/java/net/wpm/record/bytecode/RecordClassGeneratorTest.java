package net.wpm.record.bytecode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import net.wpm.codegen.utils.DefiningClassLoader;
import net.wpm.record.RecordView;
import net.wpm.record.annotation.Array;
import net.wpm.record.blueprint.BlueprintClass;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintMethod.ActionType;
import net.wpm.record.blueprint.BlueprintVariable;
import net.wpm.record.bytes.UnsafeMemoryAdapter;
import net.wpm.reflectasm.ClassAccess;
import net.wpm.reflectasm.FieldAccess;

/**
 * Component Test.
 * Testing internal behaviors.
 * 
 * @author Nico Hezel
 */
public class RecordClassGeneratorTest {

	/**
	 * Test blueprints
	 */
	public interface TestClass {
				
		@Array(size=10)
		public int getNumberSize();
		public int getNumber();
		public int getNumberAt(int index);
		public void setNumber(int value);
		public void setNumberAt(int index, int value);
		public void increaseNumber();
		public void increaseNumberBy(int add);
		public void decreaseNumber();
		public void decreaseNumberBy(int sub);
		
		@Array(size=10)
		public SimpleValue getSimpleValue(SimpleValue reuse);	
		public SimpleValue getSimpleValueAt(int index, SimpleValue reuse);	
		
		public boolean getBoolean();
		public byte getByte();
		public short getShort();
		public int getInt();
		public long getLong();
		public float getFloat();
		public double getDouble();
		
		public Boolean getBooleanBoxed();
		public Byte getByteBoxed();
		public Short getShortBoxed();
		public Integer getIntBoxed();
		public Long getLongBoxed();
		public Float getFloatBoxed();
		public Double getDoubleBoxed();
		
		// optional Record methods 
		public int blueprintId();
		public TestClass view();		
		public long recordId();
		public void recordId(long recordId);
		public int recordSize();
		public TestClass copy();
		public void copyFrom(TestClass to);		
		public default String string() { return ""; }
	}

	public interface SimpleValue {
		public int getValue();
		public void setValue(int val);
	}
	
	protected static Class<? extends TestClass> blueprint = TestClass.class;
	
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
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "recordId", ActionType.GetRecordId)
		);
		long recordId = record.recordId();
		assertNotEquals(0, recordId);
	}

	@Test
	public void setRecordIdTest() throws InstantiationException, IllegalAccessException {		
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "recordId", ActionType.SetRecordId)
		);
		record.recordId(5);
		assertEquals(5, ((RecordView)record).getRecordId());
	}
		
	@Test
	public void recordSizeTest() throws InstantiationException, IllegalAccessException {		
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "recordSize", ActionType.GetRecordSize)
		);
		int size = record.recordSize();
		assertEquals(0, size);
	}
	
	@Test
	public void blueprintIdTest() throws InstantiationException, IllegalAccessException {		
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "blueprintId", ActionType.GetBlueprintId)
		);
		int blueprintId = record.blueprintId();
		assertEquals(-1, blueprintId);
	}
	
	@Test
	@Ignore
	public void viewTest() throws InstantiationException, IllegalAccessException {		
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "view", ActionType.View)
		);
		TestClass otherView = record.view();
		assertNotEquals(record, otherView);
	}
	
	@Test
	@Ignore
	public void copyTest() throws InstantiationException, IllegalAccessException {		
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "copy", ActionType.Copy)
		);
		TestClass otherView = record.copy();
		assertNotEquals(record, otherView);
	}
	
	@Test
	@Ignore
	public void copyFromTest() throws InstantiationException, IllegalAccessException {		
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "copyFrom", ActionType.CopyFrom)
		);
		TestClass otherView = record; // TODO new record
		record.copyFrom(record);
		assertNotEquals(record, otherView);
	}
	
	@Test
	public void getValueTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "getNumber", ActionType.GetValue, var)
		);
		int num = record.getNumber();
		assertEquals(0, num);
	}
	
	@Test
	public void getValueAtTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "getNumberAt", ActionType.GetValueAt, var)
		);
		int num = record.getNumberAt(2);
		assertEquals(0, num);
	}
	
	@Test
	public void setValueTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "getNumber", ActionType.GetValue, var),
				new BlueprintMethod(TestClass.class, "setNumber", ActionType.SetValue, var)
		);
		record.setNumber(5);
		int num = record.getNumber();
		assertEquals(5, num);
	}
	
	@Test
	public void setValueAtTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "getNumberAt", ActionType.GetValueAt, var),
				new BlueprintMethod(TestClass.class, "setNumberAt", ActionType.SetValueAt, var)
		);
		record.setNumberAt(2, 7);
		int num = record.getNumberAt(2);
		assertEquals(7, num);
	}
	
	@Test
	public void getValueSizeTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		var.setElementCount(10);
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "getNumberSize", ActionType.GetArraySize, var)
		);
		int num = record.getNumberSize();
		assertEquals(10, num);
	}
	
	@Test
	public void increaseValueTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		var.setElementCount(10);
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "getNumber", ActionType.GetValue, var),
				new BlueprintMethod(TestClass.class, "increaseNumber", ActionType.IncreaseValue, var)
		);
		record.increaseNumber();
		int num = record.getNumber();
		assertEquals(1, num);
	}
	
	@Test
	public void increaseValueByTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		var.setElementCount(10);
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "getNumber", ActionType.GetValue, var),
				new BlueprintMethod(TestClass.class, "increaseNumberBy", ActionType.IncreaseValueBy, var)
		);
		record.increaseNumberBy(3);
		int num = record.getNumber();
		assertEquals(3, num);
	}
	
	@Test
	public void decreaseValueTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		var.setElementCount(10);
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "getNumber", ActionType.GetValue, var),
				new BlueprintMethod(TestClass.class, "decreaseNumber", ActionType.DecreaseValue, var)
		);
		record.decreaseNumber();
		int num = record.getNumber();
		assertEquals(-1, num);
	}
	
	@Test
	public void decreaseValueByTest() throws InstantiationException, IllegalAccessException {		
		BlueprintVariable var = BlueprintVariable.of(blueprint, "number", int.class);
		var.setElementCount(10);
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "getNumber", ActionType.GetValue, var),
				new BlueprintMethod(TestClass.class, "decreaseNumberBy", ActionType.DecreaseValueBy, var)
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
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "getSimpleValue", ActionType.GetValueWith, var)
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
		TestClass record = createRecordView(blueprint,
				new BlueprintMethod(TestClass.class, "getSimpleValueAt", ActionType.GetValueWithAt, var)
		);
		record.getSimpleValueAt(2, simpleValueRecord);
		simpleValueRecord.setValue(7);
		assertEquals(7, simpleValueRecord.getValue());
		record.getSimpleValueAt(3, simpleValueRecord);
		assertEquals(0, simpleValueRecord.getValue());
	}
}
