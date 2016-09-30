package net.wpm.record.samples;

/**
 * TODO clear-method: zeroes the entire content
 * TODO check if access to protected variables is slower under polymorphic effect
 * TODO an annotation at the blueprint method should define a ActionType, ignoring the naming conventions 
 * 		-> BlueprintInspector has to analyze all methods for this annotation
 * TODO copy-method: copy the content into a byte array
 * 
 * @author Nico Hezel
 */
public class RecordsSample_xx {

	/**
	 * Avoid interfaces with common methods only for convenient reasons. Those methods are subject to polymorphic effects and therefor slow.
	 */	
	public static interface RecordCommon {
		public int recordId();
	}
	public static interface Foo1 extends RecordCommon {
		public int getNumber();
	}
	public static interface Foo2 extends RecordCommon {
		public int getNumber();
	}
	public static interface Foo3 extends RecordCommon {
		public int getNumber();
	}
}
