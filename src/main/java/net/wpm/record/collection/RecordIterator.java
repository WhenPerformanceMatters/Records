package net.wpm.record.collection;

import java.util.Iterator;

import net.wpm.record.RecordView;

/**
 * A iterator reusing a single record view to access all records.
 * 
 * @author Nico Hezel
 */
public final class RecordIterator<B> implements Iterator<B> {

	// both records are identical, safes casting
	protected final RecordView recordView;
	
	protected final long toAddress;		// end address
	protected final int recordSize;		// record size in bytes	
	protected long address = 0;			// address in memory	
	
	/**
	 * costs 3C 0B 0A 0P 0M 1N 
	 * @param recordView
	 * @param fromAddress
	 * @param toAddress
	 */
	public RecordIterator(final RecordView recordView, final long fromAddress, final long toAddress) {						
		this.recordView = recordView;
		
		this.toAddress = toAddress;
		this.recordSize = recordView.getRecordSize();
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
	@SuppressWarnings("unchecked")
	@Override
	public final B next() {
		recordView.setRecordId(address);
		return (B)recordView;
	}
}
