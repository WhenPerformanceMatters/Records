package net.wpm.record.bytecode.template;

import static io.datakernel.codegen.Expressions.sub;
import static io.datakernel.codegen.Expressions.value;

import java.util.Collections;

import io.datakernel.codegen.AsmBuilder;
import io.datakernel.codegen.Expression;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;

public class TemplateDecreaseValue extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateDecreaseValue(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(AsmBuilder<?> builder) {	
		BlueprintVariable variable = blueprintMethod.getVariable();
		
		Expression value = readValueExpression(variable);
		value = sub(value, value(1));
		Expression store = writePrimitiveExpression(variable, value(0), value);
		builder.method(blueprintMethod.getName(), Void.TYPE, Collections.EMPTY_LIST, store);
	}	
}
