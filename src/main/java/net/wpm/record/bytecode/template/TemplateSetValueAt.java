package net.wpm.record.bytecode.template;

import static java.util.Arrays.asList;
import static net.wpm.codegen.Expressions.arg;

import net.wpm.codegen.AsmBuilder;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;

public class TemplateSetValueAt extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateSetValueAt(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@Override
	public void addBytecode(AsmBuilder<?> builder) {		
		BlueprintVariable variable = blueprintMethod.getVariable();	
		builder.method(blueprintMethod.getName(), Void.TYPE, asList(int.class, variable.getExternalType()), writeValueExpression(variable, arg(0), arg(1))); // index, value			
	}	
}
