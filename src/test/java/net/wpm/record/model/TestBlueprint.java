package net.wpm.record.model;

import net.wpm.record.annotation.Array;

/**
 * Blueprint for components and function tests
 * 
 * @author Nico Hezel
 */
public interface TestBlueprint {
	
	public static enum PlantEnum { Tree, Flower, Meadow }
	
	@Array(size=3)
	public int getPlantEnumSize();
	public PlantEnum getPlantEnum();
	public PlantEnum getPlantEnumAt(int index);
	public void setPlantEnum(PlantEnum plant);	
	public void setPlantEnumAt(int index, PlantEnum plant);
	
	@Array(size=10)
	public int getSimpleValueSize();
	public Iterable<SimpleValue> getSimpleValue();
	public SimpleValue getSimpleValueAt(int index);
	public SimpleValue getSimpleValue(SimpleValue reuse);
	public SimpleValue getSimpleValueAt(int index, SimpleValue reuse);		
	public void setSimpleValue(SimpleValue value);
	public void setSimpleValueAt(int index, SimpleValue value);
	
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
	public TestBlueprint view();		
	public void viewAt(TestBlueprint at);
	public long recordId();
	public void recordId(long recordId);
	public int recordSize();
	public TestBlueprint copy();
	public void copyFrom(TestBlueprint to);		
	public default String string() { return ""; }
	
	
	/**
	 * Nested blueprint
	 * 
	 * @author Nico Hezel
	 */
	public static interface SimpleValue {
		public int getValue();
		public void setValue(int val);
	}
}

