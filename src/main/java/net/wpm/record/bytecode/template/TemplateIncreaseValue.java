package net.wpm.record.bytecode.template;

import static net.wpm.codegen.Expressions.add;
import static net.wpm.codegen.Expressions.value;

import java.util.Collections;

import net.wpm.codegen.AsmBuilder;
import net.wpm.codegen.Expression;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;

public class TemplateIncreaseValue extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateIncreaseValue(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(AsmBuilder<?> builder) {		
		BlueprintVariable variable = blueprintMethod.getVariable();		
		
		Expression value = readValueExpression(variable);
		value = add(value, value(1));
		Expression store = writePrimitiveExpression(variable, value(0), value);
		builder.method(blueprintMethod.getName(), Void.TYPE, Collections.EMPTY_LIST, store);
	}	
}
