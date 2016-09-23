package net.wpm.record;

import net.wpm.record.bytes.MemoryAccess;

/**
 * TODO if the the content of RecordView gets generated with bytecode Records could extend abstract classes, but performance might degenerate
 * TODO HashCode should be calculated with a fast hashing strategies
 *  http://vanillajava.blogspot.de/2015/08/comparing-hashing-strategies.html
 *  http://vanillajava.blogspot.de/2015/09/an-introduction-to-optimising-hashing.html
 *  
 * @author Nico Hezel
 */
public abstract class RecordView {
	
	/**
	 * Id of the blueprint
	 * 
	 * @return blueprint id
	 */
	public abstract int getBlueprintId();	
	
	/**
	 * Access to the native memory
	 * 
	 * @return MemoryAccess
	 */
	public abstract MemoryAccess getMemoryAccess();		
	
	/**
	 * Record adapter to access all records of this type
	 * 
	 * @return RecordAdapter
	 */
	public abstract RecordAdapter<?> getRecordAdapter();	
	
	/**
	 * Size in bytes of the record
	 * 
	 * @return record size in bytes
	 */
	public abstract int getRecordSize();	

	/**
	 * Native memory address of the record
	 */
	protected long address;
	
	/**
	 * Get the record id pointing to a record
	 * 
	 * @return record id
	 */
	public final long getRecordId() {
		return address;
	}

	/**
	 * Set the record id pointing to some underlying data.
	 * 
	 * @param recordId
	 */
	public final void setRecordId(final long recordId) {
		address = recordId;
	}
}
