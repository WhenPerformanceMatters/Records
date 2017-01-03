package net.wpm.record.bytecode.template;

import static net.wpm.codegen.Expressions.asString;
import static net.wpm.codegen.Expressions.call;
import static net.wpm.codegen.Expressions.self;
import static net.wpm.codegen.Expressions.value;

import java.util.Collection;
import java.util.Collections;

import net.wpm.codegen.ClassBuilder;
import net.wpm.codegen.ExpressionToString;
import net.wpm.record.blueprint.BlueprintVariable;

public class TemplateToString extends TemplateBase {

	protected Collection<BlueprintVariable> variables;
	protected boolean customToStringMethod;
	
	public TemplateToString(Collection<BlueprintVariable> variables, boolean customToStringMethod){
		this.variables = variables;
		this.customToStringMethod = customToStringMethod;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addBytecode(ClassBuilder<?> builder) {
		
		// use a custom to string method
		if(customToStringMethod) {
			builder.method("toString", String.class, Collections.EMPTY_LIST, call(self(), "string"));
			return;
		}
		
		// write your own one
		ExpressionToString ets = asString().quotes("{", "}", ", ");
		for (BlueprintVariable variable : variables) {
			
			if(variable.isArray()) {
				ExpressionToString arrayEts = asString().quotes("[", "]", ", ");
				for (int i = 0; i < variable.getElementCount(); i++)	{
					arrayEts.add(readValueExpression(variable, value(i)));
				}
				ets.add(variable.getName()+": ", arrayEts);
			}
			else 
				ets.add(variable.getName()+": ", readValueExpression(variable));
		}
		builder.method("toString", String.class, Collections.EMPTY_LIST, ets);
	}
}