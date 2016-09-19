package net.wpm.record.bytecode.template;

import static net.wpm.codegen.Expressions.call;
import static net.wpm.codegen.Expressions.cast;
import static net.wpm.codegen.Expressions.let;
import static net.wpm.codegen.Expressions.sequence;

import java.util.Collections;

import net.wpm.codegen.AsmBuilder;
import net.wpm.codegen.Expression;
import net.wpm.record.blueprint.BlueprintMethod;

public class TemplateView extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	protected Class<?> returnType;
	
	public TemplateView(BlueprintMethod blueprintMethod, Class<?> returnType) {
		this.blueprintMethod = blueprintMethod;
		this.returnType = returnType;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(AsmBuilder<?> builder) {	
		// cast to object, since the RecordAdapter.view uses generics
		Expression recordView = let(call(adapter(), "newInstance"));
		Expression setId = call(recordView, "setRecordId", address());
		Expression cast = cast(recordView, returnType);
		
		Expression seq = sequence(recordView, setId, cast);		
		builder.method(blueprintMethod.getName(), returnType, Collections.EMPTY_LIST, seq);			
	}	
}
