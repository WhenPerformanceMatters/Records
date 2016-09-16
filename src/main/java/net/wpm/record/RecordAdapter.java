package net.wpm.record;

import net.wpm.record.blueprint.BlueprintClass;
import net.wpm.record.blueprint.BlueprintInspector;
import net.wpm.record.bytecode.RecordClassGenerator;
import net.wpm.record.bytes.MemoryAccess;
import net.wpm.record.collection.RecordSequence;
import net.wpm.reflectasm.ClassAccess;
import net.wpm.reflectasm.ConstructorAccess;
import net.wpm.reflectasm.FieldAccess;
import net.wpm.reflectasm.MethodAccess;

/**
 * The RecordAdapter has access to all methods of the RecordView.
 * It can also create new views and allocate memory for additional records.
 * 
 * TODO is "Class<? extends B> recordViewClass" faster?
 * 
 * @author Nico Hezel
 *
 * @param <B>
 */
public final class RecordAdapter<B> {

	// id of the blueprint 
	protected int blueprintId = 0;
	
	// blueprint defining the record structure
	protected final Class<B> blueprint;	
	
	// record view class to access the data of a record
	protected final Class<? extends RecordView> recordViewClass;	
	protected final FieldAccess recordViewClassFieldAccess;
	protected final MethodAccess recordViewClassMethodAccess;	
	protected final ConstructorAccess<? extends RecordView> recordViewClassConstructorAccess;
	
	// size in bytes for the data of a record
	protected final int recordSize;
	
	// access to memory
	protected final MemoryAccess memoryAccess; 
	
	/**
	 * Analyze the blueprint and constructs a record view class.
	 * 
	 * @param blueprint
	 */
	public RecordAdapter(final Class<B> blueprint) {
		this(blueprint, generateClass(blueprint));
	}
	
	/**
	 * The record view class needs implements all methods of the blueprint.
	 * 
	 * @param blueprint
	 * @param recordViewClass
	 */
	public RecordAdapter(final Class<B> blueprint, final Class<? extends RecordView> recordViewClass) {
		this.blueprint = blueprint;
		this.recordViewClass = recordViewClass;		

		// get access to the fields, methods and constructor of the recordViewClass
		ClassAccess classAccess = ClassAccess.get(recordViewClass);
		recordViewClassFieldAccess = FieldAccess.get(classAccess);
		recordViewClassMethodAccess = MethodAccess.get(classAccess);
		recordViewClassConstructorAccess = ConstructorAccess.get(classAccess);
				
		// size in bytes of a record
		recordSize = (Integer) recordViewClassFieldAccess.get(null, "recordSize");
			
		// create the underlying byte buffer
		memoryAccess = MemoryAccess.build();
		
		// inform the recordView about the adapter and its buffer 
		recordViewClassFieldAccess.set(null, "recordAdapter", this);
		recordViewClassFieldAccess.set(null, "memoryAccess", memoryAccess);
	}

	/**
	 * Constructs a new record view class implementing the blueprint methods 
	 * and providing access to the data of a record.
	 * 
	 * @param blueprint
	 * @return
	 */
	protected static final <B> Class<? extends RecordView> generateClass(final Class<B> blueprint) {	
		final BlueprintInspector inspector = new BlueprintInspector(blueprint);
		final BlueprintClass blueprintClass = inspector.getBlueprintClass();
		final RecordClassGenerator generator = new RecordClassGenerator(blueprintClass);
		final Class<RecordView> recordViewClass = generator.construct();
		System.out.println("Generated " + recordViewClass);
		return recordViewClass;
	}
	
	/**
	 * Get a record id pointing to an empty record
	 * 
	 * @return
	 */
	protected final long nextId() {			
		return memoryAccess.reserve(recordSize);
	}	

	/**
	 * Create a new array
	 * 
	 * @param count
	 * @return
	 */
	public final RecordSequence<B> array(int count) {
		final long fromAddress =  memoryAccess.reserve(recordSize * count);				
		return new RecordSequence<B>(this, fromAddress, count);
	}
	
	
	/**
	 * Create a new record view, pointing no-where
	 * 
	 * @return
	 */
	public final RecordView newInstance() {
		return recordViewClassConstructorAccess.newInstance();
	}
	
	
	/**
	 * Create a new record view pointing to an empty record
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final B create() {
		final RecordView recordView = newInstance();
		recordView.setRecordId(nextId());
		return (B) recordView;
	}
	
	/**
	 * Reuse a record view but point to an empty record.
	 * 
	 * @param reuse
	 * @return
	 */
	public final RecordView create(final RecordView reuse) {
		reuse.setRecordId(nextId());
		return reuse;
	}

	/**
	 * Create a new record, pointing to the data of another record.
	 * 
	 * @param recordId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public final B view(final long recordId) {
		final RecordView recordView = newInstance();
		recordView.setRecordId(recordId);
		return (B)recordView;
	}
	
	/**
	 * Create a new record view, pointing to the data of the given record.
	 * 
	 * @param record
	 * @return
	 */
	public B view(final B record) {
		final long recordId = ((RecordView)record).getRecordId();
		return view(recordId);
	}
	
	/**
	 * Reuse an existing record, pointing to the data of another record. 
	 * 
	 * @param pojo
	 * @param recordId
	 * @return
	 */
	protected final B reuse(final B recordView, final long recordId) {
		((RecordView)recordView).setRecordId(recordId);
		return recordView;
	}
	
	/**
	 * Create a copy of the record and return the copy.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public B copy(final B record) {
		final long recordId = nextId();
		final RecordView copy = newInstance();
		copy.setRecordId(recordId);
		
		final long fromId = ((RecordView)record).getRecordId();
		memoryAccess.copy(fromId, recordId, recordSize);
		
		return (B)copy;
	}
	
	/**
	 * Copy the data of one record to another one
	 * 
	 * @param from
	 * @param to
	 */
	public void copy(final B from, final B to) {
		final long fromId = ((RecordView)from).getRecordId();
		final long toId = ((RecordView)to).getRecordId();
		memoryAccess.copy(fromId, toId, recordSize);
	}
	
	// -----------------------------------------------------------------------------------------
	// -------------------------------- getter and setter --------------------------------------
	// -----------------------------------------------------------------------------------------
	
	public final int getBlueprintId() {
		return blueprintId;
	}

	public final void setBlueprintId(final int blueprintId) {
		this.blueprintId = blueprintId;
		recordViewClassFieldAccess.set(null, "blueprintId", blueprintId);
	}

	public final Class<B> getBlueprint() {
		return blueprint;
	}

	public final void releaseAll() {
		memoryAccess.releaseAll();
	}

	public final int getRecordSize() {
		return recordSize;
	}
}