package net.wpm.record;


import net.wpm.record.RecordAdapter;
import net.wpm.record.RecordView;
import net.wpm.record.Records;

/**
 * A class to access some protected methods of the Records class.
 * 
 * @author Nico Hezel
 */
public class OpenRecords extends Records {
	
	public static final <B> RecordAdapter<B> getRecordAdapterImpl(int blueprintId) {
		return Records.getRecordAdapter(blueprintId);
	}

	public static <B> int register(Class<B> blueprint, Class<? extends RecordView> recordViewClass, int expectedElements) {
		
		// creates a new record adapter
		RecordAdapter<B> recordAdapter = new RecordAdapter<>(blueprint, recordViewClass);
		
		// register the adapter and return the new blueprint id
		return register(recordAdapter);
	}

	public static <B> int registerAdapter(final RecordAdapter<B> recordAdapter) {
		return Records.register(recordAdapter);
	}
}
