package net.wpm.record;

import java.io.IOException;
import java.util.Iterator;

import net.wpm.record.Records;
import net.wpm.record.annotation.Array;
import net.wpm.record.collection.RecordIterator;
import net.wpm.record.collection.RecordSequence;

/**
 * This class exists only for test purposes. 
 * 
 * @author Nico Hezel
 */
public class RecordSandbox {

//	public static enum VehicleType { Bicycle, Car, Truck, Train }

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {		
		Records.register(Bar.class);
		Records.register(Foo.class);
		Bar bar = Records.of(Bar.class);	
//		bar.setVehicleType(VehicleType.Car);
//		bar.setVehicleTypeAt(2, VehicleType.Train);
		
//		for (Foo foo : bar.getFoo()) 			
//			System.out.println(foo);
		
		Iterator<Foo> it = bar.getFooIterator();
		while(it.hasNext())
			System.out.println(it.next());
		
//		System.out.println(bar.getVehicleTypeAt(2));
//		System.out.println(bar.getVehicleTypeSize());
		System.out.println(bar);
	}

	protected static interface Bar {
		
		@Array(size=3)
//		public RecordSequence<Foo> getFoo();
		public Iterator<Foo> getFooIterator();
		
//		@Array(size=3)
//		public int getVehicleTypeSize();
//		public VehicleType getVehicleTypeAt(int index);
//		public void setVehicleType(VehicleType type);
//		public void setVehicleTypeAt(int index, VehicleType type);
//		public Iterable<VehicleType> getVehicleType();
	}
	
	protected static interface Foo {
		public int getValue();
	}
}