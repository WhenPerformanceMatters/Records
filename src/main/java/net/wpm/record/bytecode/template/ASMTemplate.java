package net.wpm.record.bytecode.template;

import io.datakernel.codegen.AsmBuilder;

public interface ASMTemplate {	
	public void addBytecode(AsmBuilder<?> builder);	
}
