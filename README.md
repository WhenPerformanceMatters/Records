## Introduction

Records is an open-source Java framework, which improves the memory object layout of the JVM. Relying heavily on sun.misc.Unsafe to store data off heap, it is designed for high performance applications. The new layout has features similar to C-structs, like unions and allocating continuous memory for multiple Java objects. 

At the same time Records provides a convenient API and keeps the Java OOP feeling. Depending on the application a speed up and reduction in memory of factor two can be archived. In case of projects with a lot of parallel process the scalability gets improved.
