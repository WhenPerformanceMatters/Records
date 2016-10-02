package net.wpm.record.bytecode.template;

import net.wpm.codegen.AsmBuilder;

public interface ASMTemplate {	
	public void addBytecode(AsmBuilder<?> builder);	
}
