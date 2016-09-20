package net.wpm.record.blueprint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import net.wpm.record.RecordView;
import net.wpm.record.annotation.Array;
import net.wpm.record.exception.InvalidBlueprintException;
import net.wpm.reflectasm.ClassAccess;
import net.wpm.reflectasm.ConstructorAccess;
import net.wpm.reflectasm.FieldAccess;
import net.wpm.reflectasm.MethodAccess;

/**
 * The BlueprintInspector analyzes the blueprint for declared getter and setter methods. 
 * Possible prefixes for those methods are "get", "set". The Methods with the same root 
 * word access operate on the same underlying variable. A variable is only a piece of
 * memory and a data type. The content of all variables are stored in a common byte 
 * buffer. A offset decides which part of the buffer is occupied by which variable.
 * 
 * 
 * Inspired by Richard Warburton SLAB TypeInspector.
 * 
 * @author Nico
 *
 * @param <B>
 */
public class BlueprintInspector {
	
	// a set of all method names used in the record class
	protected static final Set<String> occupiedMethods = new HashSet<String>();

	static {		
		for(String methodName : ClassAccess.get(RecordView.class).getMethodNames()) {
			occupiedMethods.add(methodName);
		}
	}
	
	
	
	protected final BlueprintClass blueprintClass;
	
	// access to different class informations of the blueprint
	protected final ClassAccess classAccess;
	protected final FieldAccess fieldAccess;
	protected final MethodAccess methodAccess;	
	protected final ConstructorAccess<?> constructorAccess;	
	
	public BlueprintInspector(final Class<?> blueprint) {
		
        if(!blueprint.isInterface())
        	throw new InvalidBlueprintException(blueprint.getName()+" must be an interface in order to be used as a record.");
		
		this.classAccess = ClassAccess.get(blueprint);
		this.fieldAccess = FieldAccess.get(classAccess);
		this.methodAccess = MethodAccess.get(classAccess);
		this.constructorAccess = ConstructorAccess.get(classAccess);
		
		// all blueprint specific informations
		this.blueprintClass = analyseBlueprint(blueprint);
	}
	
	public BlueprintClass getBlueprintClass() {
		return blueprintClass;
	}
	
	/**
	 * Analyse the entire blueprint
	 * 
	 * @param blueprint
	 * @return
	 */
	protected BlueprintClass analyseBlueprint(final Class<?> blueprint) {
		BlueprintClass blueprintClass = new BlueprintClass(blueprint);
		
		analyseBlueprintClass(blueprintClass);
		analyseBlueprintVariables(blueprintClass);
		analyseBlueprintMethods(blueprintClass);
		
		blueprintClass.adjustVariableOffset();
		return blueprintClass;
	}

	/**
	 * Analyse the annotations of the blueprint class
	 * @param blueprintClass
	 */
	protected void analyseBlueprintClass(final BlueprintClass blueprintClass) {
		// TODO check annotations of class
		
		// only protected and public classes are allowed
		if(Modifier.isPrivate(classAccess.getModifiers()))
			throw new InvalidBlueprintException("Cannot implement private "+blueprintClass.getBlueprint()+".");

	}
	
	/**
	 * Analyse the fields of the blueprint
	 * @param blueprintClass
	 */
	protected void analyseBlueprintVariables(final BlueprintClass blueprintClass) {
		// only static fields are allowed	
		for (int i = 0; i < fieldAccess.getFieldCount(); i++) 
			if(Modifier.isStatic(fieldAccess.getFieldModifiers()[i]) == false)
				throw new InvalidBlueprintException("Only static fields are allowed in blueprint "+blueprintClass.getBlueprint());

		
	}
	
