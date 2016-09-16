package net.wpm.record.bytecode.template;

import java.util.Collections;

import io.datakernel.codegen.AsmBuilder;
import net.wpm.record.blueprint.BlueprintMethod;

public class TemplateGetRecordId extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateGetRecordId(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(AsmBuilder<?> builder) {		
		builder.method(blueprintMethod.getName(), long.class, Collections.EMPTY_LIST, address());			
	}	
}
