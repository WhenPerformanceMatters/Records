package net.wpm.record.blueprint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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
			sum += var.getSizeInBytes();
		return sum;
	}
	
	/**
	 * Adjust the offset of the variables.
	 */
	public void adjustVariableOffset() {
		
		// compare variables by their size of bytes
		Comparator<BlueprintVariable> cmp = new Comparator<BlueprintVariable>() {			
			@Override
			public int compare(BlueprintVariable o1, BlueprintVariable o2) {
				int cmp = Integer.compare(o1.getSizeInBytes(), o2.getSizeInBytes());
				if(cmp == 0)
					cmp = Integer.compare(o1.hashCode(), o2.hashCode());
				return cmp;
			}
		};
		
		// sort the variables by their size of bytes
		List<BlueprintVariable> vars = new ArrayList<>(variables.values());
		vars.sort(cmp);
		
		// change the offset of the variables
		int offset = 0;
		for (BlueprintVariable var : vars) {			
			var.setOffset(offset);
			offset += var.getSizeInBytes();
		}
	}
}