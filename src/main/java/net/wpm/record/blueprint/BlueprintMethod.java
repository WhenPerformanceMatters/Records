package net.wpm.record.blueprint;

/**
 * Every blueprint method works on a variable. The method 
 * is either manipulating or reading its content.
 * 
 * @author Nico
 *
 */
public class BlueprintMethod {
	
	public static enum ActionType { 
			GetValue, GetValueAt, GetValueWith, GetValueWithAt, 
			SetValue, SetValueAt, GetArraySize,
			IncreaseValue, IncreaseValueBy, DecreaseValue, DecreaseValueBy,
			GetRecordId, SetRecordId, GetRecordSize, GetBlueprintId, 
			Copy, CopyFrom, View
		};

	protected final String name;
	protected final ActionType actionType;	
	protected final BlueprintVariable variable;
	
	public BlueprintMethod(String name, ActionType action) {
		this.name = name;
		this.actionType = action;
		this.variable = null;
	}
	
	public BlueprintMethod(String name, ActionType type, BlueprintVariable underlyingVariable) {
		this.name = name;
		this.actionType = type;
		this.variable = underlyingVariable;
	}


	/**
	 * Name of th eMethod
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Kind of method
	 * 
	 * @return
	 */
	public ActionType getActionType() {
		return actionType;
	}

	/**
	 * optional internal variable the method is based on
	 * 
	 * @return
	 */
	public BlueprintVariable getVariable() {
		return variable;
	}
	
	public String getSignature() {
		switch (actionType) {
			case SetValue:
				return getName() + "(" + variable.getExternalType().getName() + ")";
			case SetValueAt:
				return getName() + "(int, " + variable.getExternalType().getName() + ")";
			case IncreaseValueBy:
			case DecreaseValueBy:
			case SetRecordId:
			case GetValueAt:
				return getName() + "(int)";
			case CopyFrom:
			case GetValueWith:
				return getName() + "(Object)";
			case GetValueWithAt:
				return getName() + "(int, Object)";
			default:
				return getName() + "()";
		}		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		switch (actionType) {
			case GetValue:
			case GetValueWith:
			case GetValueAt:
			case GetValueWithAt:
			case GetRecordId:
			case GetBlueprintId:
			case GetRecordSize:
			case IncreaseValue:
			case IncreaseValueBy:
			case DecreaseValue:
			case DecreaseValueBy:
				sb.append(variable.getExternalType().getName());
				sb.append(" " + getSignature());
				sb.append(" {" + actionType.toString() + "}");
				break;
			case SetValue:
			case SetValueAt:
			case SetRecordId:
			case CopyFrom:
				sb.append("void " + getSignature());
				sb.append(" {" + actionType.toString() + "}");
				break;
			case GetArraySize:
				sb.append("int " + getSignature());
				sb.append(" {" + actionType.toString() + "}");
				break;
			case Copy:
			case View:
				sb.append("Object " + getSignature());
				sb.append(" {" + actionType.toString() + "}");
				break;
			default:
				sb.append(getSignature());
				sb.append(" " + variable.getExternalType().getName());
				sb.append(" " + actionType.toString());
				break;
		}
		return sb.toString();
	}
}
