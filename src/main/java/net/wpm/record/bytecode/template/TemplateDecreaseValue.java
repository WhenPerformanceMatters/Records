package net.wpm.record.bytecode.template;

import static net.wpm.codegen.Expressions.sub;
import static net.wpm.codegen.Expressions.value;

import java.util.Collections;

import net.wpm.codegen.ClassBuilder;
import net.wpm.codegen.Expression;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;

public class TemplateDecreaseValue extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateDecreaseValue(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(ClassBuilder<?> builder) {	
		BlueprintVariable variable = blueprintMethod.getVariable();
		
		Expression value = readValueExpression(variable);
		value = sub(value, value(1));
		Expression store = writePrimitiveExpression(variable, value(0), value);
		builder.method(blueprintMethod.getName(), Void.TYPE, Collections.EMPTY_LIST, store);
	}	
}
