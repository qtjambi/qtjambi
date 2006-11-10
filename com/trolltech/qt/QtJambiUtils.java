/****************************************************************************
**en
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

import java.lang.reflect.*;
import java.util.*;
import com.trolltech.qt.core.*;

public class QtJambiUtils {

    static private final boolean DEBUG_LOTS = false;
            
    /**
     * Returns wether a class is an actual implementor of a function or
     * if the function is simply a shell around a native implementation
     * provided by default by the Qt Jambi bindings. 
     * 
     * @param method The function to match.
     * @return wether the implements the function or not.
     */	
    public static boolean isImplementedInJava(Method method) {
        if (DEBUG_LOTS) {
            System.err.println("isImplementedInJava: " 
                               + method + " - "
                               + method.getDeclaringClass() + " - " 
                               + method.getDeclaringClass().getAnnotation(QtJambiInternalClass.class));
        }
        return method.getDeclaringClass().getAnnotation(QtJambiInternalClass.class) == null;        
    }

    
    /**
     * Returns the signature of the method m excluding the modifiers and the
     * return type.
     */
    private static String methodSignature(Method m) {
        Class params[] = m.getParameterTypes();
        StringBuilder s = new StringBuilder();
        s.append(m.getName()).append("(");
        for (int i = 0; i < params.length; ++i) {
            if (i != 0)
                s.append(",");
            s.append(params[i].getName());
        }
        s.append(")");
        return s.toString();
    }

    /**
     * Returns the field entry for all declared signales in o and its base
     * classes.
     */
    private static List<Field> findSignals(QObject o) {
        Class c = o.getClass();
        List<Field> fields = new ArrayList<Field>();
        while (c != null) {
            Field declared[] = c.getDeclaredFields();
            for (Field f : declared) {
                if (QtJambiInternal.isSignal(f.getType())) {
                    fields.add(f);
                }
            }
            c = c.getSuperclass();
        }
        return fields;
    }

    private static Class objectClass(Class cl) {
        if (cl == boolean.class)
            return java.lang.Boolean.class;
        if (cl == byte.class)
            return java.lang.Byte.class;
        if (cl == char.class)
            return java.lang.Character.class;
        if (cl == short.class)
            return java.lang.Short.class;
        if (cl == int.class)
            return java.lang.Integer.class;
        if (cl == long.class)
            return java.lang.Long.class;
        if (cl == float.class)
            return java.lang.Float.class;
        if (cl == double.class)
            return java.lang.Double.class;
        return cl;
    }

    /**
     * Compares the signatures and does a connect if the signatures match up.
     */
    private static void tryConnect(QObject receiver, Method method, QObject sender, Field signal) {
        Class params[] = method.getParameterTypes();
        Type type = signal.getGenericType();

        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type types[] = pt.getActualTypeArguments();

            // If the signal has too few arguments, we abort...
            if (types.length < params.length)
                return;

            for (int i = 0; i < params.length; ++i) {
                Class signal_type = (Class) types[i];
                Class param_type = params[i];

                if (signal_type.isPrimitive())
                    signal_type = objectClass(signal_type);

                if (param_type.isPrimitive())
                    param_type = objectClass(param_type);

                // Parameter types don't match.
                if (signal_type != param_type)
                    return;
            }
            
        } else if (params.length != 0) {
            throw new RuntimeException("Don't know how to autoconnect to: "
                    + signal.getDeclaringClass().getName() + "."
                    + signal.getName());
        }

        // Do the connection...
        Object signal_object = null;
        try {
            signal_object = signal.get(sender);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        boolean ok = ((QObject.AbstractSignal) signal_object).connect(receiver, methodSignature(method));
        if (!ok) {
            throw new RuntimeException("Autoconnection failed for: " 
                    + signal.getDeclaringClass().getName() + "."
                    + signal.getName() 
                    + " to " + receiver.getClass().getName() + "." 
                    + methodSignature(method));
        }
    }

    /**
     * 
     */
    public static void connectSlotsByName(QObject object) {
        List<QObject> children = object.findChildren();
        Class objectClass = object.getClass();
        while (objectClass != null) {
            Method methods[] = objectClass.getDeclaredMethods();
            for (QObject child : children) {
                String prefix = "on_" + child.objectName() + "_";
                List<Field> fields = findSignals(child);
                for (Field f : fields) {
                    String slot_name = prefix + f.getName();
                    for (int i = 0; i < methods.length; ++i) {
                        if (methods[i].getName().equals(slot_name)) {
                            tryConnect(object, methods[i], child, f);
                        }
                    }
                }
            }
            objectClass = objectClass.getSuperclass();
        }
    }
    
    public static void connect(QObject.AbstractSignal signal, String slotSignature, QObject ... objects) {
        for (QObject o : objects) {
            signal.connect(o, slotSignature);
        }
    }
    
    public static void addSearchPathForResourceEngine(String path)
    {
        QClassPathEngine.addSearchPath(path);
    }
    
    public static void removeSearchPathForResourceEngine(String path)
    {
        QClassPathEngine.removeSearchPath(path);
    }
}
