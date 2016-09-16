package net.wpm.record.blueprint;

import java.util.HashMap;
import java.util.Map;

import net.wpm.record.Records;

/**
 * Underlying variable of a blueprint method. 
 * 
 * @author Nico
 */
public class BlueprintVariable {
	
	/**
	 * Blueprint where this variable is used
	 */
	protected final Class<?> blueprint;
	
	/**
	 * Name of the variable
	 */
	protected final String name;
	
	/**
	 * Size of the data type in bytes e.g. 4
	 */
	protected int sizeInBytes;
	
	/**
	 * External type e.g. Integer specifies how
	 * the variable is presented to the outside.
	 */
	protected Class<?> externalType;
	
	/**
	 * Internal type e.g. int specifies how 
	 * the variable is stored internally. 
	 */
	protected Class<?> internalType;
	
	/**
	 * Offset inside the byte buffer of the record
	 */
	protected int offset;	
		
	/**
	 * Is this an array
	 */
	protected boolean isArray;
	
	/**
	 * How many elements does this array have 
	 */
	protected int length;
	
	private BlueprintVariable(Class<?> blueprint, String name, int sizeInBytes, Class<?> internalType, Class<?> externalType) {
		this.blueprint = blueprint;
		this.name = name;
        this.sizeInBytes = sizeInBytes;
		this.externalType = externalType;
		this.internalType = internalType;
		this.length = 1;
		this.isArray = false;
    }	
	
	public int getOffset() {
		return this.offset;
	}
	
	public String getName() {
		return name;
	}
	
	public int getLength() {
		return length;
	}
	
	public boolean isArray() {
		return isArray;
	}
	
	public int getSizeInBytes() {
		return sizeInBytes;
	}
	
	public Class<?> getExternalType() {
		return externalType;
	}
	
	public Class<?> getInternalType() {
		return internalType;
	}
	

	public void setType(Class<?> type) {
		BlueprintVariable defaultType = getDefault(type);
		this.sizeInBytes = defaultType.sizeInBytes; 
		this.internalType = defaultType.internalType; 
		this.externalType = type;		
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void setLength(int length) {
		this.length = length;
		if(length > 1)
			this.isArray = true;
	}	
	
	
	@Override
	public String toString() {	
		return externalType.getName() + (sizeInBytes * 8) + " " + getName();
	}
	
	/**
	 * Variable of unknown type and size
	 * 
	 * @param blueprint
	 * @param name
	 * @return
	 */
	public static BlueprintVariable of(Class<?> blueprint, String name) {
		return new BlueprintVariable(blueprint, name, 0, null, null);
	}
	
	/**
	 * Expects an primitive or a wrapper class of it.
	 * 
	 * @param blueprint
	 * @param name
	 * @param type
	 * @return
	 */
	public static BlueprintVariable of(Class<?> blueprint, String name, Class<?> type) {
		BlueprintVariable defaultType = getDefault(type);
		
		// check if another blueprint of this type exists
		if(defaultType == null) {
			int blueprintId = Records.blueprintId(type);
			int sizeInBytes = Records.size(blueprintId);
			defaultType = new BlueprintVariable(blueprint, name, sizeInBytes, type, type);
		}
		
		return new BlueprintVariable(blueprint, name, defaultType.sizeInBytes, defaultType.internalType, type);
	}
		
	/**
	 * Returns the default supported data type or null if not supported
	 * 
	 * @param type
	 * @return
	 */
	protected static BlueprintVariable getDefault(Class<?> type) {
		while(type.isArray())
			type = type.getComponentType();
		return nameToDataType.get(type.getName());
	}
	
	 // creates a map of all supported data types, associated by their name
    protected final static Map<String, BlueprintVariable> nameToDataType = new HashMap<>();
    
    static {    	
    	nameToDataType.put("byte", new BlueprintVariable(BlueprintVariable.class, "byte", 1, Byte.TYPE, Byte.TYPE));
    	nameToDataType.put("java.lang.Byte", new BlueprintVariable(BlueprintVariable.class, "java.lang.Byte", 1, Byte.TYPE, Byte.class));
    	
    	nameToDataType.put("short", new BlueprintVariable(BlueprintVariable.class, "short", 2, Short.TYPE, Short.TYPE));
    	nameToDataType.put("java.lang.Short", new BlueprintVariable(BlueprintVariable.class, "java.lang.Short", 2, Short.TYPE, Short.class));
    	
    	nameToDataType.put("int", new BlueprintVariable(BlueprintVariable.class, "int", 4, Integer.TYPE, Integer.TYPE));
    	nameToDataType.put("java.lang.Integer", new BlueprintVariable(BlueprintVariable.class, "java.lang.Integer", 4, Integer.TYPE, Integer.class));
    	
    	nameToDataType.put("long", new BlueprintVariable(BlueprintVariable.class, "long", 8, Long.TYPE, Long.TYPE));
    	nameToDataType.put("java.lang.Long", new BlueprintVariable(BlueprintVariable.class, "java.lang.Long", 8, Long.TYPE, Long.class));
    	
    	nameToDataType.put("float", new BlueprintVariable(BlueprintVariable.class, "float", 4, Float.TYPE, Float.TYPE));
    	nameToDataType.put("java.lang.Float", new BlueprintVariable(BlueprintVariable.class, "java.lang.Float", 4, Float.TYPE, Float.class));
    	
    	nameToDataType.put("double", new BlueprintVariable(BlueprintVariable.class, "double", 8, Double.TYPE, Double.TYPE));
    	nameToDataType.put("java.lang.Double", new BlueprintVariable(BlueprintVariable.class, "java.lang.Double", 8, Double.TYPE, Double.class));

    	nameToDataType.put("boolean", new BlueprintVariable(BlueprintVariable.class, "boolean", 1, Boolean.TYPE, Boolean.TYPE));
    	nameToDataType.put("java.lang.Boolean", new BlueprintVariable(BlueprintVariable.class, "java.lang.Boolean", 1, Boolean.TYPE, Boolean.class));
    	
    	nameToDataType.put("char", new BlueprintVariable(BlueprintVariable.class, "char", 2, Character.TYPE, Character.TYPE));
    	nameToDataType.put("java.lang.Character", new BlueprintVariable(BlueprintVariable.class, "java.lang.Character", 2, Character.TYPE, Character.class));
    	
    	nameToDataType.put("java.lang.String", new BlueprintVariable(BlueprintVariable.class, "java.lang.String", 2, Character.TYPE, String.class));
    	
    	// to store references to other objects
    	nameToDataType.put("java.lang.Object", new BlueprintVariable(BlueprintVariable.class, "java.lang.Object", 8, Long.TYPE, Object.class));

    }
}