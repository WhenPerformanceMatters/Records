package net.wpm.record.bytecode.template;

import static net.wpm.codegen.Expressions.call;
import static net.wpm.codegen.Expressions.callStatic;
import static net.wpm.codegen.Expressions.cast;
import static net.wpm.codegen.Expressions.constructor;
import static net.wpm.codegen.Expressions.getter;
import static net.wpm.codegen.Expressions.self;
import static net.wpm.codegen.Expressions.setter;
import static net.wpm.codegen.Expressions.value;

import java.util.Collections;

import net.wpm.codegen.AsmBuilder;
import net.wpm.codegen.Expression;
import net.wpm.record.RecordAdapter;
import net.wpm.record.RecordView;
import net.wpm.record.Records;
import net.wpm.record.blueprint.BlueprintMethod;
import net.wpm.record.blueprint.BlueprintVariable;
import net.wpm.record.collection.RecordSequence;

/**
 * TODO can not deal with enum or primitive type sequences at the moment
 * 
 * @author Nico Hezel
 */
public class TemplateGetSequence extends TemplateBase {

	protected BlueprintMethod blueprintMethod;
	
	public TemplateGetSequence(BlueprintMethod blueprintMethod) {
		this.blueprintMethod = blueprintMethod;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(AsmBuilder<?> builder) {		
		BlueprintVariable variable = blueprintMethod.getVariable();		
		
		// TODO could be much faster if there is a way to access the internal RecordAdapter Array of the Records class
		int blueprintId = Records.blueprintId(variable.getExternalType());
		Expression view = callStatic(Records.class, "view", value(blueprintId));
		Expression variableAdapter = call(cast(view, RecordView.class), "getRecordAdapter");
		
		// create a static field containing the adapter for the current variable
		String variableAdapterName = variable.getName()+"RecordAdapter";
		builder.staticField(variableAdapterName, RecordAdapter.class);
		builder.staticInitializationBlock(setter(self(), variableAdapterName, variableAdapter));
		
		Expression adapter = getter(self(), variableAdapterName);
		Expression count = value(variable.getElementCount());
		Expression constructSequence = constructor(RecordSequence.class, adapter, address(), count);
		
		// TODO should create only the method which the user desired
		builder.method(blueprintMethod.getName(), Iterable.class, Collections.EMPTY_LIST, constructSequence);
		builder.method(blueprintMethod.getName(), RecordSequence.class, Collections.EMPTY_LIST, constructSequence);
	}
}