package net.wpm.record.bytecode.template;

import net.wpm.codegen.ClassBuilder;

public interface ASMTemplate {	
	public void addBytecode(ClassBuilder<?> builder);	
}
