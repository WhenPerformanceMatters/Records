package net.wpm.record.bytecode.template;

import static java.util.Arrays.asList;
import static net.wpm.codegen.Expressions.arg;
import static net.wpm.codegen.Expressions.call;
import static net.wpm.codegen.Expressions.cast;

import net.wpm.codegen.ClassBuilder;
import net.wpm.codegen.Expression;
import net.wpm.record.RecordView;
import net.wpm.record.blueprint.BlueprintMethod;

public class TemplateCopyFrom extends TemplateBase {
	
	protected BlueprintMethod blueprintMethod;
	protected Class<?> blueprintType;
	
	public TemplateCopyFrom(BlueprintMethod blueprintMethod, Class<?> blueprintType) {
		this.blueprintMethod = blueprintMethod;
		this.blueprintType = blueprintType;
	}
	
	
	@Override
	public void addBytecode(ClassBuilder<?> builder) {
		Expression fromId = call(cast(arg(0), RecordView.class), "getRecordId");
		Expression toId = address();
		Expression recordSize = recordSize();		
		builder.method("copyFrom", Void.TYPE, asList(blueprintType), call(memoryAccess(), "copy", fromId, toId, recordSize));
	}	
}
