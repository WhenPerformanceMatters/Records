package net.wpm.record.bytecode.template;

import static java.util.Arrays.asList;
import static net.wpm.codegen.Expressions.arg;
import static net.wpm.codegen.Expressions.sub;
import static net.wpm.codegen.Expressions.value;

import net.wpm.codegen.AsmBuilder;
import net.wpm.codegen.Expression;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;

public class TemplateDecreaseValueBy extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateDecreaseValueBy(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@Override
	public void addBytecode(AsmBuilder<?> builder) {		
		BlueprintVariable variable = blueprintMethod.getVariable();
		
		Expression value = readValueExpression(variable);
		value = sub(value, arg(0));
		Expression store = writePrimitiveExpression(variable, value(0), value);
		builder.method(blueprintMethod.getName(), Void.TYPE, asList(variable.getExternalType()), store);
	}	
}
