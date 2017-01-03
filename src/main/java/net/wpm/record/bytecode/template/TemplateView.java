package net.wpm.record.bytecode.template;

import static net.wpm.codegen.Expressions.call;
import static net.wpm.codegen.Expressions.cast;
import static net.wpm.codegen.Expressions.let;
import static net.wpm.codegen.Expressions.sequence;

import java.util.Collections;

import net.wpm.codegen.ClassBuilder;
import net.wpm.codegen.Expression;
import net.wpm.record.blueprint.BlueprintMethod;

public class TemplateView extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	protected Class<?> blueprintType;
	
	public TemplateView(BlueprintMethod blueprintMethod, Class<?> blueprintType) {
		this.blueprintMethod = blueprintMethod;
		this.blueprintType = blueprintType;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(ClassBuilder<?> builder) {	
		Expression recordView = let(call(adapter(), "newInstance"));
		Expression setId = call(recordView, "setRecordId", address());
		Expression cast = cast(recordView, blueprintType);
		
		Expression seq = sequence(recordView, setId, cast);		
		builder.method(blueprintMethod.getName(), blueprintType, Collections.EMPTY_LIST, seq);			
	}	
}
