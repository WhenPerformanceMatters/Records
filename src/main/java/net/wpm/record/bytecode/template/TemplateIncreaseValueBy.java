package net.wpm.record.bytecode.template;

import static java.util.Arrays.asList;
import static net.wpm.codegen.Expressions.add;
import static net.wpm.codegen.Expressions.arg;
import static net.wpm.codegen.Expressions.value;

import net.wpm.codegen.ClassBuilder;
import net.wpm.codegen.Expression;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;

public class TemplateIncreaseValueBy extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateIncreaseValueBy(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@Override
	public void addBytecode(ClassBuilder<?> builder) {		
		BlueprintVariable variable = blueprintMethod.getVariable();	
		
		Expression value = readValueExpression(variable);
		value = add(value, arg(0));
		Expression store = writePrimitiveExpression(variable, value(0), value);
		builder.method(blueprintMethod.getName(), Void.TYPE, asList(variable.getExternalType()), store);
	}	
}
