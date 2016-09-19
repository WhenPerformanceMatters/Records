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
 * @author Nico
 *
 */
public abstract class TemplateBase implements ASMTemplate {

	// -----------------------------------------------------------------------------------------------------
	// ---------------------- dealing with other records or primitive data types ---------------------------
	// -----------------------------------------------------------------------------------------------------
	
	/**
	 * Creates an expression to read the content of the variable
	 * 
	 * @param variable
	 * @return Expression
	 */
	protected Expression readValueExpression(BlueprintVariable variable) {
		return readValueExpression(variable, value(0));
	}

	/**
	 * Creates an expression to read the content of the variable
	 * 
	 * @param variable
	 * @return Expression
	 */
	protected Expression readValueExpression(BlueprintVariable variable, Expression index) {		
		boolean isBlueprint = Records.blueprintId(variable.getInternalType()) > 0;
		return isBlueprint ? readRecordExpression(variable, index) : readPrimitiveExpression(variable, index);
	}
	
	/**
	 * Creates an expression to write the content of the variable
	 * 
	 * @param variable
	 * @return Expression
	 */
	protected Expression writeValueExpression(BlueprintVariable variable, Expression value) {
		return writeValueExpression(variable, value(0), value);
	}
	
	/**
	 * Creates an expression to write the content of the variable
	 * 
	 * @param variable
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
	 * @param variable
	 * @param index of the array (1 = no array)
	 * @return
	 */
	protected Expression readRecordExpression(BlueprintVariable variable, Expression index) {		
		int blueprintId = Records.blueprintId(variable.getInternalType());
		return callStatic(Records.class, "view", value(blueprintId), addressOf(variable, index));
	}	
	
	/**
	 * Creates an expression to read the content of another record. Reuses the given record view. 
	 *  
	 * @param variable
	 * @param index
	 * @param withRecordView
	 * @return
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
	 * @param variable
	 * @param index of the array (1 = no array)
	 * @param recordView
	 * @return
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
	 * @param variable
	 * @param index of the array (1 = no array)
	 * @return
	 */
	protected Expression readPrimitiveExpression(BlueprintVariable variable, Expression index) {		
		String methodName = "get"+RecordClassGenerator.capitalize(variable.getInternalType().getName());		
		return call(memoryAccess(), methodName, addressOf(variable, index));
	}

	
	/**
	 * Creates an expression to write the content of the variable
	 * 
	 * @param variable
	 * @param index of the array (1 = no array)
	 * @param value
	 * @return
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
	 * @return
	 */
	protected Expression address() {
		return getter(self(), "address");
	}
		
	/**
	 * An Expression representing a setter for the record id or address of the record in memory
	 * 
	 * @return
	 */
	protected Expression setAddress(Expression exp) {		
		return setter(self(), "address", exp);
	}
	
	
	/**
	 * An Expression representing the offset value of the variable in the memory buffer
	 * 
	 * @param variable
	 * @return
	 */
	protected Expression addressOf(BlueprintVariable variable) {
		return add(address(), value(variable.getOffset()));	
	}
	
	/**
	 * An Expression representing the offset value at the specified array index of the variable in the memory buffer
	 * 
	 * @param variable
	 * @param index
	 * @return
	 */
	protected Expression addressOf(BlueprintVariable variable, Expression index) {
		return add(addressOf(variable), mul(index, value(variable.getSizeInBytes())));	// offset + index * sizeInBytes
	}
	
	/**
	 * An Expression representing the memory buffer
	 * 
	 * @return
	 */
	protected Expression memoryAccess() {
		return getter(self(), "memoryAccess");
	}
	
	/**
	 * An Expression representing blueprint id
	 * 
	 * @return
	 */
	protected Expression blueprintId() {
		return getter(self(), "blueprintId");
	}
	
	/**
	 * An Expression representing record size
	 * 
	 * @return
	 */
	protected Expression recordSize() {
		return getter(self(), "recordSize");
	}

	/**
	 * An Expression representing blueprint id
	 * 
	 * @return
	 */
	protected Expression adapter() {
		return getter(self(), "recordAdapter");
	}
}
