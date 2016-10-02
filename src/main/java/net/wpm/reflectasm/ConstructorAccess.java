
package net.wpm.reflectasm;

/**
 * Copyright (c) 2008, Nathan Sweet
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *  3. Neither the name of Esoteric Software nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ---------------------------
 *
 *
 * This is a special version of the ReflectASM library.
 * https://github.com/EsotericSoftware/reflectasm/issues/24
 * 
 * It is extended to meet the needs of Java Records but follows their license agreement:
 * https://github.com/EsotericSoftware/reflectasm/blob/master/license.txt
 * 
 * 
 * @author Nico Hezel
 */
public class ConstructorAccess<T> {
	public final ClassAccess classAccess;

    @Override
    public String toString() {
        return classAccess.toString();
    }

    protected ConstructorAccess(ClassAccess classAccess) {
        this.classAccess = classAccess;
    }

    public boolean isNonStaticMemberClass () {
		return classAccess.isNonStaticMemberClass();
	}

    /**
     * Constructor for top-level classes and static nested classes.
     * 
     * If the underlying class is a inner (non-static nested) class, a new instance will be created using <code>null</code> as the
     * this synthetic reference. The instantiated object will work as long as it actually don't use any member variable or method
     * from the enclosing instance.
     */
    @SuppressWarnings("unchecked")
    public T newInstance() {
        return (T) classAccess.newInstance();
    }

    /**
     * Constructor for inner classes (non-static nested classes).
     *
     * @param enclosingInstance The instance of the enclosing type to which this inner instance is related to (assigned to its
     *                          synthetic this$0 field).
     */
    @SuppressWarnings("unchecked")
    public T newInstance(Object enclosingInstance) {
        return (T) classAccess.newInstance(0, enclosingInstance);
    }

    static public <T> ConstructorAccess<T> get (Class<T> type) {
        return new ConstructorAccess<T>(ClassAccess.get(type));
	}

    static public <T> ConstructorAccess<T> get (ClassAccess access) {
		return new ConstructorAccess<T>(access);
	}
}
