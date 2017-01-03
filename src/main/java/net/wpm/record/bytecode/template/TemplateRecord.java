package net.wpm.record.bytecode.template;

import static net.wpm.codegen.Expressions.getter;
import static net.wpm.codegen.Expressions.self;
import static net.wpm.codegen.Expressions.sequence;
import static net.wpm.codegen.Expressions.setter;
import static net.wpm.codegen.Expressions.value;

import java.util.Collections;

import net.wpm.codegen.ClassBuilder;
import net.wpm.record.RecordAdapter;
import net.wpm.record.bytes.MemoryAccess;

/**
 * 
 * @author Nico Hezel
 */
public class TemplateRecord extends TemplateBase {

	protected int sizeInBytes;
	
	public TemplateRecord(int sizeInBytes) {
		this.sizeInBytes = sizeInBytes;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(ClassBuilder<?> builder) {
		
		// TODO: everything should be final
		// TODO: name of variables should have a prefix z.b. ASMRecord_recordSize
		builder.staticConstant("recordSize", int.class);
		builder.staticField("blueprintId", int.class);
		builder.staticField("recordAdapter", RecordAdapter.class);
		builder.staticField("memoryAccess", MemoryAccess.class);

		builder.staticInitializationBlock(sequence(
					setter(self(), "recordSize",  value(sizeInBytes)),
					setter(self(), "blueprintId",  value(-1)) // TODO könnte gleich mit gesetzt werden
				));
		
		builder.method("getBlueprintId", int.class, Collections.EMPTY_LIST, getter(self(), "blueprintId"));
		builder.method("getRecordSize", int.class, Collections.EMPTY_LIST, getter(self(), "recordSize"));
		builder.method("getMemoryAccess", MemoryAccess.class, Collections.EMPTY_LIST, getter(self(), "memoryAccess"));
		builder.method("getRecordAdapter", RecordAdapter.class, Collections.EMPTY_LIST, getter(self(), "recordAdapter"));
	}
}