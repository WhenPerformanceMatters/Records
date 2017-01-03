package net.wpm.record.bytecode.template;

import java.util.Collections;

import net.wpm.codegen.ClassBuilder;
import net.wpm.record.blueprint.BlueprintMethod;

public class TemplateGetRecordSize extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateGetRecordSize(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(ClassBuilder<?> builder) {		
		builder.method(blueprintMethod.getName(), int.class, Collections.EMPTY_LIST, recordSize());			
	}	
}
