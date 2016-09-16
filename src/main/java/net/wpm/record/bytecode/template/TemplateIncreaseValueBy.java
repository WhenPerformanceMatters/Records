package net.wpm.record.bytecode.template;

import static io.datakernel.codegen.Expressions.add;
import static io.datakernel.codegen.Expressions.arg;
import static io.datakernel.codegen.Expressions.value;
import static java.util.Arrays.asList;

import io.datakernel.codegen.AsmBuilder;
import io.datakernel.codegen.Expression;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;

public class TemplateIncreaseValueBy extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateIncreaseValueBy(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@Override
	public void addBytecode(AsmBuilder<?> builder) {		
		BlueprintVariable variable = blueprintMethod.getVariable();	
		
		Expression value = readValueExpression(variable);
		value = add(value, arg(0));
		Expression store = writePrimitiveExpression(variable, value(0), value);
		builder.method(blueprintMethod.getName(), Void.TYPE, asList(variable.getExternalType()), store);
	}	
}
