package net.wpm.record.bytes;

import java.util.Iterator;
import java.util.LinkedList;

import com.koloboke.collect.map.hash.HashLongObjMap;
import com.koloboke.collect.map.hash.HashLongObjMaps;

import net.openhft.chronicle.core.Jvm;
import net.openhft.chronicle.core.Memory;
import net.openhft.chronicle.core.OS;

/**
 * Can not be 
	 * 
	 * Reserviert eine bestimmte Menge an Speicher. 
	 * Alloziert welchen falls keiner vorhanden ist.
 * 
 * TODO: not thread safe
 * 
 * @author Nico Hezel
 *
 */
public class UnsafeMemoryAdapter implements MemoryAccess {
	
	protected static final Memory memory = OS.memory();		// net.openhft.chronicle.core.UnsafeMemory
	protected static final int BlockSize = 4096;			// size of the piece of memory
	protected static final int OverProvision = 256; 		// sufficient bytes
	
	// all pieces of memory allocated
	protected final HashLongObjMap<UnsafeBytes> addressToBytes = HashLongObjMaps.newMutableMap();
	
	// list of memory pieces which has some free space
	protected final LinkedList<UnsafeBytes> freeBytes = new LinkedList<UnsafeBytes>();
	
	/**
	 * Only one instance of UnsafeMemoryAdapter
	 */
	protected static UnsafeMemoryAdapter instance = new UnsafeMemoryAdapter();	
	public static UnsafeMemoryAdapter getInstance() {
		return instance;
	}
	
	/**
	 * Start with an initial allocation of 4096 bytes
	 * 
	 * costs 0C 0B 2A 1P 1M 4N
	 */
	private UnsafeMemoryAdapter() {
		freeBytes.add(allocate(BlockSize));
	}
	
	/**
	 * Allocate a new piece of memory
	 * 
	 * costs 0C 0B 1A 1P 1M 4N
	 * @param size
	 */
	protected final UnsafeBytes allocate(int size) {	
		UnsafeBytes bytes = new UnsafeBytes(memory, size);
		addressToBytes.put(bytes.address, bytes);
		return bytes;
	}
	
	/**
	 * Returns a piece of memory containing the desired amount of free space.
	 * The memory is either retrieved from the list of FreeBytes or has been 
	 * newly allocated.
	 * 
	 * costs 0C ?B ?A ?P 1M 5N
	 * @param size in bytes
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
				
		// or allocate a new one
		return allocate(Math.max(BlockSize, size));
	}
		
	/**
	 * Reserve a specific amount of memory. 
	 * Returns the starting address of reserved region.
	 * 
	 * costs 0C ?B ?A ?P 1M 5N
	 */
	@Override
	public final long reserve(final int size) {
		
		// get a piece of memory able to store the desired size 
		final UnsafeBytes bytes = ensureCapacity(size);
		
		// current free address of this piece 
		final long address = bytes.freeAddress();
		
		// reserve some parts of it
		bytes.use(size);
				
		// does the piece still have enough space? Add to the list of free bytes.
		if(OverProvision < bytes.remaining()) 
			freeBytes.add(bytes);
		
		return address;
	}
	
	/**
	 * Releases all allocated memory.
	 * Start over with a single chunk of unused memory.
	 * 
	 * costs 0C ?B ?A ?P 0M 1N
	 */
	@Override
	public void releaseAll() {
		for (UnsafeBytes memory : addressToBytes.values()) {
			memory.release();
		}
		addressToBytes.clear();
		freeBytes.clear();
		
		freeBytes.add(allocate(BlockSize));
	}

	/**
	 * Maximum amount of allocatable memory. Including memory allocated by DirectByteBuffers.
	 * 
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public int capacity() {
		long max = Jvm.maxDirectMemory();
		long used = Jvm.usedNativeMemory() + Jvm.usedDirectMemory();
		return (int)(max - used);
	}

	/**
	 * costs 0C 1B 0A 0P 0M 0N
	 */
	@Override
	public boolean getBoolean(long address) {
		return getByte(address) != 0;
	}

	/**
	 * costs 0C 1B 0A 0P 0M 0N
	 */
	@Override
	public void setBoolean(long address, boolean value) {
		setByte(address, value ? (byte)'Y' : 0);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public byte getByte(long address) {
		return memory.readByte(address);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public void setByte(long address, byte value) {
		memory.writeByte(address, value);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public short getShort(long address) {
		return memory.readShort(address);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public void setShort(long address, short value) {
		memory.writeShort(address, value);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public int getInt(long address) {
		return memory.readInt(address);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public void setInt(long address, int value) {
		memory.writeInt(address, value);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public float getFloat(long address) {
		return memory.readFloat(address);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public void setFloat(long address, float value) {
		memory.writeFloat(address, value);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public long getLong(long address) {
		return memory.readLong(address);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public void setLong(long address, long value) {
		memory.writeLong(address, value);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public double getDouble(long address) {
		return memory.readDouble(address);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public void setDouble(long address, double value) {
		memory.writeDouble(address, value);
	}

	/**
	 * costs 0C 0B 0A 0P 0M 0N
	 */
	@Override
	public void copy(long fromAddress, long toAddress, int length) {
		memory.copyMemory(fromAddress, toAddress, length);
	}
}
