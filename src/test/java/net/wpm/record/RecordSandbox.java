package net.wpm.record;

import java.io.IOException;

import net.wpm.record.annotation.Array;

/**
 * This class exists only for test purposes. 
 * 
 * @author Nico Hezel
 */
public class RecordSandbox {

	public static enum VehicleType { Bicycle, Car, Truck, Train }

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {		
		Records.register(Bar.class);
		Bar bar = Records.of(Bar.class);	
		bar.setVehicleType(VehicleType.Car);
		bar.setVehicleTypeAt(2, VehicleType.Train);
		
		System.out.println(bar.getVehicleType());
		System.out.println(bar.getVehicleTypeAt(2));
		System.out.println(bar.getVehicleTypeSize());
		System.out.println(bar);
	}

	protected static interface Bar {
		
		@Array(size=3)
		public int getVehicleTypeSize();
		public VehicleType getVehicleType();
		public VehicleType getVehicleTypeAt(int index);
		public void setVehicleType(VehicleType type);
		public void setVehicleTypeAt(int index, VehicleType type);
	}
}