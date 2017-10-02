package net.wpm.record.collection;

import java.util.Iterator;
import java.util.RandomAccess;
import java.util.function.Consumer;

import net.wpm.record.RecordAdapter;
import net.wpm.record.RecordView;

/**
 * Works like a Java array. All elements are placed consecutive in memory. 
 * 
 * @author Nico Hezel
 */
public class RecordSequence<B> implements Iterable<B>, RandomAccess {

	protected final RecordView view;	// record view
	
	protected long fromAddress;			// starting address of the sequence
	protected final int recordSize;		// record size in bytes	
	protected final int count;			// amount of records
	
	public RecordSequence(final RecordAdapter<B> adapter, final long fromAddress, final int count) {
		this.view = adapter.newInstance();
		
		this.fromAddress = fromAddress;
		this.recordSize = view.getRecordSize();		
		this.count = count;
	}
	
	/**
	 * Set a new address to reuse this sequence view
	 * 
	 * @param fromAddress
	 */
	public void setAddress(final long fromAddress) {
		this.fromAddress = fromAddress;
	}

	/**
	 * Amount of records in this sequence
	 * 
	 * costs 0C 0B 0A 0P 0M 0N
	 * @return int count
	 */
	public int size() {
		return count;
	}
	
	/**
	 * Get the element at index. Creates a new record view object.
	 * 
	 * costs 2C 0B 0A 0P 0M 1N
	 * @param index
	 * @return Record extends RecordView
	 */
	@SuppressWarnings("unchecked")
	public B get(final int index) {
		view.setRecordId(fromAddress + index * recordSize);
		return (B)view;
	}
	
	/**
	 * Set the content of the element at the given index. 
	 * Makes a copy of the content and stores is in the sequence.
	 * 
	 * costs 1C 0B 0A 2P 0M 0N
	 * @param index
	 * @param value
	 */
	public void set(final int index, final B value) {
		final RecordView fromValue = ((RecordView)value);
		
		// use the memory access of the value
		long copyFromAddress = fromValue.getRecordId();
		long copyToAddress = fromAddress + index * recordSize;
		fromValue.getMemoryAccess().copy(copyFromAddress, copyToAddress, recordSize);
	}

	/**
	 * Get the element at index and reuse the record view
	 * 
	 * costs 1C 0B 0A 0P 0M 0N
	 * @param index
	 * @param reuse
	 * @return Record extends RecordView
	 */
	public B get(final int index, final B reuse) {
		((RecordView)reuse).setRecordId(fromAddress + index * recordSize);
		return reuse;
	}
	
	/**
	 * costs 3C ?B 0A ?P 0M 1N
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void forEach(final Consumer<? super B> action) {	
		long address = fromAddress;
		for (int index = 0; index < count; index++, address += recordSize) {
			view.setRecordId(address);
			action.accept((B)view);
		}
	}

	/**
	 * costs 3C 0B 0A 0P 0M 2N
	 */
	@Override
	public final Iterator<B> iterator() {
		return new RecordIterator<B>(view, fromAddress, fromAddress + count * recordSize);
	}
}
