package net.wpm.record.bytecode.template;

import static net.wpm.codegen.Expressions.value;

import java.util.Collections;

import net.wpm.codegen.ClassBuilder;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;

public class TemplateGetArraySize extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateGetArraySize(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(ClassBuilder<?> builder) {		
		BlueprintVariable variable = blueprintMethod.getVariable();
		builder.method(blueprintMethod.getName(), int.class, Collections.EMPTY_LIST, value(variable.getElementCount()));			
	}	
}
