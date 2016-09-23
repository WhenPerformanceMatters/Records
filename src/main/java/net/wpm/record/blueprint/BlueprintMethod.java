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

	/**
	 * Blueprint where this variable is used
	 */
	protected final Class<?> blueprint;
		
	protected final String name;
	protected final ActionType actionType;	
	protected final BlueprintVariable variable;	
	
	public BlueprintMethod(Class<?> blueprint, String name, ActionType actionType) {
		this(blueprint, name, actionType, null);
	}
	
	public BlueprintMethod(Class<?> blueprint, String name, ActionType actionType, BlueprintVariable var) {
		this.blueprint = blueprint;
		this.name = name;
		this.actionType = actionType;
		this.variable = var;
	}


	/**
	 * Name of the Method
	 * 
	 * @return String 
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Kind of method
	 * 
	 * @return ActionType
	 */
	public ActionType getActionType() {
		return actionType;
	}

	/**
	 * optional internal variable the method is based on
	 * 
	 * @return BlueprintVariable
	 */
	public BlueprintVariable getVariable() {
		return variable;
	}
	
	public String getSignature() {
		switch (actionType) {
			case SetValue:
			case GetValueWith:
				return getName() + "(" + variable.getExternalType().getName() + ")";
			case SetValueAt:
			case GetValueWithAt:
				return getName() + "(int, " + variable.getExternalType().getName() + ")";
			case IncreaseValueBy:
			case DecreaseValueBy:
			case GetValueAt:
				return getName() + "(int)";
			case SetRecordId:
				return getName() + "(long)";
			case CopyFrom:
				return getName() + "(" + blueprint.getName() + ")";
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
			case GetRecordSize:
			case GetBlueprintId:
			case GetArraySize:
				sb.append("int " + getSignature());
				sb.append(" {" + actionType.toString() + "}");
				break;
			case GetRecordId:
				sb.append("long " + getSignature());
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
