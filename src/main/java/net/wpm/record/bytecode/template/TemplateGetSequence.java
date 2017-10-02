package net.wpm.record.bytecode.template;

import static net.wpm.codegen.Expressions.call;
import static net.wpm.codegen.Expressions.callStatic;
import static net.wpm.codegen.Expressions.constructor;
import static net.wpm.codegen.Expressions.getter;
import static net.wpm.codegen.Expressions.ifTrue;
import static net.wpm.codegen.Expressions.isNull;
import static net.wpm.codegen.Expressions.self;
import static net.wpm.codegen.Expressions.sequence;
import static net.wpm.codegen.Expressions.setter;
import static net.wpm.codegen.Expressions.value;

import java.util.Collections;

import net.wpm.codegen.ClassBuilder;
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
	public void addBytecode(ClassBuilder<?> builder) {		
		BlueprintVariable variable = blueprintMethod.getVariable();		
		
		// TODO could be much faster if there is a way to access the internal RecordAdapter Array of the Records class
		int blueprintId = Records.blueprintId(variable.getExternalType());
		Expression variableAdapter = callStatic(RecordView.class, "recordAdapter", value(blueprintId));
		
		// create a static field containing the adapter for the current variable
		String variableAdapterName = variable.getName()+"RecordAdapter";
		builder.staticConstant(variableAdapterName, RecordAdapter.class);
		builder.staticInitializationBlock(setter(self(), variableAdapterName, variableAdapter));
		
		// create a new RecordSequence with the adapter of the static field
		Expression adapter = getter(self(), variableAdapterName);
		Expression count = value(variable.getElementCount());
		Expression constructSequence = constructor(RecordSequence.class, adapter, address(), count);

		// add a member field for the constructed RecordSequence
		String sequenceName = variable.getName()+"RecordSequence";
		builder.field(sequenceName, RecordSequence.class);
		Expression getSequence = getter(self(), sequenceName);
		Expression setSequence = setter(self(), sequenceName, constructSequence);
		Expression updateSequence = call(getSequence, "setAddress", address());
		Expression lazySequenceInit = ifTrue(isNull(getSequence), setSequence);
		Expression seq = sequence(lazySequenceInit, updateSequence, getSequence);
		
		// TODO should create only the method which the user desired
		builder.method(blueprintMethod.getName(), Iterable.class, Collections.EMPTY_LIST, seq);
		builder.method(blueprintMethod.getName(), RecordSequence.class, Collections.EMPTY_LIST, seq);
	}
}