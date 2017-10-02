package net.wpm.record.bytes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.openhft.chronicle.core.Memory;
import net.openhft.chronicle.core.OS;
import net.wpm.record.bytes.UnsafeBytes;

public class UnsafeBytesTest {

	private static Memory memory = OS.memory();
	
	@Test
	public void remainingTest() {
		UnsafeBytes bytes = new UnsafeBytes(memory, 100);	
		assertEquals(100, bytes.remaining());
		bytes.release();
	}	
	
	@Test
	public void releaseTest() {
		UnsafeBytes bytes = new UnsafeBytes(memory, 100);	
		bytes.release();
		assertEquals(0, bytes.remaining());
		bytes.release();
	}
	
	@Test
	public void sufficientCapacityTest() {
		UnsafeBytes bytes = new UnsafeBytes(memory, 100);	
		assertEquals(true, bytes.hasCapacity(100));
		bytes.release();
	}
	
	@Test
	public void insufficientCapacityTest() {
		UnsafeBytes bytes = new UnsafeBytes(memory, 100);	
		assertEquals(false, bytes.hasCapacity(101));
		bytes.release();
	}
	
	@Test
	public void invalidCapacityTest() {
		UnsafeBytes bytes = new UnsafeBytes(memory, 100);	
		assertEquals(false, bytes.hasCapacity(-1));
		bytes.release();
	}
	
	@Test
	public void useTest() {
		UnsafeBytes bytes = new UnsafeBytes(memory, 100);	
		int before = bytes.remaining();
		bytes.use(1);
		int after = bytes.remaining();
		assertEquals(1, before-after);
		bytes.release();
	}
	
	@Test
	public void addressTest() {
		UnsafeBytes bytes = new UnsafeBytes(memory, 100);	
		long address = bytes.freeAddress();
		memory.writeInt(address, 16);
		int output = memory.readInt(address);
		assertEquals(16, output);
		bytes.release();
	}
}