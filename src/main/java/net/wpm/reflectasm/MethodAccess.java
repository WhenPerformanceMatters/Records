
package net.wpm.reflectasm;

import java.lang.annotation.Annotation;

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
public class MethodAccess {
    public final ClassAccess classAccess;

    @Override
    public String toString() {
        return classAccess.toString();
    }

    protected MethodAccess(ClassAccess classAccess) {
        this.classAccess = classAccess;
    }

    public Object invoke(Object object, int methodIndex, Object... args) {
        return classAccess.invoke(object, methodIndex, args);
    }

    /** Invokes the method with the specified name and the specified param types. */
	public Object invoke (Object object, String methodName, Class<?>[] paramTypes, Object... args) {
		return invoke(object, getIndex(methodName, paramTypes), args);
	}

	/** Invokes the first method with the specified name and the specified number of arguments. */
	public Object invoke (Object object, String methodName, Object... args) {
		return invoke(object, getIndex(methodName, args == null ? 0 : args.length), args);
	}

	/** Returns the index of the first method with the specified name. */
	public int getIndex (String methodName) {
        return classAccess.indexOfMethod(methodName);
	}

	/** Returns the index of the first method with the specified name and param types. */
	public int getIndex (String methodName, Class<?>... paramTypes) {
        return classAccess.indexOfMethod(methodName, paramTypes);
	}

	/** Returns the index of the first method with the specified name and the specified number of arguments. */
	public int getIndex (String methodName, int paramsCount) {
        return classAccess.indexOfMethod(methodName, paramsCount);
	}

	public int[] getMethodModifiers() {
		return classAccess.getMethodModifiers();
	}
	 
	public String[] getMethodNames() {
		return classAccess.getMethodNames();
	}
	
	public Annotation[][] getMethodAnnotations() {
		return classAccess.getMethodAnnotations();
	}

	public Class<?>[][] getParameterTypes() {
		return classAccess.getParameterTypes();
	}

	public Class<?>[] getReturnTypes() {
		return classAccess.getReturnTypes();
	}

	static public MethodAccess get(Class<?> type) {
        return new MethodAccess(ClassAccess.get(type));
	}
	
	static public MethodAccess get (ClassAccess access) {
		return new MethodAccess(access);
	}

	public int size() {
		return classAccess.getMethodCount();
	}
}
