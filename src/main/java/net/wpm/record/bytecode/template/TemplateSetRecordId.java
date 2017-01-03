package net.wpm.record.bytecode.template;

import static java.util.Arrays.asList;
import static net.wpm.codegen.Expressions.arg;

import net.wpm.codegen.ClassBuilder;
import net.wpm.record.blueprint.BlueprintMethod;

public class TemplateSetRecordId extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateSetRecordId(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@Override
	public void addBytecode(ClassBuilder<?> builder) {		
		builder.method(blueprintMethod.getName(), Void.TYPE, asList(long.class), setAddress(arg(0)));			
	}	
}
