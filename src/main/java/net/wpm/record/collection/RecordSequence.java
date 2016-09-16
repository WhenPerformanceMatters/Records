package net.wpm.record.collection;

import java.util.Iterator;
import java.util.function.Consumer;

import net.wpm.record.RecordAdapter;
import net.wpm.record.RecordView;

/**
 * @author Nico Hezel
 */
public class RecordSequence<B> implements Iterable<B> {

	protected final RecordAdapter<B> adapter;
	protected final int recordSize;		// record size in bytes
	
	protected final long fromAddress;	// 
	protected final long toAddress;		//
	protected final int count;			// element count
	
	public RecordSequence(final RecordAdapter<B> adapter, final long fromAddress, final int count) {
		this.adapter = adapter;
		this.recordSize = adapter.getRecordSize();
		
		this.fromAddress = fromAddress;
		this.toAddress = fromAddress + recordSize * count;
		this.count = count;
	}

	public int size() {
		return count;
	}
	
	public B get(final int index) {
		return adapter.view(fromAddress + index * recordSize);
	}

	/**
	 * Get the element at index and reuse the record view
	 * 
	 * @param index
	 * @param record
	 * @return
	 */
	public B get(final int index, final B record) {
		((RecordView)record).setRecordId(fromAddress + index * recordSize);
		return record;
	}
	
	
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

	@Override
	public final Iterator<B> iterator() {
		return new Itr(adapter);
	}

	protected final class Itr implements Iterator<B> {
		
		protected long address = 0;		// address in memory	
		
		// beide Objekte sind identisch, spart das casting
		protected final RecordView reuseRecordView;
		protected final B reuseRecord;
		
		public Itr(final RecordAdapter<B> adapter) {						
			this.reuseRecord = adapter.view(0);
			this.reuseRecordView = (RecordView) reuseRecord;
			this.address = fromAddress - recordSize;
		}

		// beste megamorphic variante
		@Override
		public final boolean hasNext() {
			return ((address += recordSize) < toAddress);
		}

		@Override
		public final B next() {
			reuseRecordView.setRecordId(address);
			return reuseRecord;
		}
	}
}
