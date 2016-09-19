package net.wpm.record.blueprint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BlueprintClass {

	protected final Class<?> blueprint;
	protected final Map<String, BlueprintVariable> variables;
	protected final Map<String, BlueprintMethod> methods;
	
	// use a custom to string method
	protected boolean customToString = false;
	
	public BlueprintClass(Class<?> blueprint) {
		this.blueprint = blueprint;
		this.variables = new HashMap<String, BlueprintVariable>();
		this.methods = new HashMap<String, BlueprintMethod>();
	}

	public Class<?> getBlueprint() {
		return blueprint;
	}	
	
	public void useCustomToString(boolean value) {
		customToString = value;
	}
	
	public boolean isCustomToString() {
		return customToString;
	}

	
	
	public Collection<BlueprintVariable> getVariables() {
		return variables.values();
	}
	
	public BlueprintVariable getVariable(String name) {
		return variables.get(name);
	}
	
	public void addVariable(BlueprintVariable variable) { 
		variables.put(variable.getName(), variable);
	}
	
	public BlueprintVariable createVariable(String variableName, Class<?> type) {
		BlueprintVariable variable = BlueprintVariable.of(blueprint, variableName, type);
		variables.put(variableName, variable);
		return variable;
	}
	
	public BlueprintMethod getMethod(String methodSignature) {
		return methods.get(methodSignature);
	}
	
	public Collection<BlueprintMethod> getMethods() {
		return methods.values();
	}

	public void addMethod(BlueprintMethod method) {
		methods.put(method.getSignature(), method);
	}


	public int getSizeInBytes() {
		int sum = 0;
		for (BlueprintVariable var : variables.values()) 		
			sum += var.getSizeInBytes() * var.getLength();
		return sum;
	}
	
	/**
	 * Adjust the offset of the variables.
	 * 
	 * @param variables
	 */
	public void adjustVariableOffset() {
		int offset = 0;
		for (BlueprintVariable var : variables.values()) {			
			var.setOffset(offset);
			offset += var.getSizeInBytes() * var.getLength();
		}
	}
}