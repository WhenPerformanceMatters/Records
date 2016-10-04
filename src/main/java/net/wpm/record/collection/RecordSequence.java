package net.wpm.record.collection;

import java.util.Iterator;
import java.util.function.Consumer;

import net.wpm.record.RecordAdapter;
import net.wpm.record.RecordView;

/**
 * Works like a Java array. All elements are placed consecutive in memory. 
 * 
 * @author Nico Hezel
 */
public class RecordSequence<B> implements Iterable<B> {

	protected final RecordAdapter<B> adapter;
	protected final int recordSize;		// record size in bytes	
	protected final long fromAddress;	// starting address of the sequence
	protected final long toAddress;		// end address
	protected final int count;			// amount of records
	
	public RecordSequence(final RecordAdapter<B> adapter, final long fromAddress, final int count) {
		this.adapter = adapter;
		this.recordSize = adapter.getRecordSize();
		
		this.fromAddress = fromAddress;
		this.toAddress = fromAddress + recordSize * count;
		this.count = count;
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
	public B get(final int index) {
		return adapter.view(fromAddress + index * recordSize);
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
	@Override
	public void forEach(Consumer<? super B> action) {
		
		final B record = adapter.view(0);
		final RecordView recordView = ((RecordView)record);
		
		long address = fromAddress;
		for (int i = 0; i < count; i++) {
			recordView.setRecordId(address += recordSize);
			action.accept(record);
		}
	}

	/**
	 * costs 3C 0B 0A 0P 0M 2N
	 */
	@Override
	public final Iterator<B> iterator() {
		return new Itr(adapter);
	}

	/**
	 * A iterator using reusing a single record view to access all records.
	 * 
	 * @author Nico Hezel
	 */
	protected final class Itr implements Iterator<B> {
		
		protected long address = 0;		// address in memory	
		
		// both records are identical, safes casting
		protected final RecordView reuseRecordView;
		protected final B reuseRecord;
		
		/**
		 * costs 3C 0B 0A 0P 0M 1N
		 * @param adapter
		 */
		public Itr(final RecordAdapter<B> adapter) {						
			this.reuseRecord = adapter.view(0);
			this.reuseRecordView = (RecordView) reuseRecord;
			this.address = fromAddress - recordSize;
		}

		/**
		 * costs 0C 0B 0A 0P 0M 0N
		 */
		@Override
		public final boolean hasNext() {
			return ((address += recordSize) < toAddress);
		}

		/**
		 * costs 0C 0B 0A 0P 0M 0N
		 */
		@Override
		public final B next() {
			reuseRecordView.setRecordId(address);
			return reuseRecord;
		}
	}
}
