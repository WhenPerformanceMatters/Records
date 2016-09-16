package net.wpm.record.bytes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.Memory;
import net.openhft.chronicle.core.OS;

public class UnsafeMemoryAdapter implements MemoryAccess {
	
	// net.openhft.chronicle.core.UnsafeMemory
	protected static final Memory memory = OS.memory();
	protected static final int BlockSize = 4096;			// size of the piece of memory
	protected static final int OverProvision = 256; 		// sufficient bytes
	
	// all pieces of memory allocaed
	protected final Map<Long, UnsafeBytes> addressToBytes = new HashMap<>();
	
	// list of memory which has at least OverProvision free
	protected final LinkedList<UnsafeBytes> freeBytes = new LinkedList<>();
	
	/**
	 * Only one instance of UnsafeMemoryAdapter
	 */
	protected static UnsafeMemoryAdapter instance = new UnsafeMemoryAdapter();	
	public static UnsafeMemoryAdapter getInstance() {
		return instance;
	}
	
	/**
	 * Start with an initial allocation of 4096 bytes
	 */
	private UnsafeMemoryAdapter() {
		freeBytes.add(allocate(BlockSize));
	}
	
	/**
	 * Allocate a new piece of memory
	 * 
	 * @param size
	 */
	protected final UnsafeBytes allocate(int size) {	
//		System.out.println("Allocate "+size+" bytes of memory.");
		UnsafeBytes bytes = new UnsafeBytes(memory, size);
		addressToBytes.put(bytes.address, bytes);
		return bytes;
	}
	
	/**
	 * Returns a piece of memory containing the desired amount of free space.
	 * The memory is either retrieved from the list of FreeBytes or has been allocated.
	 * 
	 * @param size
	 * @return
	 */
	protected final UnsafeBytes ensureCapacity(final int size) {
		
		// find a piece of memory which has enough space
		Iterator<UnsafeBytes> it = freeBytes.iterator();
		while(it.hasNext()) {
			UnsafeBytes bytes = it.next();
			if(bytes.hasCapacity(size)) {
				it.remove();
				return bytes;
			}
		}
				
		return allocate(Math.max(BlockSize, size));
	}
		
	/**
	 * Reserviert eine bestimmte Menge an Speicher. 
	 * Alloziert welchen falls keiner vorhanden ist.
	 */
	@Override
	public final long reserve(final int size) {
		final UnsafeBytes bytes = ensureCapacity(size);
		
		// use some parts of it
		final long address = bytes.address;
		bytes.use(size);
				
		// still enough space, add to the list of free bytes
		if(bytes.remaining() < OverProvision) 
			freeBytes.add(bytes);
		
		return address;
	}
	
	@Override
	public void releaseAll() {
		for (UnsafeBytes memory : addressToBytes.values()) {
			memory.release();
		}
		addressToBytes.clear();
	}

	@Override
	public long capacity() {
		long max = Jvm.maxDirectMemory();
		long used = Jvm.usedDirectMemory();
		return max - used;
	}

	@Override
	public boolean getBoolean(long address) {
		return getByte(address) != 0;
	}

	@Override
	public void setBoolean(long address, boolean value) {
		setByte(address, value ? (byte)'Y' : 0);
	}

	@Override
	public byte getByte(long address) {
		return memory.readByte(address);
	}

	@Override
	public void setByte(long address, byte value) {
		memory.writeByte(address, value);
	}

	@Override
	public short getShort(long address) {
		return memory.readShort(address);
	}

	@Override
	public void setShort(long address, short value) {
		memory.writeShort(address, value);
	}

	@Override
	public int getInt(long address) {
		return memory.readInt(address);
	}

	@Override
	public void setInt(long address, int value) {
		memory.writeInt(address, value);
	}

	@Override
	public float getFloat(long address) {
		return memory.readFloat(address);
	}

	@Override
	public void setFloat(long address, float value) {
		memory.writeFloat(address, value);
	}

	@Override
	public long getLong(long address) {
		return memory.readLong(address);
	}

	@Override
	public void setLong(long address, long value) {
		memory.writeLong(address, value);
	}

	@Override
	public double getDouble(long address) {
		return memory.readDouble(address);
	}

	@Override
	public void setDouble(long address, double value) {
		memory.writeDouble(address, value);
	}

	@Override
	public void copy(long fromAddress, long toAddress, int length) {
		memory.copyMemory(fromAddress, toAddress, length);
	}
}
