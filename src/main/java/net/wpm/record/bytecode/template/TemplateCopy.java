package net.wpm.record.bytecode.template;

import static net.wpm.codegen.Expressions.call;
import static net.wpm.codegen.Expressions.cast;
import static net.wpm.codegen.Expressions.self;

import java.util.Collections;

import net.wpm.codegen.AsmBuilder;
import net.wpm.codegen.Expression;
import net.wpm.record.blueprint.BlueprintMethod;

public class TemplateCopy extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	protected Class<?> blueprintType;
	
	public TemplateCopy(BlueprintMethod blueprintMethod, Class<?> blueprintType) {
		this.blueprintMethod = blueprintMethod;
		this.blueprintType = blueprintType;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(AsmBuilder<?> builder) {	
		// cast to object, since the RecordAdapter.copy uses generic
		Expression copy = call(adapter(), "copy", cast(self(), Object.class));
		builder.method(blueprintMethod.getName(), blueprintType, Collections.EMPTY_LIST, copy);			
	}	
}
