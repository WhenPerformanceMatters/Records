package net.wpm.record.samples;

/**
 * TODO sequence iteration
 * TODO clear zeroes sämtlichen inhalt
 * TODO Teste ob zugriffe auf protected variablen auch den polymorphic einflüssen unterlegen?
 * TODO Annotation an blueprint methoden die ihren ActionType ändern obwohl der Name nicht passt.
 * 		-> BlueprintInspector muss alle methoden mit invaliden Namen auf diese Annotation testen
 * TODO copy methode für inhalt (auch in ein byte array rein)
 * 
 * @author Nico
 *
 */
public class RecordsSample_xx {

	/**
	 * Polymorphic vermeiden in dem kein interface mit allgemeinen methoden verwendet wird 
	 */	
	public static interface StructCommon {
		public int structId();
	}
	public static interface Foo1 extends StructCommon {
		public int getNumber();
	}
	public static interface Foo2 extends StructCommon {
		public int getNumber();
	}
	public static interface Foo3 extends StructCommon {
		public int getNumber();
	}
}
