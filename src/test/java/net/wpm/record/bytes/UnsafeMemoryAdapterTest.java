package net.wpm.record.bytes;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.wpm.record.bytes.UnsafeMemoryAdapter;

public class UnsafeMemoryAdapterTest {

	private UnsafeMemoryAdapter memory;
	
	@Before
	public void setUpBeforeClass() throws Exception {
		this.memory = UnsafeMemoryAdapter.getInstance();		
	}
	
	@Test
	public void releaseAllTest() {
		memory.releaseAll();
		long before = memory.capacity();
		memory.reserve(10_000);
		memory.releaseAll();
		long after = memory.capacity();
		assertEquals(before, after);
	}
	
	@Test
	public void reserveTest() {
		long before = memory.capacity();
		memory.reserve(10_000);
		long after = memory.capacity();
		assertEquals(10_000, before-after);
	}	
	
	@Test
	public void booleanTest() {
		long address = memory.reserve(1);
		boolean input = false;
		memory.setBoolean(address, input);
		boolean output = memory.getBoolean(address);
		assertEquals(input, output);
	}
	
	@Test
	public void byteTest() {
		long address = memory.reserve(1);
		byte input = 17;
		memory.setByte(address, input);
		byte output = memory.getByte(address);
		assertEquals(input, output);
	}
	
	@Test
	public void shortTest() {
		long address = memory.reserve(2);
		short input = 1007;
		memory.setShort(address, input);
		short output = memory.getShort(address);
		assertEquals(input, output);
	}
	
	@Test
	public void intTest() {
		long address = memory.reserve(4);
		int input = 100000007;
		memory.setInt(address, input);
		int output = memory.getInt(address);
		assertEquals(input, output);
	}
	
	@Test
	public void longTest() {
		long address = memory.reserve(8);
		long input = 76132779641317L;
		memory.setLong(address, input);
		long output = memory.getLong(address);
		assertEquals(input, output);
	}
	
	@Test
	public void floatTest() {
		long address = memory.reserve(4);
		float input = 7.17f;
		memory.setFloat(address, input);
		float output = memory.getFloat(address);
		assertEquals(input, output, 0.001);
	}
	
	@Test
	public void doubleTest() {
		long address = memory.reserve(8);
		double input = 0.17;
		memory.setDouble(address, input);
		double output = memory.getDouble(address);
		assertEquals(input, output, 0.0001);
	}
}
