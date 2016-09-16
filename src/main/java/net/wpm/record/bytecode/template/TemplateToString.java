package net.wpm.record.bytecode.template;

import static io.datakernel.codegen.Expressions.asString;
import static io.datakernel.codegen.Expressions.call;
import static io.datakernel.codegen.Expressions.self;
import static io.datakernel.codegen.Expressions.value;

import java.util.Collection;
import java.util.Collections;

import io.datakernel.codegen.AsmBuilder;
import io.datakernel.codegen.ExpressionToString;
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
	public void addBytecode(AsmBuilder<?> builder) {
		
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
				for (int i = 0; i < variable.getLength(); i++)	{
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