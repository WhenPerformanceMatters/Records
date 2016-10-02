package net.wpm.record.collection;

import java.util.function.IntConsumer;

import com.koloboke.collect.IntCursor;

import net.openhft.chronicle.core.Memory;
import net.openhft.chronicle.core.OS;
import net.wpm.record.bytes.UnsafeBytes;

/**
 * Do not use this class. It is not tested throughout.
 * 
 * 
 * TODO maxium size is Integer.MAXVALUE 
 * 
 * @author Nico Hezel
 */
public class IntSequence {
	
	protected static final Memory memory = OS.memory();
	protected static final int elementBytes = 4;
	
	protected final int capacity;
	protected final long address;
	protected final SequenceCursor cursor;
	protected final UnsafeBytes memoryBytes;
	
	public IntSequence(int count) {
		
		// http://stackoverflow.com/questions/30600621/java-unsafe-storefence-documentation-wrong
		// NativeBytesStore<Void>.of(...)
//	    long address = memory.allocate(capacity);
//	    if (zeroOut || capacity < MEMORY_MAPPED_SIZE) {
//	    	memory.setMemory(address, capacity, (byte) 0);
//	        memory.storeFence();
//	    }
		
		this.capacity = getRecordSize(count);
		this.memoryBytes = new UnsafeBytes(memory, capacity);	    
		this.address = memoryBytes.freeAddress();
		this.cursor = new SequenceCursor(count);
	}
	
	public int getRecordSize(int count) {
		return count * elementBytes;
	}

	public final SequenceCursor cursor() {
		return cursor;
	}

	public final int get(final int index) {
		return memory.readInt(address+index*elementBytes);
	}
	

	public void set(int index, int value) {
		memory.writeInt(address+index*elementBytes, value);		
	}
	
	public void forEachForward(final IntConsumer action) {
//		int idx = -1;
//		while(++idx < limit)
//			action.accept(memory.readInt(address+(idx<<2)));
		
		for(int idx = 0; idx < capacity/elementBytes; idx++)
			action.accept(memory.readInt(address+(idx<<2)));
		
//		for(int idx = 0; idx < capacity/elementBytes; idx++)
//			action.accept(memory.readInt(address+(idx*elementBytes)));
		
//		for(int idx = 0; idx < capacity; idx+=elementBytes)
//			action.accept(memory.readInt(address+idx));
	}
	
	public final class SequenceCursor implements IntCursor {

		protected int index;
		protected final int limit;
		
		
		public SequenceCursor(int capacity) {
			this.index = -1;
			this.limit = capacity;
		}
		
		@Override
		public boolean moveNext() {
			return (++index < limit);
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
		}

		@Override
		public void forEachForward(final IntConsumer action) {
//			int idx = -1;
//			while(++idx < limit)
//				action.accept(memory.readInt(address+(idx<<2)));
			
//			for(int idx = 0; idx < limit; idx++)
//				action.accept(memory.readInt(address+(idx<<2)));
			
//			for(int idx = 0; idx < limit; idx++)
//				action.accept(memory.readInt(address+(idx*elementBytes)));
			
			for(int idx = 0; idx < limit*elementBytes; idx+=elementBytes)
				action.accept(memory.readInt(address+idx));
		}
		
		/**
		 * Für ByteSequence interessant
		 */
//		@Override
//		public void forEachForward(final IntConsumer action) {
//			for (int idx = 0; idx < capacity; idx++)
//				action.accept(memory.readByte(address+idx));
//		}

		@Override
		public int elem() {
			return memory.readInt(address+index*elementBytes);
		}
		
		public void set(final int value) {
			memory.writeInt(address+index*elementBytes, value);
		}
		
		public void reset() {
			this.index = -1;
		}
	}

	public void release() {
		memoryBytes.release();
	}
}
