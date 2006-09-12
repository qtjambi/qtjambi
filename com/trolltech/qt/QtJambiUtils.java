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
import com.trolltech.qt.gui.*;

public class QtJambiUtils {

    static private final boolean DEBUG_LOTS = false;
    
    static private final boolean CHECK_NATIVE_SIGNATURES = Utilities.matchProperty("com.trolltech.qt.check-native-signatures");
    static private final boolean VERBOSE_VTABLE_JAVA = Utilities.matchProperty("com.trolltech.qt.verbose-vtable", "java");
    static private final boolean VERBOSE_VTABLE_CPP = Utilities.matchProperty("com.trolltech.qt.verbose-vtable", "cpp");
        
    /**
     * A silly function to help me find the actual class name...
     */
    private static String className(Class cl) {
        String name = cl.getName();
        int dot_pos = name.lastIndexOf('.');
        if (dot_pos < 0)
            return name;
        else
            return name.substring(dot_pos + 1);
    }
    
    private static String innerClassName(Class cl) {
        String name = className(cl);     
        int dollar_pos = name.indexOf('$');
        assert(dollar_pos >= 0);
        return name.substring(dollar_pos + 1);
    }

    /**
     * A silly function to help me remove Interface at the end of the name
     */
    private static String stripInterface(String name) {
        if (name.regionMatches(name.length() - 9, "Interface", 0, 9))
            return name.substring(0, name.length() - 9);
        return name;
    }

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
            System.err.println("\nQtJambiUtils.isImplementor(): " + method);
        }

        Class implementor = method.getDeclaringClass();

        StringBuilder name = new StringBuilder();
        name.append("__qt_").append(method.getName());

        Class args[] = method.getParameterTypes();

        int arg_count = args.length;

        if (!Modifier.isStatic(method.getModifiers()))
            ++arg_count;

        Class jni_args[] = new Class[arg_count];
        if (!Modifier.isStatic(method.getModifiers())) {
            jni_args[0] = long.class;
            if (DEBUG_LOTS)
                System.out.println("appending arg: " + jni_args[0]);
        }

        for (int i = 0; i < args.length; ++i) {
            name.append("_");

            if (args[i].isPrimitive() || args[i] == Object.class) {
                name.append(className(args[i]));
                jni_args[i + 1] = args[i];

                if (DEBUG_LOTS)
                    System.out.println(" - : " + className(args[i]) + ":" + jni_args[i + 1]);

            } else if (QtObject.class.isAssignableFrom(args[i])) {
                name.append(className(args[i]));
                jni_args[i + 1] = long.class;

                if (DEBUG_LOTS)
                    System.out.println(" - : " + className(args[i]) + ":" + jni_args[i + 1]);

            } else if (QtObjectInterface.class.isAssignableFrom(args[i])) {
                name.append(stripInterface(className(args[i])));
                jni_args[i + 1] = long.class;

                if (DEBUG_LOTS)
                    System.out.println(" - (if) : " + stripInterface(className(args[i])) + ":"
                            + jni_args[i + 1]);
                
            } else if (args[i].isEnum()) {
//                System.out.println("its an enum:" + args[i]);
                name.append(innerClassName(args[i]));
                jni_args[i + 1] = int.class;
                
            } else if (QFlags.class.isAssignableFrom(args[i])) {
//                System.out.println("its a flag..." + args[i]);
                name.append(innerClassName(args[i]));
                jni_args[i + 1] = int.class;

            } else if (args[i] == QNativePointer.class) {
                name.append("nativepointer");
                jni_args[i + 1] = args[i];

                if (DEBUG_LOTS)
                    System.out.println(" - : " + "nativepointer" + jni_args[i + 1]);

            } else if (args[i] == String.class) {
                name.append("String");
                jni_args[i + 1] = String.class;
                if (DEBUG_LOTS)
                    System.out.println(" - : String");

            } else if (args[i] == java.util.SortedMap.class) {
                name.append("map");
                jni_args[i + 1] = java.util.SortedMap.class;
                if (DEBUG_LOTS)
                    System.out.println(" - : Map");

            } else if (args[i] == java.util.List.class) {
                name.append("list");
                jni_args[i + 1] = java.util.List.class;
                if (DEBUG_LOTS)
                    System.out.println(" - : List");

            } else if (args[i] == QPair.class) {
                name.append("qpair");
                jni_args[i + 1] = QPair.class;
                if (DEBUG_LOTS)
                    System.out.println(" - : QPair");

            } else if (args[i] == QModelIndex.class) {
                name.append("QModelIndex");
                jni_args[i + 1] = QModelIndex.class;
                if (DEBUG_LOTS)
                    System.out.println(" - : QModelIndex");

            } else {
                throw new RuntimeException("Unhandled argument type: " + args[i]);
            }
        }

        String complete_name = name.toString();

        // A sanity check... Verify that we mangled the name correctly
        // by traversing the hierarchy and locating the class that
        // implements it. It its nowhere to be found, its a bug..
        if (CHECK_NATIVE_SIGNATURES) {
            boolean found_somewhere = false;
            Class base = implementor;
            while (base != null && !found_somewhere) {
                try {
                    base.getDeclaredMethod(complete_name, jni_args);
                    found_somewhere = true;
                } catch (Exception e) {
                }
                base = base.getSuperclass();
            }
            if (!found_somewhere) {
                System.err.println("(ERROR) Did not find function: " + complete_name);
                for (Class c : jni_args)
                    System.err.println(" - " + c);
                System.exit(0);
            }
        }

        boolean found = false;
        try {
            implementor.getDeclaredMethod(complete_name, jni_args);
            found = true;
        } catch (Exception e) {
        }

        if (VERBOSE_VTABLE_JAVA || VERBOSE_VTABLE_CPP) {            
            if (found && VERBOSE_VTABLE_CPP) {
                System.out.println(method.getName() + ": (C++)"
                        + "\n - " + method
                        + "\n - " + complete_name);
            } else if (!found && VERBOSE_VTABLE_JAVA) {
                System.out.println(method.getName() + ": (Java)"
                        + "\n - " + method
                        + "\n - " + complete_name);                
            }
        }

        return !found;
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
                if (QObject.isSignal(f.getType())) {
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
