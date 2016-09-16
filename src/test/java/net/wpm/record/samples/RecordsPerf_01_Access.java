package net.wpm.record.samples;

import java.io.IOException;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.wpm.record.OpenRecords;
import net.wpm.record.RecordAdapter;
import net.wpm.record.Records;
import net.wpm.record.annotation.Array;
import net.wpm.record.annotation.Integer;
import net.wpm.record.annotation.Text;
import net.wpm.record.annotation.Text.Encoding;
import net.wpm.record.blueprint.TestPojo;
import net.wpm.record.bytecode.RecordClassGenerator;
import net.wpm.record.bytes.MemoryAccess;
import net.wpm.record.collection.RecordSequence;

public class RecordsPerf_01_Access {

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException {

		// Zugriff auf die Structs ist möglich über die Structs Klasse oder direkt dem Adapter
		RecordAdapter<Foo> structAdapter = new RecordAdapter<>(Foo.class);
		int classId = OpenRecords.registerAdapter(structAdapter);
		
		// erstelle ein neues Objekt
		Foo obj = Records.create(Foo.class);
		Foo otherObj = Records.create(classId); // faster
		Foo anotherObj = structAdapter.create(); // the fastest
		
		// die interne Id vom Objekt
		long id = Records.id(obj);
		
		// zwei Referenzen zum selben Objekt
		Foo sameObj = Records.view(Foo.class, id);
		Foo otherSameObj = Records.view(classId, id); // faster
		Foo anotherSameObj = structAdapter.view(id); // the fastest
		
		// reuse vom selben objekt
		long[] objId = new long[] { id+structAdapter.getRecordSize()*2, id+0, id+structAdapter.getRecordSize()*5 };
		long sum = 0;
		for (int i = 0; i < objId.length; i++) {
			obj = Records.view(Foo.class, objId[i]);
			obj = Records.view(obj, objId[i]); // faster
			
			// mach etwas
			obj.setInt1(i);
			sum += obj.getInt1();
		}
		System.out.println("sum "+sum);
		
		// oder gleich als liste
		sum = 0;
		RecordSequence<Foo> fooList = Records.array(Foo.class, 10);
		for (Foo foo : fooList) {
			sum += foo.getInt1();
		}
	}
	
	private static interface Foo {
		
		public byte getByte();
		public void setByte(byte number);
		
		public int getInt1();
		public void setInt1(int number);

		public int getInt2();
		public void setInt2(int number);
		
		public long getLong();
		public void setLong(long number);
	}
}