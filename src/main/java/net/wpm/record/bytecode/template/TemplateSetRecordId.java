package net.wpm.record.bytecode.template;

import static io.datakernel.codegen.Expressions.arg;
import static java.util.Arrays.asList;

import io.datakernel.codegen.AsmBuilder;
import net.wpm.record.blueprint.BlueprintMethod;

public class TemplateSetRecordId extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateSetRecordId(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@Override
	public void addBytecode(AsmBuilder<?> builder) {		
		builder.method(blueprintMethod.getName(), Void.TYPE, asList(long.class), setAddress(arg(0)));			
	}	
}
