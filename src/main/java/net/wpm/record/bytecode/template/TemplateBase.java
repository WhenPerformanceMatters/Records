package net.wpm.record.bytecode.template;

import static net.wpm.codegen.Expressions.add;
import static net.wpm.codegen.Expressions.call;
import static net.wpm.codegen.Expressions.callStatic;
import static net.wpm.codegen.Expressions.cast;
import static net.wpm.codegen.Expressions.getter;
import static net.wpm.codegen.Expressions.mul;
import static net.wpm.codegen.Expressions.self;
import static net.wpm.codegen.Expressions.setter;
import static net.wpm.codegen.Expressions.value;

import net.wpm.codegen.Expression;
import net.wpm.codegen.Expressions;
import net.wpm.record.RecordView;
import net.wpm.record.Records;
import net.wpm.record.blueprint.BlueprintVariable;
import net.wpm.record.bytecode.RecordClassGenerator;

/**
 * Base class for accessing record view methods and variables.
 * 
 * @author Nico Hezel
 */
public abstract class TemplateBase implements ASMTemplate {

	// -----------------------------------------------------------------------------------------------------
	// ---------------------- dealing with other records or primitive data types ---------------------------
	// -----------------------------------------------------------------------------------------------------
	
	/**
	 * Creates an expression to read the content of the variable
	 * 
	 * @param variable which content is stored in memory
	 * @return Expression
	 */
	protected Expression readValueExpression(BlueprintVariable variable) {
		return readValueExpression(variable, value(0));
	}

	/**
	 * Creates an expression to read the content of the variable
	 * 
	 * @param variable which content is stored in memory
	 * @param index of the array 
	 * @return Expression
	 */
	protected Expression readValueExpression(BlueprintVariable variable, Expression index) {		
		boolean isBlueprint = Records.blueprintId(variable.getInternalType()) > 0;
		return isBlueprint ? readRecordExpression(variable, index) : readPrimitiveExpression(variable, index);
	}
	
	/**
	 * Creates an expression to write the content of the variable
	 * 
	 * @param variable which content is stored in memory
	 * @param value new content
	 * @return Expression
	 */
	protected Expression writeValueExpression(BlueprintVariable variable, Expression value) {
		return writeValueExpression(variable, value(0), value);
	}
	
	/**
	 * Creates an expression to write the content of the variable
	 * 
	 * @param variable which content is stored in memory
	 * @param index of the array 
	 * @param value new content
	 * @return Expression
	 */
	protected Expression writeValueExpression(BlueprintVariable variable, Expression index, Expression value) {
		boolean isBlueprint = Records.blueprintId(variable.getInternalType()) > 0;
		return isBlueprint ? writeRecordExpression(variable, index, value) : writePrimitiveExpression(variable, index, value);
	}
	
	
	// ----------------------------------------------------------------------------------------
	// -------------------------------- dealing with other records ----------------------------
	// ----------------------------------------------------------------------------------------

	/**
	 * Creates an expression to read the content of another record
	 * 
	 * @param variable which content is stored in memory
	 * @param index of the array (1 = no array)
	 * @return Expression
	 */
	protected Expression readRecordExpression(BlueprintVariable variable, Expression index) {		
		int blueprintId = Records.blueprintId(variable.getInternalType());
		return callStatic(Records.class, "view", value(blueprintId), addressOf(variable, index));
	}	
	
	/**
	 * Creates an expression to read the content of another record. Reuses the given record view. 
	 *  
	 * @param variable which content is stored in memory
	 * @param index of the array 
	 * @param withRecordView
	 * @return Expression
	 */
	protected Expression readRecordWithExpression(BlueprintVariable variable, Expression index, Expression withRecordView) {
		return Expressions.sequence(
				call(cast(withRecordView, RecordView.class), "setRecordId", addressOf(variable, index)),
				withRecordView
			);		
	}	

	/**
	 * Creates an expression to write the content of the variable
	 * 
	 * @param variable which content is stored in memory
	 * @param index of the array (1 = no array)
	 * @param recordView
	 * @return Expression
	 */
	protected Expression writeRecordExpression(BlueprintVariable variable, Expression index, Expression recordView) {
		Expression fromAddress = call(cast(recordView, RecordView.class), "getRecordId");
		Expression toAddress = addressOf(variable, index);
		Expression recordSize = value(variable.getSizeInBytes());	
		return call(memoryAccess(), "copy", fromAddress, toAddress, recordSize);
	}
	
	// ----------------------------------------------------------------------------------------
	// ------------------------- dealing with primitive data types ----------------------------
	// ----------------------------------------------------------------------------------------


	/**
	 * Creates an expression to read the content of the variable
	 * 
	 * @param variable which content is stored in memory
	 * @param index of the array (1 = no array)
	 * @return Expression
	 */
	protected Expression readPrimitiveExpression(BlueprintVariable variable, Expression index) {		
		String methodName = "get"+RecordClassGenerator.capitalize(variable.getInternalType().getName());		
		return call(memoryAccess(), methodName, addressOf(variable, index));
	}

	
	/**
	 * Creates an expression to write the content of the variable
	 * 
	 * @param variable which content is stored in memory
	 * @param index of the array (1 = no array)
	 * @param value new content
	 * @return Expression
	 */
	protected Expression writePrimitiveExpression(BlueprintVariable variable, Expression index, Expression value) {
		String methodName = "set"+RecordClassGenerator.capitalize(variable.getInternalType().getName());
		return call(memoryAccess(), methodName, addressOf(variable, index), value);
	}
		
	// ----------------------------------------------------------------------------------------
	// ----------------------------------- helper methods -------------------------------------
	// ----------------------------------------------------------------------------------------
		
	/**
	 * An Expression representing the record id or address of the record in memory
	 * 
	 * @return Expression
	 */
	protected Expression address() {
		return getter(self(), "address");
	}
		
	/**
	 * An Expression representing a setter for the record id or address of the record in memory
	 * 
	 * @param exp
	 * @return Expression
	 */
	protected Expression setAddress(Expression exp) {		
		return setter(self(), "address", exp);
	}
	
	
	/**
	 * An Expression representing the offset value of the variable in the memory buffer
	 * 
	 * @param variable
	 * @return Expression
	 */
	protected Expression addressOf(BlueprintVariable variable) {
		return add(address(), value(variable.getOffset()));	
	}
	
	/**
	 * An Expression representing the offset value at the specified array index of the variable in the memory buffer
	 * 
	 * @param variable
	 * @param elementIndex of the array 
	 * @return Expression
	 */
	protected Expression addressOf(BlueprintVariable variable, Expression elementIndex) {
		return add(addressOf(variable), mul(elementIndex, value(variable.getElementSizeInBytes())));	// offset + index * sizeInBytes
	}
	
	/**
	 * An Expression representing the memory buffer
	 * 
	 * @return Expression
	 */
	protected Expression memoryAccess() {
		return getter(self(), "memoryAccess");
	}
	
	/**
	 * An Expression representing blueprint id
	 * 
	 * @return Expression
	 */
	protected Expression blueprintId() {
		return getter(self(), "blueprintId");
	}
	
	/**
	 * An Expression representing record size
	 * 
	 * @return Expression
	 */
	protected Expression recordSize() {
		return getter(self(), "recordSize");
	}

	/**
	 * An Expression representing blueprint id
	 * 
	 * @return Expression
	 */
	protected Expression adapter() {
		return getter(self(), "recordAdapter");
	}
}