	/**
	 * Scan all methods in the blueprint and extract the methods that needs to be implemented by the record class
	 * 
	 * @param blueprintClass
	 */
	protected void analyseBlueprintMethods(final BlueprintClass blueprintClass) {
		
		// find all declared methods in the blueprint
		for (int methodIndex = 0; methodIndex < methodAccess.size(); methodIndex++) {
			String methodName = getMethodName(methodIndex);
			
			if(methodName.startsWith("get") && methodName.endsWith("At"))
				analyseGetAtMethod(methodIndex, blueprintClass);
			else if(methodName.startsWith("get") && methodName.endsWith("Size"))
				analyseGetSizeMethod(methodIndex, blueprintClass);
			else if(methodName.startsWith("get"))
				analyseGetterMethod(methodIndex, blueprintClass);
			else if(methodName.startsWith("set") && methodName.endsWith("At"))
				analyseSetAtMethod(methodIndex, blueprintClass);
			else if(methodName.startsWith("set"))
				analyseSetterMethod(methodIndex, blueprintClass);
			else if(methodName.startsWith("increase")&& methodName.endsWith("By"))
				analyseIncreaseByMethod(methodIndex, blueprintClass);
			else if(methodName.startsWith("increase"))
				analyseIncreaseMethod(methodIndex, blueprintClass);
			else if(methodName.startsWith("decrease")&& methodName.endsWith("By"))
				analyseDecreaseByMethod(methodIndex, blueprintClass);
			else if(methodName.startsWith("decrease"))
				analyseDecreaseMethod(methodIndex, blueprintClass);
			
			else if(methodName.equalsIgnoreCase("view"))
				analyseViewMethod(methodIndex, blueprintClass);
			else if(methodName.equalsIgnoreCase("copy"))
				analyseCopyMethod(methodIndex, blueprintClass);
			else if(methodName.equalsIgnoreCase("copyFrom"))
				analyseCopyFromMethod(methodIndex, blueprintClass);
			else if(methodName.equalsIgnoreCase("blueprintId"))
				analyseBlueprintIdMethod(methodIndex, blueprintClass);
			else if(methodName.equalsIgnoreCase("recordId"))
				analyseRecordIdMethod(methodIndex, blueprintClass);
			else if(methodName.equalsIgnoreCase("string"))
				analyseToStringMethod(methodIndex, blueprintClass);
			else if(methodName.equalsIgnoreCase("recordSize"))
				analyseSizeMethod(methodIndex, blueprintClass);
		}		
	}
	
