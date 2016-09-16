
package net.wpm.reflectasm;

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
     * <p/>
     * If the underlying class is a inner (non-static nested) class, a new instance will be created using <code>null</code> as the
     * this$0 synthetic reference. The instantiated object will work as long as it actually don't use any member variable or method
     * fron the enclosing instance.
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
