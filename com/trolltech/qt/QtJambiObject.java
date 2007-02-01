/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.qt;

import com.trolltech.qt.QNativePointer;
import java.lang.reflect.*;

/**
 * The super class of all class types in Qt. Loading this class or any
 * of its subclasses will imply a dependency on both the Qt Jambi
 * library and the Qt libraries.
 */
public abstract class QtJambiObject extends QSignalEmitter implements QtJambiInterface
{
    static {
    	QtJambi_LibraryInitializer.init();
    }
    
    protected static class QPrivateConstructor { }

    public QtJambiObject()
    {
        /* intentionally empty */
    }

    public QtJambiObject(QPrivateConstructor p)
    {
        /* intentionally empty */
    }
    
    /**
     * Called either as the native resources that belong to the object are being
     * cleaned up or directly before the object is finalized. Reimplement this
     * function to do clean up when the object is destroyed. The function
     * will never be called more than once per object, and the object is 
     * guaranteed to be unusable after this function has returned. The default
     * implementation does nothing.
     */
    protected void disposed() 
    {
        /* intentionally empty */
    }

    public String tr(String str) { return str; }

    /**
     * Called before the java object is removed by the garbage collector. As the 
     * native resources belonging to an object may be cleaned up prior to the 
     * call of this function, it has been set as final. Reimplement disposed() instead,
     * which will be called either as the native resources are being removed
     * or just before the object is finalized, whichever happens first.
     */
    protected final native void finalize();

    /**
     * Explicitly removes the native resources held by the
     * object. Note that though this method does not guarantee that
     * the object will be garbage collected, it is not safe to
     * reference the object after it has been disposed.
     */
    public final native void dispose();


    /**
     * This is an internal function. Calling it can have unexpected results.  
     * 
     * Disables garbage collection for this object. This should be
     * used when objects created in java are passed to C++ functions
     * that take ownership of the objects. Both the Java and C++ part
     * of the object will then be cleaned up by C++.
     */
    public final native void disableGarbageCollection();
    
    /**
     * This is an internal function. Calling it can have unexpected results.  
     * 
     * Reenables garbage collection for this object. Should be used
     * on objects for which disableGarbageCollection() has previously
     * been called. After calling this function, the object ownership will be
     * reset to default.
     */
    public final native void reenableGarbageCollection();
    
    /**
     * This is an internal function. Calling it can have unexpected results.
     * 
     * Forces Java ownership of both the Java object and its C++ resources.
     * The C++ resources will be cleaned up when the Java object is finalized.
     */
    public final native void setJavaOwnership();
    

    /**
     * Internal function which fetches a wrapper around the pointer to
     * the native resources held by this object.
     * @return A QNativePointer object for the current object.
     */
    public final native QNativePointer nativePointer();

    /**
     * Internal function which fetches the native id of the current
     * object.
     * @return A long value which uniquely define the native resources
     * held by this object during their life time.
     */
    public final long nativeId() { return native__id; }
    
    /**
     * In certain, uncommon cases, the native resources of a QtJambiObject object may
     * be out of sync with its class. In such cases this method can be
     * used to reassign the native resources to an object of another
     * class. Take special care when using this method, as it has
     * limited type safety and may cause crashes when used in the wrong way. Note
     * also that as the returned object "steals" the native resources
     * held by the original object, the original object will not be
     * usable after a call to this function. Invoking a method on the
     * original object may result in an exception being raised. If an
     * exception is raised, it is safe to assume that the original
     * object is still valid. If the object is already of the type
     * specified by clazz, then a reference to the object itself is
     * returned.
     *
     * @param object The original object which holds the native
     * resources. This object will be considered unusable after the
     * call.
     * @param clazz The class of the new object. The class must be a
     * subclass of QtJambiObject and you must not rely on any constructors
     * being invoked upon construction of the object.
     * @return An object of the specified type which owns the
     * resources previously held by object.
     * @throws ClassCastException If the class of object is unrelated
     * to clazz, or if clazz is an unsupported class type.
     * @throws InstantiationException If clazz cannot be instantiated
     */
    public static QtJambiObject reassignNativeResources(QtJambiObject object, Class<? extends QtJambiObject> clazz)
        throws InstantiationException
    {
        if (!object.getClass().isAssignableFrom(clazz)) {
            throw new ClassCastException("The object '" + object.toString() + "' (class: '" + object.getClass().getName() + "') " 
                                         + "must be of class '" + clazz.getName() + "' or one of its superclasses.");
        }

        if (object.getClass().equals(clazz))
            return object;

        if (object.native__id == 0)
            throw new QNoNativeResourcesException("The object '" + object.toString() + "' does not have native resources.");

        Constructor<? extends QtJambiObject> c;
        try {
            c = clazz.getDeclaredConstructor(QPrivateConstructor.class);
        } catch (NoSuchMethodException e) {
            ClassCastException new_e = new ClassCastException("The class '" + clazz.getName() + "' must be of a generated type.");
            new_e.initCause(e);
            throw new_e;
        }

        long nativeId = object.native__id;
        object.native__id = 0;
        return __qt_reassignLink(nativeId, clazz, c);
    }

    // The constructor must take a single object reference as its
    // parameter and accept null. Basically, it should be the
    // QPrivateConstructor-constructor.
    private static native QtJambiObject __qt_reassignLink(long newNativeId, Class cls, Constructor constructor);

    private long native__id = 0;
}
