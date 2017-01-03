package net.wpm.record.bytecode.template;

import static java.util.Arrays.asList;
import static net.wpm.codegen.Expressions.arg;

import net.wpm.codegen.ClassBuilder;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;

public class TemplateGetValueAt extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateGetValueAt(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@Override
	public void addBytecode(ClassBuilder<?> builder) {		
		BlueprintVariable variable = blueprintMethod.getVariable();
		builder.method(blueprintMethod.getName(), variable.getExternalType(), asList(int.class), readValueExpression(variable, arg(0)));			
	}	
}
