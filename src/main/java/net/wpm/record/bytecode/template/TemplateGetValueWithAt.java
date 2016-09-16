package net.wpm.record.bytecode.template;

import static io.datakernel.codegen.Expressions.arg;
import static java.util.Arrays.asList;

import io.datakernel.codegen.AsmBuilder;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;

public class TemplateGetValueWithAt extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateGetValueWithAt(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@Override
	public void addBytecode(AsmBuilder<?> builder) {		
		BlueprintVariable variable = blueprintMethod.getVariable();
		builder.method(blueprintMethod.getName(), variable.getExternalType(), asList(int.class, variable.getExternalType()), readRecordWithExpression(variable, arg(0), arg(0)));			
	}	
}
