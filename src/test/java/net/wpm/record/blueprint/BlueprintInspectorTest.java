package net.wpm.record.blueprint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import net.wpm.record.Records;
import net.wpm.record.blueprint.BlueprintMethod.ActionType;
import net.wpm.record.model.TestBlueprint;
import net.wpm.record.model.TestBlueprint.SimpleValue;

/**
 * Component Test.
 * Testing internal behaviors.
 * 
 * @author Nico Hezel
 */
public class BlueprintInspectorTest {
	
	@BeforeClass 
	public static void setup() {
		Records.register(SimpleValue.class);
	}
	
	protected BlueprintClass inspect(Class<?> blueprint) {
		BlueprintInspector inspector = new BlueprintInspector(blueprint);
		return inspector.getBlueprintClass();
	}
	
	@Test
	public void variableByNameTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		BlueprintVariable var = blueprintClass.getVariable("SimpleValue");
		assertEquals("SimpleValue", var.getName());
	}
	
	@Test
	public void variablesTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		Collection<BlueprintVariable> vars = blueprintClass.getVariables();	
		Set<String> varNameSet = vars.stream().map(var -> var.getName()).collect(Collectors.toSet());
		
		String[] names = new String[] { "SimpleValue" };
		for (String name : names)			
			assertEquals("Could not find variable " + name, true, varNameSet.contains(name));
	}
	
	@Test
	public void blueprintTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		Class<?> blueprint = blueprintClass.getBlueprint();		
		assertEquals(TestBlueprint.class, blueprint);
	}
	
	@Test
	public void sizeInBytesTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		int size = blueprintClass.getSizeInBytes();					
		assertEquals(96+40, size);
	}
	
	@Test
	public void methodBySignatureTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		BlueprintMethod method = blueprintClass.getMethod("setSimpleValue("+SimpleValue.class.getName()+")");		
		assertEquals("setSimpleValue", method.getName());
	}
	
	@Test
	public void customToStringTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		assertTrue(blueprintClass.isCustomToString());
	}
	
	@Test
	public void methodsTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		Collection<BlueprintMethod> methods = blueprintClass.getMethods();
		Set<String> methodNameSet = methods.stream().map(method -> method.getSignature()).collect(Collectors.toSet());
		
		String[] names = new String[] { 
			"getSimpleValueSize()", "getSimpleValue()", "getSimpleValueAt(int)", "getSimpleValue(" + SimpleValue.class.getName() + ")", 
			"getSimpleValueAt(int, " + SimpleValue.class.getName() + ")", "setSimpleValue(" + SimpleValue.class.getName() + ")", 
			"setSimpleValueAt(int, " + SimpleValue.class.getName() + ")", "getNumber()", "increaseNumber()", "increaseNumberBy(int)", 
			"decreaseNumber()", "decreaseNumberBy(int)", "getBoolean()", "getByte()", "getShort()", "getInt()", "getLong()", 
			"getFloat()", "getDouble()", "getBooleanBoxed()", "getByteBoxed()", "getShortBoxed()", "getIntBoxed()", "getLongBoxed()", 
			"getFloatBoxed()", "getDoubleBoxed()", "blueprintId()", "view()", "recordId()", "recordId(long)", "recordSize()", 
			"copy()", "copyFrom(" + TestBlueprint.class.getName() + ")", 
		};
		for (String name : names)			
			assertTrue("Could not find method " + name, methodNameSet.contains(name));
	}
	
	@Test
	public void sharedVariableTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		BlueprintMethod setMethod = blueprintClass.getMethod("setSimpleValue("+SimpleValue.class.getName()+")");	
		BlueprintMethod getMethod = blueprintClass.getMethod("getSimpleValue()");
		assertEquals(setMethod.getVariable(), getMethod.getVariable());
	}
	
	@Test
	public void isArrayTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		BlueprintVariable nonArray = blueprintClass.getVariable("Int");		
		assertEquals(false, nonArray.isArray());
		
		BlueprintVariable isArray = blueprintClass.getVariable("SimpleValue");
		assertEquals(true, isArray.isArray());
	}
	
	@Test
	public void checkOffsetsTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		Collection<BlueprintVariable> vars = blueprintClass.getVariables();	

		// sort by offset
		Comparator<BlueprintVariable> cmp = (BlueprintVariable o1, BlueprintVariable o2) -> {
			return Integer.compare(o1.getOffset(), o2.getOffset());
		};
		
		// sort the variables
		BlueprintVariable[] sortedVars = vars.stream().sorted(cmp).toArray(BlueprintVariable[]::new);

		// check the offsets
		int expected = 0;
		for (BlueprintVariable var : sortedVars) {
			assertEquals("Variable " + var.getName() + " should have an offset of " + expected, expected, var.getOffset());			
			expected += var.getSizeInBytes();
		}
	}
	
	@Test
	public void variableInternalTypeTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		
		// primitive variables
		{
			BlueprintVariable var = blueprintClass.getVariable("Boolean");		
			assertEquals(boolean.class, var.getInternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("Byte");		
			assertEquals(byte.class, var.getInternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("Short");		
			assertEquals(short.class, var.getInternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("Int");		
			assertEquals(int.class, var.getInternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("Long");		
			assertEquals(long.class, var.getInternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("Float");		
			assertEquals(float.class, var.getInternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("Double");		
			assertEquals(double.class, var.getInternalType());
		}	
		
		
		// boxed variables
		{
			BlueprintVariable var = blueprintClass.getVariable("BooleanBoxed");		
			assertEquals(boolean.class, var.getInternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("ByteBoxed");		
			assertEquals(byte.class, var.getInternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("ShortBoxed");		
			assertEquals(short.class, var.getInternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("IntBoxed");		
			assertEquals(int.class, var.getInternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("LongBoxed");		
			assertEquals(long.class, var.getInternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("FloatBoxed");		
			assertEquals(float.class, var.getInternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("DoubleBoxed");		
			assertEquals(double.class, var.getInternalType());
		}
	}
		
	@Test
	public void variableExternalTypeTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		
		// primitive variables
		{
			BlueprintVariable var = blueprintClass.getVariable("Boolean");		
			assertEquals(boolean.class, var.getExternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("Byte");		
			assertEquals(byte.class, var.getExternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("Short");		
			assertEquals(short.class, var.getExternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("Int");		
			assertEquals(int.class, var.getExternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("Long");		
			assertEquals(long.class, var.getExternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("Float");		
			assertEquals(float.class, var.getExternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("Double");		
			assertEquals(double.class, var.getExternalType());
		}	
		
		
		// boxed variables
		{
			BlueprintVariable var = blueprintClass.getVariable("BooleanBoxed");		
			assertEquals(Boolean.class, var.getExternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("ByteBoxed");		
			assertEquals(Byte.class, var.getExternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("ShortBoxed");		
			assertEquals(Short.class, var.getExternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("IntBoxed");		
			assertEquals(Integer.class, var.getExternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("LongBoxed");		
			assertEquals(Long.class, var.getExternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("FloatBoxed");		
			assertEquals(Float.class, var.getExternalType());
		}
		{
			BlueprintVariable var = blueprintClass.getVariable("DoubleBoxed");		
			assertEquals(Double.class, var.getExternalType());
		}
	}
	
	@Test
	public void actionTypeTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		
		{
			BlueprintMethod method = blueprintClass.getMethod("getSimpleValue()");	
			assertEquals(ActionType.GetValue, method.getActionType());
		}		
		{
			BlueprintMethod method = blueprintClass.getMethod("getSimpleValue("+SimpleValue.class.getName()+")");	
			assertEquals(ActionType.GetValueWith, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("getSimpleValueAt(int)");	
			assertEquals(ActionType.GetValueAt, method.getActionType());
		}	
		{
			BlueprintMethod method = blueprintClass.getMethod("getSimpleValueAt(int, "+SimpleValue.class.getName()+")");	
			assertEquals(ActionType.GetValueWithAt, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("setSimpleValue("+SimpleValue.class.getName()+")");	
			assertEquals(ActionType.SetValue, method.getActionType());
		}			
		{
			BlueprintMethod method = blueprintClass.getMethod("setSimpleValueAt(int, "+SimpleValue.class.getName()+")");	
			assertEquals(ActionType.SetValueAt, method.getActionType());
		}	
		{
			BlueprintMethod method = blueprintClass.getMethod("getSimpleValueSize()");	
			assertEquals(ActionType.GetArraySize, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("increaseNumber()");	
			assertEquals(ActionType.IncreaseValue, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("increaseNumberBy(int)");	
			assertEquals(ActionType.IncreaseValueBy, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("decreaseNumber()");	
			assertEquals(ActionType.DecreaseValue, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("decreaseNumberBy(int)");	
			assertEquals(ActionType.DecreaseValueBy, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("blueprintId()");	
			assertEquals(ActionType.GetBlueprintId, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("view()");	
			assertEquals(ActionType.View, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("recordId()");	
			assertEquals(ActionType.GetRecordId, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("recordId(long)");	
			assertEquals(ActionType.SetRecordId, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("recordSize()");	
			assertEquals(ActionType.GetRecordSize, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("copy()");	
			assertEquals(ActionType.Copy, method.getActionType());
		}
		{
			BlueprintMethod method = blueprintClass.getMethod("copyFrom(" + TestBlueprint.class.getName() + ")");	
			assertEquals(ActionType.CopyFrom, method.getActionType());
		}
	}
	
	// -------------------------------------------------------------------------------------------------------
	// ---------------------------------------- Method Annotation Tests --------------------------------------
	// -------------------------------------------------------------------------------------------------------
	
	@Test
	public void annotationArraySizeTest() {
		BlueprintClass blueprintClass = inspect(TestBlueprint.class);
		BlueprintVariable simpleValueVar = blueprintClass.getVariable("SimpleValue");		
		assertEquals(10, simpleValueVar.getElementCount());
		
		BlueprintVariable numberVar = blueprintClass.getVariable("Int");
		assertEquals(1, numberVar.getElementCount());
	}
	
}