	/**
	 * Check if this is a valid view method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseViewMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			hasNoParameter(methodIndex, methodName);		
			returns(methodIndex, methodName, blueprintClass.getBlueprint());
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a view method.", e);
		}
		
		// defines a valid recordId method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, BlueprintMethod.ActionType.View));
	}
	
	/**
	 * Check if this is a valid record size method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 */
	protected void analyseSizeMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		
		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			hasNoParameter(methodIndex, methodName);
			returns(methodIndex, methodName, int.class);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a record size method.", e);
		}

		// defines a valid blueprintId method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, BlueprintMethod.ActionType.GetRecordSize));
	}
	
	/**
	 * Check if this is a valid copy from method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseCopyFromMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);

		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			hasParameters(methodIndex, methodName, blueprintClass.getBlueprint());		
			returns(methodIndex, methodName, Void.TYPE);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a copyFrom method.", e);
		}
		
		// defines a valid recordId method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, BlueprintMethod.ActionType.CopyFrom));
	}
	
	/**
	 * Check if this is a valid copy method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseCopyMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			hasNoParameter(methodIndex, methodName);		
			returns(methodIndex, methodName, blueprintClass.getBlueprint());
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a copy method.", e);
		}
		
		// defines a valid copy method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, BlueprintMethod.ActionType.Copy));
	}
	
	/**
	 * Check if this is a valid decrease-by method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseDecreaseByMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		
		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			limitedParameterCount(methodIndex, methodName, 1);
			returns(methodIndex, methodName, Void.TYPE);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a decrease-by method.", e);
		}
		
		// add unknown variables to the variable map
		String variableName = methodName.substring(8, methodName.length() - 2);
		BlueprintVariable variable = underlyingVariable(blueprintClass, methodIndex, variableName, getMethodParameterTypes(methodIndex)[0]);

		// defines a valid get-at-index method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, BlueprintMethod.ActionType.DecreaseValueBy, variable));
	}
	
	/**
	 * Check if this is a valid decrease method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseDecreaseMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		
		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			hasNoParameter(methodIndex, methodName);
			returns(methodIndex, methodName, Void.TYPE);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a decrease method.", e);
		}
		
		// add unknown variables to the variable map
		String variableName = methodName.substring(8);
		BlueprintVariable variable = underlyingVariable(blueprintClass, methodIndex, variableName, Object.class);

		// defines a valid get-at-index method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, BlueprintMethod.ActionType.DecreaseValue, variable));
	}
	
	/**
	 * Check if this is a valid increase-by method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseIncreaseByMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		
		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			limitedParameterCount(methodIndex, methodName, 1);
			returns(methodIndex, methodName, Void.TYPE);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a increase-by method.", e);
		}
		
		// add unknown variables to the variable map
		String variableName = methodName.substring(8, methodName.length() - 2);
		BlueprintVariable variable = underlyingVariable(blueprintClass, methodIndex, variableName, getMethodParameterTypes(methodIndex)[0]);

		// defines a valid get-at-index method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, BlueprintMethod.ActionType.IncreaseValueBy, variable));
	}
	
	/**
	 * Check if this is a valid increase method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseIncreaseMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		
		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			hasNoParameter(methodIndex, methodName);
			returns(methodIndex, methodName, Void.TYPE);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a increase method.", e);
		}
		
		// add unknown variables to the variable map
		String variableName = methodName.substring(8);
		BlueprintVariable variable = underlyingVariable(blueprintClass, methodIndex, variableName, Object.class);

		// defines a valid get-at-index method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, BlueprintMethod.ActionType.IncreaseValue, variable));
	}
	
	/**
	 * Check if this is a valid to String method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseToStringMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		
		try {
			notOccupied(methodName);
			ensureImplementation(methodIndex, methodName);
			hasNoParameter(methodIndex, methodName);
			returns(methodIndex, methodName, String.class);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a toString method.", e);
		}
		
		blueprintClass.useCustomToString(true);
	}
	
	/**
	 * Check if this is a valid blueprint Id method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseBlueprintIdMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		
		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			hasNoParameter(methodIndex, methodName);
			returns(methodIndex, methodName, int.class);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a blueprintId method.", e);
		}
		
		// defines a valid blueprintId method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, BlueprintMethod.ActionType.GetBlueprintId));
	}	
	
	/**
	 * Check if this is a valid record Id method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseRecordIdMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		boolean noParams = (getMethodParameterTypes(methodIndex).length == 0);
		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			limitedParameterCount(methodIndex, methodName, 1);			
			returns(methodIndex, methodName, (noParams) ? long.class : Void.TYPE);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a get/set-record-id method.", e);
		}
		
		// defines a valid recordId method contained in the blueprint
		BlueprintMethod.ActionType type = noParams ? BlueprintMethod.ActionType.GetRecordId : BlueprintMethod.ActionType.SetRecordId;
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, type));
	}	
	
	/**
	 * Check if this is a valid Setter-Method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseSetterMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		
		try {			
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			hasParameters(methodIndex, methodName, Object.class);
			returns(methodIndex, methodName, Void.TYPE);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a setter method.", e);
		}

		// add unknown variables to the variable map
		String variableName = methodName.substring(3);
		BlueprintVariable variable = underlyingVariable(blueprintClass, methodIndex, variableName, getMethodParameterTypes(methodIndex)[0]);

		// defines a valid set-method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, BlueprintMethod.ActionType.SetValue, variable));
	}

	/**
	 * Check if this is a valid set-at-index method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseSetAtMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		
		try {		
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
	        returns(methodIndex, methodName, Void.TYPE);
	        hasParameters(methodIndex, methodName, int.class, Object.class);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a set-at-index method.", e);
		}

		// add unknown variables to the variable map
		String variableName = methodName.substring(3, methodName.length() - 2);
		BlueprintVariable variable = underlyingVariable(blueprintClass, methodIndex, variableName, getMethodParameterTypes(methodIndex)[1]);

		// defines a valid get-at-index method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, BlueprintMethod.ActionType.SetValueAt, variable));
	}
	

	/**
	 * Check if this is a valid get-size-of-array method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseGetSizeMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		
		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			hasNoParameter(methodIndex, methodName);
			returns(methodIndex, methodName, int.class);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a get-array-size method.", e);
		}
		
		// add unknown variables to the variable map
		String variableName = methodName.substring(3, methodName.length() - 4);
		BlueprintVariable variable = underlyingVariable(blueprintClass, methodIndex, variableName, Object.class);

		// defines a valid get-at-index method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, BlueprintMethod.ActionType.GetArraySize, variable));
	}
	
	
	/**
	 * Check if this is a valid Getter-Method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseGetterMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		boolean zeroParam = (getMethodParameterTypes(methodIndex).length == 0);

		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			limitedParameterCount(methodIndex, methodName, 1);
			returns(methodIndex, methodName, Object.class);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a getter method.", e);
		}	
		
		// add unknown variables to the variable map
		String variableName = methodName.substring(3);
		BlueprintVariable variable = underlyingVariable(blueprintClass, methodIndex, variableName, getMethodReturnType(methodIndex));
		
		// the type of the variable is another blueprint
		BlueprintMethod.ActionType action = zeroParam ? BlueprintMethod.ActionType.GetValue : BlueprintMethod.ActionType.GetValueWith;
							
		// defines a valid get-method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, action, variable));
	}	
	
	/**
	 * Check if this is a valid get-at-index method
	 * 
	 * @param methodIndex
	 * @param blueprintClass
	 * @return
	 */
	protected void analyseGetAtMethod(int methodIndex, BlueprintClass blueprintClass) {
		String methodName = getMethodName(methodIndex);
		boolean oneParam = (getMethodParameterTypes(methodIndex).length == 1);
		Class<?>[] params = oneParam ? new Class<?>[] {int.class} : new Class<?>[] {int.class, Object.class}; 

		try {
			notOccupied(methodName);
			ensureAbstract(methodIndex, methodName);
			limitedParameterCount(methodIndex, methodName, 2);
			hasParameters(methodIndex, methodName, params);
			returns(methodIndex, methodName, Object.class);
		} catch (Exception e) {
			throw new InvalidBlueprintException("Unable to declare "+methodName+" as a get-at-index method.", e);
		}
		
		// internal variable for this method
		String variableName = methodName.substring(3, methodName.length() - 2);
		BlueprintVariable variable = underlyingVariable(blueprintClass, methodIndex, variableName, getMethodReturnType(methodIndex));
	
		// the type of the variable is another blueprint
		BlueprintMethod.ActionType action = oneParam ? BlueprintMethod.ActionType.GetValueAt : BlueprintMethod.ActionType.GetValueWithAt;
		
		// defines a valid get-at-index method contained in the blueprint
		blueprintClass.addMethod(new BlueprintMethod(blueprintClass.getBlueprint(), methodName, action, variable));
	}	
	
	/**
	 * Every methods uses an internal variable to store and read data from.
	 * 
	 * @param blueprintClass
	 * @param methodIndex
	 * @param variableName
	 * @param type
	 * @return
	 */
	protected BlueprintVariable underlyingVariable(BlueprintClass blueprintClass, int methodIndex, String variableName, Class<?> type) {
		
		// try to get a variable already used somewhere
		BlueprintVariable variable = blueprintClass.getVariable(variableName);
		if(variable == null)
			// create a new one
			variable = blueprintClass.createVariable(variableName, type);
		else {
			
			// deal only with non object types
			if(type != Object.class) {
			
				// if type was unknown before set it now
				if(variable.getExternalType() == Object.class)
					variable.setType(type);
				
				// external type should be always the same as the given type
				else if(variable.getExternalType() != type)
					throw new InvalidBlueprintException("Underlying variable '"+variableName+"' of method " + getMethodName(methodIndex) + " exists already but with type " + variable.getExternalType() + " instead of "+type);
			}
		}
		
		// add array annotation information
		Array arrayAnnotation = getMethodAnnotation(methodIndex, Array.class);
		if(arrayAnnotation != null) {
			if(variable.isArray() && variable.getElementCount() != arrayAnnotation.size())
				throw new InvalidBlueprintException("Size of array annotation is "+arrayAnnotation.size()+" for method "+getMethodName(methodIndex)+" but is defined different elsewhere.");
			variable.setElementCount(arrayAnnotation.size());
		}
		
		// TODO andere Annotationen auslesen
		
		return variable;
	}
	
	
	
	// -----------------------------------------------------------------------------------------------------------
	// ----------------------------------------- signature checking methods --------------------------------------
	// -----------------------------------------------------------------------------------------------------------
	
    private void notOccupied(String methodName) {
		if (occupiedMethods.contains(methodName))
			throw new InvalidBlueprintException("Method name '"+methodName+"' already in use by java Records.");
	}
    
    private void ensureImplementation(int methodIndex, String methodName) {
		if (Modifier.isAbstract(getMethodModifiers(methodIndex)) == true)
			throw new InvalidBlueprintException(methodName + " must be an implemented method");
	}
    
    private void ensureAbstract(int methodIndex, String methodName) {
		if (Modifier.isAbstract(getMethodModifiers(methodIndex)) == false)
			throw new InvalidBlueprintException(methodName + " must be abstract or an interface");
	}
    
    private void limitedParameterCount(int methodIndex, String methodName, int count) {
		if (getMethodParameterTypes(methodIndex).length > count)
			throw new InvalidBlueprintException(methodName + " has to many parameters");
    }
    
    private void hasNoParameter(int methodIndex, String methodName) {
		if (getMethodParameterTypes(methodIndex).length > 0)
			throw new InvalidBlueprintException(methodName + " is not allowed to have parameters");
    }
    
    private void hasParameters(int methodIndex, String methodName, Class<?> ...types) {
		final Class<?>[] parameters = getMethodParameterTypes(methodIndex);
		final int count = types.length;
		
		if (parameters.length < count)
			throw new InvalidBlueprintException(methodName + " has less then "+count+" parameter");
		if (parameters.length > count)
			throw new InvalidBlueprintException(methodName + " has more than "+count+" parameter");
		
		
		// check the type of all parameters
		for (int i = 0; i < count; i++) {
			Class<?> type = types[i];
			
			// skip Object type
			if(types[i] == Object.class) continue;
			
			// check type
			if (parameters[i] != type)
				throw new InvalidBlueprintException(methodName + " has no "+type+" parameter at "+(i+1)+" position.");
		}
    }
       
    private void returns(int methodIndex, String methodName, Class<?> type) {
		if(type == Object.class) {
			if (getMethodReturnType(methodIndex) == Void.TYPE)
				throw new InvalidBlueprintException(methodName + " is not allowed to return void");
		} else {
			if (getMethodReturnType(methodIndex) != type)
				throw new InvalidBlueprintException(methodName + " doesn't return "+type+".");
		}
	}
    
	// -----------------------------------------------------------------------------------------------------------
	// ----------------------------------------- MethodAcces helper methods --------------------------------------
	// -----------------------------------------------------------------------------------------------------------

	protected String getMethodName(int index) {
		return methodAccess.getMethodNames()[index];
	}
	
	protected Annotation[] getMethodAnnotations(int index) {
		return methodAccess.getMethodAnnotations()[index];
	}
	
	@SuppressWarnings("unchecked")
	protected <A> A getMethodAnnotation(int index, Class<A> annotationType) {
		for (Annotation annotation : methodAccess.getMethodAnnotations()[index]) {
			if(annotation.annotationType() == annotationType)
				return (A)annotation;
		}
		return null;
	}
	
	protected int getMethodModifiers(int index) {
		return methodAccess.getMethodModifiers()[index];
	}
	
	protected Class<?>[] getMethodParameterTypes(int index) {
		return methodAccess.getParameterTypes()[index];
	}

	protected Class<?> getMethodReturnType(int index) {
		return methodAccess.getReturnTypes()[index];
	}
}