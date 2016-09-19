package net.wpm.record.bytecode.template;

import java.util.Collections;

import net.wpm.codegen.AsmBuilder;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;

public class TemplateGetValue extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateGetValue(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(AsmBuilder<?> builder) {		
		BlueprintVariable variable = blueprintMethod.getVariable();
		builder.method(blueprintMethod.getName(), variable.getExternalType(), Collections.EMPTY_LIST, readValueExpression(variable));		
	}
	
}
