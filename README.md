Records
=======

An open-source Java library to improve the memory object layout of the JVM. Relying heavily on sun.misc.Unsafe to store data off heap, it is designed for high performance applications. The new layout has features similar to C-structs, like unions and allocating consecutive memory for multiple Java objects. 

At the same time Records provides a convenient API and keeps the Java OOP feeling. Depending on the application a speed up and memory reduction of factor two can be archived. In case of projects with a lot of parallel process the scalability gets improved.

Quick start
=======
Add the following dependencies in your Maven `pom.xml`:
```xml
  <dependencies>
    <dependency>
      <groupId>net.whenperformancematters</groupId>
      <artifactId>records</artifactId>
      <version>1.0.0</version>
    </dependency>
  <dependencies>
```

Or to your Gradle build script:
```groovy
dependencies {
    compile 'net.whenperformancematters:records:1.0.0'
}
```

Example
=======

The best place to learn about this library is to look at the [examples] (src/test/java/net/wpm/record/samples). In generell an interface defining getter/setter methods is all it needs. Records will build the necessary classes at runtime implementing the interface and provides an API to instantiate an object of them. 

  
Instead of a POJO class it is sufficient to just define an interface.
```java
public interface Foo {
	public int getNumber();
	public void setNumber(int number);
	public float getFraction();
	public void setFraction(float fraction);
}
```

Creating an instance is done through the Records API.
```java
public static void main(String[] args) {
		// create a record implementing the Foo interface
		Foo obj = Records.of(Foo.class);
		
		// writes the number 5 in the record
		obj.setNumber(5);
		
		// prints -> "{Number: 5, Fraction: 0.0}"
		System.out.println(obj);
}
```

Limitations
=======

Right now Records has some limitations which might be crucial for other project. We nevertheless released a version 1.0.0 of it. With the knowledge about the shortcomings we still use Records in many closed source projects and think it is ready to enter the lime light.

##### Records can not be deleted
The current API allows the creation single records and record sequences, but lacks ability to delete those. It is only possible to release all the memory allocated by Records and start over again. Developer are advised to allocate the POJO like objects once at program start and reuse their memory afterwards. Not affected are the record views, they can be created and deleted at will.

##### References to objects and records
A record can not hold a reference to another Java object or record. There exists a set-record method which works for other records but fails upon reciving a Java object. This method will only copy the content from one record to another instead of storing a reference. While this improves the performance for later access it still is counterintuitive to the normal Java behaviour. 