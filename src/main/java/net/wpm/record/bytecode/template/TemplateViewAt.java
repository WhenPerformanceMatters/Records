package net.wpm.record.bytecode.template;

import static java.util.Arrays.asList;
import static net.wpm.codegen.Expressions.arg;
import static net.wpm.codegen.Expressions.call;
import static net.wpm.codegen.Expressions.cast;

import net.wpm.codegen.AsmBuilder;
import net.wpm.codegen.Expression;
import net.wpm.record.RecordView;
import net.wpm.record.blueprint.BlueprintMethod;

public class TemplateViewAt extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	protected Class<?> blueprintType;
	
	public TemplateViewAt(BlueprintMethod blueprintMethod, Class<?> blueprintType) {
		this.blueprintMethod = blueprintMethod;
		this.blueprintType = blueprintType;
	}
	
	@Override
	public void addBytecode(AsmBuilder<?> builder) {	
		Expression setId = call(cast(arg(0), RecordView.class), "setRecordId", address());		
		builder.method(blueprintMethod.getName(), Void.TYPE, asList(blueprintType), setId);			
	}	
}
