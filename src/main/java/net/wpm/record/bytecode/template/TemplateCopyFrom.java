package net.wpm.record.bytecode.template;

import static io.datakernel.codegen.Expressions.arg;
import static io.datakernel.codegen.Expressions.call;
import static io.datakernel.codegen.Expressions.cast;
import static java.util.Arrays.asList;

import io.datakernel.codegen.AsmBuilder;
import io.datakernel.codegen.Expression;
import net.wpm.record.RecordView;
import net.wpm.record.blueprint.BlueprintMethod;

public class TemplateCopyFrom extends TemplateBase {
	
	protected BlueprintMethod blueprintMethod;
	protected Class<?> returnType;
	
	public TemplateCopyFrom(BlueprintMethod blueprintMethod, Class<?> returnType) {
		this.blueprintMethod = blueprintMethod;
		this.returnType = returnType;
	}
	
	
	@Override
	public void addBytecode(AsmBuilder<?> builder) {
		Expression fromId = call(cast(arg(0), RecordView.class), "getRecordId");
		Expression toId = address();
		Expression recordSize = recordSize();		
		builder.method("copyFrom", Void.TYPE, asList(returnType), call(memoryAccess(), "copy", fromId, toId, recordSize));
	}	
}
