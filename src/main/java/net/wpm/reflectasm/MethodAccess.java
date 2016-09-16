
package net.wpm.reflectasm;

import java.lang.annotation.Annotation;

/**
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
