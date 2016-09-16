package net.wpm.record.exception;

public class InvalidBlueprintException extends RuntimeException {

	private static final long serialVersionUID = 8396124614536749930L;

	public InvalidBlueprintException(String message) {
		super(message);
	}
	
	public InvalidBlueprintException(String message, Throwable cause) {
		super(message, cause);
	}

}
