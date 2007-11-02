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

import com.trolltech.qt.core.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * @exclude
 */
public class QtJambiInternal {

    static {
        QtJambi_LibraryInitializer.init();
    }

    public static final char SlotPrefix = '1';
    public static final char SignalPrefix = '2';

    public static void setupDefaultPluginPath() {
        try {
            String basePath = Utilities.filePathForClasses();
            QCoreApplication.addLibraryPath(basePath + File.separatorChar + "plugins");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  @exclude
     */
    static class QMetaCallEvent extends QEvent {

        public static final QEvent.Type MetaCallEventType = QEvent.Type
                .resolve(512);

        private QObject sender;

        public QMetaCallEvent(QSignalEmitter.AbstractSignal.Connection connection, QObject sender,
                Object... arguments) {
            super(MetaCallEventType);
            this.arguments = arguments;
            this.connection = connection;
            this.sender = sender;
        }

        public final Object[] getArguments() {
            return arguments;
        }

        public final void setArguments(Object[] arguments) {
            this.arguments = arguments;
        }

        public final QSignalEmitter.AbstractSignal.Connection getConnection() {
            return connection;
        }

        public final void setConnection(QSignalEmitter.AbstractSignal.Connection connection) {
            this.connection = connection;
        }

        final void execute() {
            boolean updateSender = sender != null;
            long oldSender = 0;
            QSignalEmitter oldEmitter = null;
            if (updateSender) {
                oldSender = QtJambiInternal.swapQObjectSender(((QObject) connection.receiver).nativeId(),
                                                               sender.nativeId(), true);
                oldEmitter = QSignalEmitter.currentSender.get();
                QSignalEmitter.currentSender.set(sender);
            }
            try {
                connection.slot.invoke(connection.receiver, arguments);
            } catch (IllegalAccessException e) {
                QtJambiInternal.invokeSlot(connection.receiver, connection.slotId,
                                           connection.returnType,
                                           connection.args, connection.convertTypes);
            } catch (Exception e) {
                System.err.printf("Exception while executing queued connection: sender=%s, receiver=%s %s\n",
                        sender != null ? sender.getClass().getName() : "N/A",
                        connection.receiver,
                        connection.slot.toString());
                if (e.getCause() != null)
                	e.getCause().printStackTrace();
                else
                	e.printStackTrace();
            }
            if (updateSender) {
                QtJambiInternal.swapQObjectSender(((QObject) connection.receiver).nativeId(),
                                                  oldSender, false);
                QSignalEmitter.currentSender.set(oldEmitter);
            }
        }

        private Object arguments[];
        private QSignalEmitter.AbstractSignal.Connection connection;
    }

    static Class<?> getComplexType(Class<?> primitiveType) {
        if (!primitiveType.isPrimitive())
            throw new RuntimeException("Primitive type required");

        if (primitiveType.equals(Integer.TYPE))
            return Integer.class;
        else if (primitiveType.equals(Double.TYPE))
            return Double.class;
        else if (primitiveType.equals(Long.TYPE))
            return Long.class;
        else if (primitiveType.equals(Float.TYPE))
            return Float.class;
        else if (primitiveType.equals(Short.TYPE))
            return Short.class;
        else if (primitiveType.equals(Boolean.TYPE))
            return Boolean.class;
        else if (primitiveType.equals(Character.TYPE))
            return Character.class;
        else if (primitiveType.equals(Byte.TYPE))
            return Byte.class;
        else
            throw new RuntimeException("Unrecognized primitive type: "
                    + primitiveType);
    }

    static byte primitiveToByte(Class<?> primitiveType) {
        if (primitiveType.equals(Integer.class) || primitiveType.equals(Integer.TYPE)) {
            return 'I';
        } else if (primitiveType.equals(Long.class) || primitiveType.equals(Long.TYPE)) {
            return 'J';
        } else if (primitiveType.equals(Short.class) || primitiveType.equals(Short.TYPE)) {
            return 'S';
        } else if (primitiveType.equals(Boolean.class) || primitiveType.equals(Boolean.TYPE)) {
            return 'Z';
        } else if (primitiveType.equals(Byte.class) || primitiveType.equals(Byte.TYPE)) {
            return 'B';
        } else if (primitiveType.equals(Float.class) || primitiveType.equals(Float.TYPE)) {
            return 'F';
        } else if (primitiveType.equals(Double.class) || primitiveType.equals(Double.TYPE)) {
            return 'D';
        } else if (primitiveType.equals(Character.class) || primitiveType.equals(Character.TYPE)) {
            return 'C';
        } else if (primitiveType.equals(Void.class) || primitiveType.equals(Void.TYPE)) {
            return 'V';
        } else {
            throw new RuntimeException(
                    "Error in conversion to primitive for complex type "
                            + primitiveType);
        }
    }

    @SuppressWarnings( "all" )
    private static QSignalEmitter.AbstractSignal lookupSignal(QSignalEmitter signalEmitter, String name)
    {
        if (name == null || signalEmitter == null) {
            System.err.println("lookupSignal: Name or object is null");
            return null;
        }

        QSignalEmitter.AbstractSignal returned = null;
        for (Class cls = signalEmitter.getClass();
             QSignalEmitter.class.isAssignableFrom(cls) && returned == null;
             cls = cls.getSuperclass()) {

            Field f;
            try {
                f = cls.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                continue;
            }

            //noinspection EmptyCatchBlock
            try {
                f.setAccessible(true);
            } catch (SecurityException e) { }

            if (QSignalEmitter.AbstractSignal.class.isAssignableFrom(f.getType())) {
                try {
                    returned = (QSignalEmitter.AbstractSignal) f.get(signalEmitter);
                } catch (Exception e) {
                    returned = fetchSignal(signalEmitter, f);
                }
            }
        }

        return returned;
    }

    static Method lookupSlot(Object object, String signature) {
        Class cls = object.getClass();

        int pos = signature.indexOf('(');
        if (pos < 0) {
            throw new RuntimeException("Wrong syntax in slot signature: '"
                                       + signature + "'");
        }
        int spacePos = signature.trim().lastIndexOf(' ', pos);
        if (pos > spacePos && spacePos > 0) {
            throw new RuntimeException(
                    "Do not specify return type in slot signature: '"
                            + signature + "'");
        }

        String name = signature.substring(0, pos).trim();

        int pos2 = signature.indexOf(')', pos);
        if (pos2 < 0) {
            throw new RuntimeException("Wrong syntax in slot signature: '"
                                       + signature + "'");
        }
        String strTypes = signature.substring(pos + 1, pos2).trim();

        String argumentTypes[];

        if (strTypes.length() == 0)
            argumentTypes = new String[0];
        else
            argumentTypes = strTypes.split(",");

        for (int i = 0; i < argumentTypes.length; ++i)
            argumentTypes[i] = argumentTypes[i].replace(" ", "");

        return findFunctionRecursive(cls, name, argumentTypes);
    }

    private static Method findFunctionRecursive(Class cls, String functionName,
            String argumentTypes[]) {
        Method methods[] = cls.getDeclaredMethods();

        for (Method m : methods) {
            boolean found;
            if (!m.getName().equals(functionName))
                continue;

            Class a[] = m.getParameterTypes();
            if (a.length != argumentTypes.length)
                continue;

            found = true;
            for (int i = 0; i < a.length; ++i) {
                String arg = a[i].getName();

                Class t = a[i];

                if(t.isArray()){
                    String brackets = "";

                    do {
                        t = t.getComponentType();
                        brackets += "[]";
                    }
                    while(t.isArray());

                    arg = t.getName() + brackets;
                }

                if (argumentTypes[i].indexOf('.') < 0) {
                    arg = arg.substring(arg.lastIndexOf('.') + 1);
                }

                if (!arg.equals(argumentTypes[i])) {

                    found = false;
                    break;
                }
            }

            if (found)
                return m;
        }

        cls = cls.getSuperclass();
        if (cls == null)
            return null;
        else
            return findFunctionRecursive(cls, functionName, argumentTypes);
    }

    private static native long nativeSwapQObjectSender(long receiver_id,
            long sender_id, boolean returnPreviousSender);

    public static long swapQObjectSender(long receiver, long newSender, boolean returnPreviousSender) {
    	return nativeSwapQObjectSender(receiver,
    			newSender != 0 ? newSender : 0, returnPreviousSender);
    }

    @SuppressWarnings("unused")
    private static boolean signalMatchesSlot(String signal, String slot) {                    
        // void slots always match...
        if (slot.contains("()"))
            return true;

        int signalIndex = signal.indexOf('<');

        // Match only if () slot which is covered above already...
        if (signalIndex < 0)
            return false;

        int slotIndex = slot.indexOf('(');
        if (slotIndex < 0) {
            throw new IllegalArgumentException("slot doesn't contain () as expected, '"
                                               + slot + "'");
        }

        String signalArguments[] = signal.substring(signalIndex + 1, signal.length() - 1).split(",");
        String slotArguments[] = slot.substring(slotIndex + 1, slot.length() - 1).split(",");

        if (slotArguments.length > signalArguments.length)
            return false;

        for (int i=0; i<slotArguments.length; ++i) {
            if (!matchTypes(signalArguments[i], slotArguments[i]))
                return false;
        }

        return true;
    }

    
    private static HashMap<String, String> typeMap;
    static {
        typeMap = new HashMap<String, String>();
        typeMap.put("java.lang.Boolean", "boolean");
        typeMap.put("java.lang.Byte", "byte");
        typeMap.put("java.lang.Char", "char");
        typeMap.put("java.lang.Short", "short");
        typeMap.put("java.lang.Integer", "int");
        typeMap.put("java.lang.Long", "long");
        typeMap.put("java.lang.Float", "float");
        typeMap.put("java.lang.Double", "double");
    }    
    private static boolean matchTypes(String a, String b) {
        return (a.equals(b) || (typeMap.get(a) != null && typeMap.get(a).equals(b)));
    }        
    
    public static void disconnect(QSignalEmitter sender, Object receiver) {
        Class cls = sender.getClass();
        while (QSignalEmitter.class.isAssignableFrom(cls)) {
            Field fields[] = cls.getDeclaredFields();

            for (Field f : fields) {
                if (isSignal(f.getType())) {
                    QObject.AbstractSignal signal;
                    try {
                        f.setAccessible(true);
                        signal = (QObject.AbstractSignal) f.get(sender);
                    } catch (Exception e) {
                        signal = fetchSignal(sender, f);
                    }

                    signal.disconnect(receiver);
                }
            }

            cls = cls.getSuperclass();
        }
    }

    public static native QSignalEmitter sender(QObject receiver);

    static Method findEmitMethod(QSignalEmitter.AbstractSignal signal) {
        Method methods[] = signal.getClass().getDeclaredMethods();

        Method slotMethod = null;
        for (Method method : methods) {
            if (method.getName().equals("emit")) {
                slotMethod = method;
                break;
            }
        }
        return slotMethod;
    }


    /**
     * Returns true if the class cl represents a Signal.
     * @return True if the class is a signal
     * @param cl The class to check
     */
    static boolean isSignal(Class cl) {
        return QSignalEmitter.AbstractSignal.class.isAssignableFrom(cl);
    }

    static native QSignalEmitter.AbstractSignal fetchSignal(QSignalEmitter signalEmitter, Field field);

    static native long resolveSlot(Method method);

    static native void invokeSlot(Object receiver, long m,
            byte returnType, Object args[], int slotTypes[]);

    /*private static native void setField(QSignalEmitter object, Field f,
            QObject.AbstractSignal newValue);*/

    static native boolean cppDisconnect(QObject sender, String signal, QObject receiver, String slot);

    /**
     * Searches the object's class and its superclasses for a method of the given name and returns
     * its signature.
     */
    static private HashMap<String, String> signalMethodSignatureCash = new HashMap<String, String>();
    static String findSignalMethodSignature(QSignalEmitter signalEmitter, String name) throws NoSuchFieldException, IllegalAccessException {

        Class cls = signalEmitter.getClass();
        String fullName = cls + "." + name;
        String found = signalMethodSignatureCash.get(fullName);

        if (found != null) {
            return found;
        }

        while (cls != null) {
            Method methods[] = cls.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(name)) {
                    found = name + "(";

                    Class<?> params[] = method.getParameterTypes();
                    for (int j = 0; j < params.length; ++j) {
                        if (j > 0) {
                            found += ",";
                        }
                        found += params[j].getName();
                    }
                    found = found + ")";
                    break;
                }
            }

            cls = cls.getSuperclass();
        }
        signalMethodSignatureCash.put(fullName, found);
        return found;
    }

    public static List<QObject> findChildren(QObject qobject, Class<?> cl,
            String name) {
        List<QObject> children = qobject.children();
        List<QObject> matching = new ArrayList<QObject>();
        for (QObject current : children) {
            if ((name == null || name.equals(current.objectName()))
                    && (cl == null || cl.isAssignableFrom(current.getClass()))) {
                matching.add(current);
            }
            matching.addAll(current.findChildren(cl, name));
        }

        return matching;
    }

    public static List<QObject> findChildren(QObject qobject, Class<?> cl,
            QRegExp name) {
        List<QObject> children = qobject.children();
        List<QObject> matching = new ArrayList<QObject>();
        for (QObject current : children) {
            if ((name == null || name.indexIn(current.objectName()) >= 0)
                    && (cl == null || cl.isAssignableFrom(current.getClass()))) {
                matching.add(current);
            }
            matching.addAll(current.findChildren(cl, name));
        }

        return matching;
    }

    public static QObject findChild(QObject qobject, Class<?> cl, String name) {
        List<QObject> children = qobject.children();
        for (QObject current : children) {
            if ((name == null || name.equals(current.objectName()))
                && (cl == null || cl.isAssignableFrom(current.getClass()))) {
                return current;
            }
            QObject object = findChild(current, cl, name);
            if (object != null)
                return object;
        }
        return null;
    }

    public native static Object createExtendedEnum(int value, int ordinal,
            Class cl, String name);


    private static class MutableInteger {
        int value;
    }

    private static HashMap<Class, MutableInteger> expensesTable;
    public static void countExpense(Class cl, int cost, int limit) {
        if (expensesTable == null)
            expensesTable = new HashMap<Class, MutableInteger>();

        MutableInteger mi = expensesTable.get(cl);
        if (mi == null) {
            mi = new MutableInteger();
            expensesTable.put(cl, mi);
        }

        mi.value += cost;
        if (mi.value > limit) {
            mi.value = 0;
            System.gc();
        }
    }

    private static final boolean threadAsserts;

    static {
        threadAsserts = !Utilities.matchProperty("com.trolltech.qt.thread-check", "false", "no");
    }

    public static void setField(Object owner, Class<?> declaringClass, String fieldName, Object newValue) {

    	Field f = null;
    	try {
    		f = declaringClass.getDeclaredField(fieldName);
    		f.setAccessible(true);
    		f.set(owner, newValue);
    	} catch (Exception e) {
    		if (!setFieldNative(owner, f, newValue)) {
    			throw new RuntimeException("Cannot set field '" + fieldName);
    		}
    	}
    }
    public static native boolean setFieldNative(Object owner, Field field, Object newValue);

    public static Object fetchField(Object owner, Class<?> declaringClass, String fieldName) {

    	Field f = null;
    	try {
    		f = declaringClass.getDeclaredField(fieldName);
    		f.setAccessible(true);
    		return f.get(owner);
    	} catch (Exception e) {
    		return fetchFieldNative(owner, f);
    	}
    }
    public static native Object fetchFieldNative(Object owner, Field field);

    public static void threadCheck(QObject obj) {
    	if (threadAsserts)

    	    if (obj.thread() != null && obj.thread() != Thread.currentThread()) {
    	        throw new QThreadAffinityException("QObject used from outside its own thread",
                                                   obj,
                                                   Thread.currentThread());
            }
    }

    public static QNativePointer intArrayToNativePointer(int data[]) {
        QNativePointer np = new QNativePointer(QNativePointer.Type.Int, data.length);
        for (int i=0; i<data.length; ++i)
            np.setIntAt(i, data[i]);
        return np;
    }

    public static QNativePointer byteArrayToNativePointer(byte data[]) {
        QNativePointer np = new QNativePointer(QNativePointer.Type.Byte, data.length);
        for (int i=0; i<data.length; ++i)
            np.setByteAt(i, data[i]);
        return np;
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
        return method.getDeclaringClass().getAnnotation(QtJambiGeneratedClass.class) == null;
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

        ((QSignalEmitter.AbstractSignal) signal_object).connect(receiver, methodSignature(method));
    }

    /**
     * Dunno what to make of this, so I'm making it internal.
     * @exclude
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

    /**
     * Sets the path in which Qt Jambi should search for resource
     * engines.
     */
    public static void addSearchPathForResourceEngine(String path)
    {
        QClassPathEngine.addSearchPath(path);
    }

    /**
     * Removes <tt>path</tt> from the path in which Qt Jambi searches
     * for resource engines.
     */
    public static void removeSearchPathForResourceEngine(String path)
    {
        QClassPathEngine.removeSearchPath(path);
    }

    /**
     * Returns the class of the most direct ancestor of <tt>obj</tt> that
     * is an instance of a class generated by the Qt Jambi designer. It
     * returns the class of the object itself if its class is
     * generated by the designer.
     */
    public static Class findGeneratedSuperclass(Object obj){
        Class<?> clazz = obj.getClass();
        while(clazz!=null && !clazz.isAnnotationPresent(QtJambiGeneratedClass.class)){
            clazz = clazz.getSuperclass();
        }

        return clazz;
    }

    public static void writeSerializableJavaObject(QDataStream s, Object o) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(o);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        s.writeBytes(bos.toByteArray());
    }
    
    private static class MetaData {
        public int metaData[];
        public byte stringData[];
        
        public Field signalsArray[];
        public Method slotsArray[];
        
        public Method propertyReadersArray[];
        public Method propertyWritersArray[];
        public Method propertyResettersArray[];
        public Method propertyDesignablesArray[];
        public Class<?> extraDataArray[];
        
        public String originalSignatures[];
    }
    
    private final static int MethodAccessPrivate = 0x00;
    private final static int MethodAccessProtected = 0x01;
    private final static int MethodAccessPublic = 0x02;
    private final static int MethodSignal = 0x04;
    private final static int MethodSlot = 0x8;
    private final static int PropertyReadable = 0x1;
    private final static int PropertyWritable = 0x2;    
    private final static int PropertyResettable = 0x4;
    private final static int PropertyEnumOrFlag = 0x8;
    private final static int PropertyDesignable = 0x1000;
    private final static int PropertyResolveDesignable = 0x2000;
    private final static int PropertyStored = 0x10000;
    
    
    public static boolean isGeneratedClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(QtJambiGeneratedClass.class);
    }
    
    public native static void emitNativeSignal(QObject object, String signalSignature, String signalCppSignature, Object args[]);
    
    public static String cppSignalSignature(QSignalEmitter signalEmitter, String signalName) {
        QSignalEmitter.AbstractSignal signal = lookupSignal(signalEmitter, signalName);
        if (signal != null)
            return cppSignalSignature(signal);
        else
            return "";
    }
    
    public static String cppSignalSignature(QSignalEmitter.AbstractSignal signal) {
        String signalParameters = signalParameters(signal);
        String params = internalTypeName(signalParameters, 1);        
        if (signalParameters.length() > 0 && params.length() == 0)
            return "";
        else
            return signal.name() + "(" + params + ")"; 
    }
    
    private native static String internalTypeName(String s, int varContext);
    
    private static int queryEnums(Class<?> clazz, Hashtable<String, Class<?>> enums) {
        int enumConstantCount = 0;
        
        Class<?> declaredClasses[] = clazz.getDeclaredClasses();        
        for (Class<?> declaredClass : declaredClasses)
            enumConstantCount += putEnumTypeInHash(declaredClass, enums);        
        
        return enumConstantCount;
    }
    
    private static Class getEnumForQFlags(Class<?> flagsType) {
        Type t = flagsType.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type typeArguments[] = ((ParameterizedType)t).getActualTypeArguments();
            return ((Class) typeArguments[0]);
        }
        
        return null;
    }
    
    private static int putEnumTypeInHash(Class<?> type, Hashtable<String, Class<?>> enums) {
        Class<?> flagsType = QFlags.class.isAssignableFrom(type) ? type : null;
        Class<?> enumType = type.isEnum() ? type : null;                    
        if (enumType == null && flagsType != null) {
            enumType = getEnumForQFlags(flagsType);
        }
        
        if (enumType == null)
            return 0;        
        
        // Since Qt supports enums that are not part of the meta object
        // we need to check whether the enum can actually be used in
        // a property. 
        Class<?> enclosingClass = enumType.getEnclosingClass();
        if (   enclosingClass != null               
            && ((!QObject.class.isAssignableFrom(enclosingClass) && !Qt.class.equals(enclosingClass))
               || enumType.isAnnotationPresent(QtBlockedEnum.class))) {
            return -1;
        }
                
        int enumConstantCount = 0;
        if (!enums.contains(enumType.getName())) {
            enums.put(enumType.getName(), enumType);            
            
            enumConstantCount = enumType.getEnumConstants().length;
        } 
        
        if (flagsType != null && !enums.contains(flagsType.getName()))
            enums.put(flagsType.getName(), flagsType);
                
        return enumConstantCount;
    }
    
    private static Object isDesignable(Method declaredMethod, Class<?> clazz) {
        QtPropertyDesignable designable = declaredMethod.getAnnotation(QtPropertyDesignable.class);
        
        if (designable != null) {
            String value = designable.value();
            
            if (value.equals("true")) {
                return Boolean.TRUE;
            } else if (value.equals("false")) {
                return Boolean.FALSE;                                
            } else try {
                Method m = clazz.getMethod(value, (Class<?>[]) null);  
                if (isBoolean(m.getReturnType()))
                    return m;
                else
                    throw new RuntimeException("Wrong return type of designable method '" + m.getName() + "'");
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } 
        
        return Boolean.TRUE;
    }
    
    private static boolean isValidSetter(Method declaredMethod) {
        return (declaredMethod.getParameterTypes().length == 1
                && declaredMethod.getReturnType() == Void.TYPE                     
                && !internalTypeName(methodParameters(declaredMethod), 1).equals(""));        
    }
    
    private static Method getMethod(Class<?> clazz, String name, Class<?> args[]) {
        try {
            return clazz.getMethod(name, args);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    private static String capitalizeFirst(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
    
    
    private static class Container {
        private enum AnnotationType {
            Reader,
            Writer,
            Resetter
        }
        
        private Method method;
        private String name = null;
        private boolean enabled;
        private AnnotationType type;
        
        
        private Container(String name, Method method, boolean enabled, AnnotationType type) {
            this.name = name;
            this.method = method;
            this.enabled = enabled;
            this.type = type;
        }

        private Container(QtPropertyReader reader, Method method) {
            this(reader.name(), method, reader.enabled(), AnnotationType.Reader);
        }
        
        private Container(QtPropertyWriter writer, Method method) {
            this(writer.name(), method, writer.enabled(), AnnotationType.Writer);
        }
        
        private Container(QtPropertyResetter resetter, Method method) {
            this(resetter.name(), method, resetter.enabled(), AnnotationType.Resetter);
        }
        
        private static String removeAndLowercaseFirst(String name, int count) {
            return Character.toLowerCase(name.charAt(count)) + name.substring(count + 1);
        }    
        
        private String getNameFromMethod(Method method) {
            if (type == AnnotationType.Resetter) {
                return "";
            } else if (type == AnnotationType.Reader) {
                String name = method.getName();
                if (name.startsWith("get"))
                    name = removeAndLowercaseFirst(name, 3);
                else if (isBoolean(method.getReturnType()) && name.startsWith("is"))
                    name = removeAndLowercaseFirst(name, 2);
                else if (isBoolean(method.getReturnType()) && name.startsWith("has"))
                    name = removeAndLowercaseFirst(name, 3);
                
                return name;
            } else { // starts with "set"
                String name = method.getName();
                if (!name.startsWith("set")) {
                    throw new IllegalArgumentException("The correct pattern for setter accessor names is setXxx where Xxx is the property name with upper case initial.");
                }
                                
                name = removeAndLowercaseFirst(name, 3);
                return name;
            }
        }
        
        private String name() {
            if (name == null || name.length() == 0)
                name = getNameFromMethod(method);
            
            return name;            
        }
        
        private boolean enabled() {
            return enabled;
        }

        private static Container readerAnnotation(Method method) {
            QtPropertyReader reader = method.getAnnotation(QtPropertyReader.class);        
            return reader == null ? null : new Container(reader, method);
        }        
        
        private static Container writerAnnotation(Method method) {
            QtPropertyWriter writer = method.getAnnotation(QtPropertyWriter.class);        
            return writer == null ? null : new Container(writer, method);            
        }
        
        private static Container resetterAnnotation(Method method) {
            QtPropertyResetter resetter = method.getAnnotation(QtPropertyResetter.class);        
            return resetter == null ? null : new Container(resetter, method);            
        }
        
    }
    
    private static boolean isBoolean(Class<?> type) {
        return (type == Boolean.class || type == Boolean.TYPE);
    }
    
    
    private static Method notBogus(Method method, String propertyName, Class<?> paramType) {
        if (method == null)
            return null;        
        
        Container reader = Container.readerAnnotation(method);         
        if (reader != null            
            && (!reader.name().equals(propertyName)
                || !reader.enabled()
                || !method.getReturnType().isAssignableFrom(paramType))) { 
            return null;
        } else {
            return method;
        }
    }
    
    private static boolean isValidGetter(Method method) {
        return (method.getParameterTypes().length == 0 
                && method.getReturnType() != Void.TYPE);        
    }
    
    @SuppressWarnings("unused")
    private static MetaData buildMetaData(Class<? extends QObject> clazz) {
        MetaData metaData = new MetaData();
                
        List<Method> slots = new ArrayList<Method>();
        
        Hashtable<String, Method> propertyReaders = new Hashtable<String, Method>();
        Hashtable<String, Method> propertyWriters = new Hashtable<String, Method>();
        Hashtable<String, Object> propertyDesignables = new Hashtable<String, Object>();
        Hashtable<String, Method> propertyResetters = new Hashtable<String, Method>();
        
        // First we get all enums actually declared in the class 
        Hashtable<String, Class<?>> enums = new Hashtable<String, Class<?>>(); 
        int enumConstantCount = queryEnums(clazz, enums);
        int enumCount = enums.size(); // Get the size before we add external enums
                
        Method declaredMethods[] = clazz.getDeclaredMethods();        
        for (Method declaredMethod : declaredMethods) {
            
            if (!declaredMethod.isAnnotationPresent(QtBlockedSlot.class) 
                    && ((declaredMethod.getModifiers() & Modifier.STATIC) != Modifier.STATIC)) {
                
                // If we can't convert the type, we don't list it
                String methodParameters = methodParameters(declaredMethod);
                String returnType = declaredMethod.getReturnType().getName();                
                if ((methodParameters.equals("") || !internalTypeName(methodParameters, 1).equals(""))
                    &&(returnType.equals("") || returnType.equals("void") || !internalTypeName(returnType, 0).equals(""))) {                     
                    slots.add(declaredMethod);             
                }
            }

            // Rules for readers:
            // 1. Zero arguments
            // 2. Return something other than void
            // 3. We can convert the type
            Container reader = Container.readerAnnotation(declaredMethod);
            {                
                
                if (reader != null 
                    && reader.enabled()
                    && isValidGetter(declaredMethod)
                    && !internalTypeName(declaredMethod.getReturnType().getName(), 0).equals("")) {
                    
                    // If the return type of the property reader is not registered, then 
                    // we need to register the owner class in the meta object (in which case
                    // it has to be a QObject)
                    Class<?> returnType = declaredMethod.getReturnType();                    
                    
                    int count = putEnumTypeInHash(returnType, enums);
                    if (count < 0) {
                        System.err.println("Problem in property '" + reader.name() + "' in '" + clazz.getName() 
                                           + "': Only enum types 1. declared inside QObject subclasses or the " 
                                           + "Qt interface and 2. declared without the QtBlockedEnum annotation " 
                                           + "are supported for properties");
                        continue;
                    } 
                                        
                    propertyReaders.put(reader.name(), declaredMethod);
                    propertyDesignables.put(reader.name(), isDesignable(declaredMethod, clazz));                                      
                }
            }
            
            // Rules for writers:
            // 1. Takes exactly one argument
            // 2. Return void
            // 3. We can convert the type
            Container writer = Container.writerAnnotation(declaredMethod);
            {
                                
                Class<?> parameterTypes[] = declaredMethod.getParameterTypes();
                if (writer != null
                    && writer.enabled()
                    && isValidSetter(declaredMethod)) {
                    propertyWriters.put(writer.name(), declaredMethod);
                }
            }
        
            // Rules for resetters:
            // 1. No arguments
            // 2. Return void            
            {
                Container resetter = Container.resetterAnnotation(declaredMethod);
                
                if (resetter != null 
                    && declaredMethod.getParameterTypes().length == 0
                    && declaredMethod.getReturnType() == Void.TYPE) {
                    propertyResetters.put(resetter.name(), declaredMethod);
                } 
            }            
            
            // Check naming convention by looking for setXxx patterns, but only if it hasn't already been 
            // annotated as a writer
            if (writer == null
                && reader == null // reader can't be a writer, cause the signature doesn't match, just an optimization
                && declaredMethod.getName().startsWith("set")
                && declaredMethod.getName().charAt(3) == Character.toUpperCase(declaredMethod.getName().charAt(3))
                && isValidSetter(declaredMethod)) {
                
                Class<?> paramType = declaredMethod.getParameterTypes()[0];                
                String propertyName = Character.toLowerCase(declaredMethod.getName().charAt(3))
                                    + declaredMethod.getName().substring(4);
                
                if (!propertyReaders.containsKey(propertyName)) {                                                                     
                    // We need a reader as well, and the reader must not be annotated as disabled
                    // The reader can be called 'xxx', 'getXxx', 'isXxx' or 'hasXxx' 
                    // (just booleans for the last two)
                    Method readerMethod = notBogus(getMethod(clazz, propertyName, null), propertyName, paramType);
                    if (readerMethod == null) 
                        readerMethod = notBogus(getMethod(clazz, "get" + capitalizeFirst(propertyName), null), propertyName, paramType);
                    if (readerMethod == null && isBoolean(paramType))
                        readerMethod = notBogus(getMethod(clazz, "is" + capitalizeFirst(propertyName), null), propertyName, paramType);
                    if (readerMethod == null && isBoolean(paramType))
                        readerMethod = notBogus(getMethod(clazz, "has" + capitalizeFirst(propertyName), null), propertyName, paramType);
                    
                    if (readerMethod != null) { // yay
                        reader = Container.readerAnnotation(readerMethod);
                        if (reader == null) {
                            propertyReaders.put(propertyName, readerMethod);
                            propertyWriters.put(propertyName, declaredMethod);
                            
                            propertyDesignables.put(propertyName, isDesignable(readerMethod, clazz));
                        }
                    }
                }
            }
        }
        
        Field declaredFields[] = clazz.getDeclaredFields();
        List<Field> signalFields = new ArrayList<Field>();
        List<QSignalEmitter.ResolvedSignal> resolvedSignals = new ArrayList<QSignalEmitter.ResolvedSignal>();
        for (Field declaredField : declaredFields) {
            if (isSignal(declaredField.getType())) {        
                // If we can't convert all the types we don't list the signal
                QSignalEmitter.ResolvedSignal resolvedSignal = QSignalEmitter.resolveSignal(declaredField, declaredField.getDeclaringClass());
                String signalParameters = signalParameters(resolvedSignal);
                if (signalParameters.length() == 0 || internalTypeName(signalParameters, 1).length() != 0) {
                    signalFields.add(declaredField);
                    resolvedSignals.add(resolvedSignal);
                }
                    
            }
        }
        metaData.signalsArray = signalFields.toArray(new Field[0]);
        
        {
            int functionCount = slots.size() + signalFields.size();
            int propertyCount = propertyReaders.keySet().size();            
            
            metaData.metaData = new int[12 + functionCount * 5 + 1 + propertyCount * 3 + enumCount * 4 + enumConstantCount * 2]; 
            
            // Add static header
            metaData.metaData[0] = 1; // Revision
            // metaData[1] = 0 // class name  (ints default to 0) 
                        
            metaData.metaData[2] = 1;  // class info count
            metaData.metaData[3] = 10; // class info offset 
              
            // Functions always start at offset 12 (header has size 10, class info size 2)
            metaData.metaData[4] = functionCount;
            metaData.metaData[5] = functionCount > 0 ? 12 : 0;
            
            metaData.metaData[6] = propertyCount;
            metaData.metaData[7] = 12 + functionCount * 5; // Each function takes 5 ints 
            
            // Enums
            metaData.metaData[8] = enumCount;
            metaData.metaData[9] = 12 + functionCount * 5 + propertyCount * 3; // Each property takes 3 ints
                                                
            int offset = 0;
            int metaDataOffset = 10; // Header is always 10 ints long
            Hashtable<String, Integer> strings = new Hashtable<String, Integer>();
            List<String> stringsInOrder = new ArrayList<String>();
            // Class name
            {
                String className = clazz.getName().replaceAll("\\.", "::");
                stringsInOrder.add(className);
                strings.put(clazz.getName(), offset); offset += className.length() + 1;                
            }
            
            // Class info
            {
                offset += addString(metaData.metaData, strings, stringsInOrder, "__qt__binding_shell_language", offset, metaDataOffset++);
                offset += addString(metaData.metaData, strings, stringsInOrder, "Qt Jambi", offset, metaDataOffset++);                 
            }
            
            metaData.originalSignatures = new String[signalFields.size() + slots.size()];
            
            // Signals (### make sure enum types are covered)
            for (int i=0; i<signalFields.size(); ++i) {
                Field signalField = signalFields.get(i);
                QSignalEmitter.ResolvedSignal resolvedSignal = resolvedSignals.get(i);
                
                String javaSignalParameters = signalParameters(resolvedSignal);
                metaData.originalSignatures[i] = resolvedSignal.name 
                    + (javaSignalParameters.length() > 0 ? '<' + javaSignalParameters + '>' : "");
                
                String signalParameters = internalTypeName(javaSignalParameters, 1);
                                                
                // Signal name
                offset += addString(metaData.metaData, strings, stringsInOrder, resolvedSignal.name + "(" + signalParameters + ")", offset, metaDataOffset++);
                                                
                // Signal parameters
                offset += addString(metaData.metaData, strings, stringsInOrder, signalParameters, offset, metaDataOffset++);
                
                // Signal type (signals are always void in Qt Jambi)
                offset += addString(metaData.metaData, strings, stringsInOrder, "", offset, metaDataOffset++);
                
                // Signal tag (Currently not supported by the moc either)
                offset += addString(metaData.metaData, strings, stringsInOrder, "", offset, metaDataOffset++);
                
                // Signal flags
                int flags = MethodSignal;
                int modifiers = signalField.getModifiers();
                if ((modifiers & Modifier.PRIVATE) == Modifier.PRIVATE)
                    flags |= MethodAccessPrivate;
                else if ((modifiers & Modifier.PROTECTED) == Modifier.PROTECTED)
                    flags |= MethodAccessProtected;
                else if ((modifiers & Modifier.PUBLIC) == Modifier.PUBLIC)
                    flags |= MethodAccessPublic;                
                metaData.metaData[metaDataOffset++] = flags;
            }
            
            // Slots (### make sure enum types are covered, ### also test QFlags)
            for (int i=0; i<slots.size(); ++i) {
                Method slot = slots.get(i);
                
                String javaMethodSignature = methodSignature(slot);
                metaData.originalSignatures[signalFields.size() + i] = javaMethodSignature;
                
                // Slot signature
                offset += addString(metaData.metaData, strings, stringsInOrder, internalTypeName(javaMethodSignature, 1), offset, metaDataOffset++);
                
                // Slot parameters
                offset += addString(metaData.metaData, strings, stringsInOrder, internalTypeName(methodParameters(slot), 1), offset, metaDataOffset++);
                
                // Slot type 
                String returnType = slot.getReturnType().getName();
                if (returnType.equals("void")) returnType = "";
                offset += addString(metaData.metaData, strings, stringsInOrder, internalTypeName(returnType, 0), offset, metaDataOffset++);
                
                // Slot tag (Currently not supported by the moc either)
                offset += addString(metaData.metaData, strings, stringsInOrder, "", offset, metaDataOffset++);
                
                // Slot flags
                int flags = MethodSlot;
                int modifiers = slot.getModifiers();
                if ((modifiers & Modifier.PRIVATE) == Modifier.PRIVATE)
                    flags |= MethodAccessPrivate;
                else if ((modifiers & Modifier.PROTECTED) == Modifier.PROTECTED)
                    flags |= MethodAccessProtected;
                else if ((modifiers & Modifier.PUBLIC) == Modifier.PUBLIC)
                    flags |= MethodAccessPublic;
                
                metaData.metaData[metaDataOffset++] = flags;
            }
            metaData.slotsArray = slots.toArray(new Method[0]);
            
            String propertyNames[] = propertyReaders.keySet().toArray(new String[0]);
            metaData.propertyReadersArray = new Method[propertyNames.length]; 
            metaData.propertyResettersArray = new Method[propertyNames.length];
            metaData.propertyWritersArray = new Method[propertyNames.length];
            metaData.propertyDesignablesArray = new Method[propertyNames.length];
            for (int i=0; i<propertyNames.length; ++i) {
                Method reader = propertyReaders.get(propertyNames[i]);
                Method writer = propertyWriters.get(propertyNames[i]);
                Method resetter = propertyResetters.get(propertyNames[i]);
                Object designableVariant = propertyDesignables.get(propertyNames[i]);
                                
                if (writer != null && !reader.getReturnType().isAssignableFrom(writer.getParameterTypes()[0])) {
                    System.err.println("QtJambiInternal.buildMetaData: Writer for property " 
                            + propertyNames[i] + " takes a type which is incompatible with reader's return type.");
                    writer = null;
                }
                
                // Name
                offset += addString(metaData.metaData, strings, stringsInOrder, propertyNames[i], offset, metaDataOffset++);
                
                // Type (need to special case flags and enums)
                Class<?> t = reader.getReturnType();
                boolean isEnumOrFlags = Enum.class.isAssignableFrom(t) || QFlags.class.isAssignableFrom(t);
                                                
                String typeName = null;
                if (isEnumOrFlags && t.getDeclaringClass() != null && QObject.class.isAssignableFrom(t.getDeclaringClass())) {
                    // To avoid using JObjectWrapper for enums and flags (which is required in certain cases.)
                    typeName = t.getDeclaringClass().getName().replaceAll("\\.", "::") + "::" + t.getSimpleName();
                } else { 
                    typeName = internalTypeName(t.getName(), 0);
                }
                offset += addString(metaData.metaData, strings, stringsInOrder, typeName, offset, metaDataOffset++);
                                                
                int designableFlags = 0;
                if (designableVariant instanceof Boolean) {
                    if ((Boolean) designableVariant) designableFlags = PropertyDesignable;
                } else if (designableVariant instanceof Method) {
                    designableFlags = PropertyResolveDesignable;
                    metaData.propertyDesignablesArray[i] = (Method) designableVariant;
                }
                
                // Flags
                metaData.metaData[metaDataOffset++] = PropertyReadable | PropertyStored | designableFlags 
                    | (writer != null ? PropertyWritable : 0)
                    | (resetter != null ? PropertyResettable : 0)
                    | (isEnumOrFlags ? PropertyEnumOrFlag : 0);
                                                
                metaData.propertyReadersArray[i] = reader;
                metaData.propertyWritersArray[i] = writer;
                metaData.propertyResettersArray[i] = resetter;
            }
            
            // Enum types
            int enumConstantOffset = metaDataOffset + enumCount * 4;
            
            Hashtable<String, Class<?>> enclosingClasses = new Hashtable<String, Class<?>>();
            Collection<Class<?>> classes = enums.values();
            for (Class<?> cls : classes) {
                Class<?> enclosingClass = cls.getEnclosingClass();                                
                if (enclosingClass.equals(clazz)) {                
                    // Name
                    offset += addString(metaData.metaData, strings, stringsInOrder, cls.getSimpleName(), offset, metaDataOffset++);
                    
                    // Flags (1 == flags, 0 == no flags)
                    metaData.metaData[metaDataOffset++] = QFlags.class.isAssignableFrom(cls) ? 0x1 : 0x0;
                    
                    // Get the enum class 
                    Class<?> enumClass = Enum.class.isAssignableFrom(cls) ? cls : null;
                    if (enumClass == null) {
                        enumClass = getEnumForQFlags(cls);
                    }                 
                    
                    enumConstantCount = enumClass.getEnumConstants().length;
                    
                    // Count
                    metaData.metaData[metaDataOffset++] = enumConstantCount;
                    
                    // Data
                    metaData.metaData[metaDataOffset++] = enumConstantOffset;
                    
                    enumConstantOffset += 2 * enumConstantCount;
                } else if (!enclosingClass.isAssignableFrom(clazz) && !enclosingClasses.contains(enclosingClass.getName())) { 
                    // If the enclosing class of an enum is not in the current class hierarchy, then 
                    // the generated meta object needs to have a pointer to its meta object in the
                    // extra-data.                                 
                    enclosingClasses.put(enclosingClass.getName(), enclosingClass);
                }
            }
            metaData.extraDataArray = enclosingClasses.values().toArray(new Class<?>[0]);
            
            // Enum constants
            for (Class<?> cls : classes) {
                
                if (cls.getEnclosingClass().equals(clazz)) {
                    // Get the enum class 
                    Class<?> enumClass = Enum.class.isAssignableFrom(cls) ? cls : null;
                    if (enumClass == null) {
                        enumClass = getEnumForQFlags(cls);
                    }                 
                    
                    Enum enumConstants[] = (Enum[]) enumClass.getEnumConstants();                
                    
                    for (Enum enumConstant : enumConstants) {
                        // Key
                        offset += addString(metaData.metaData, strings, stringsInOrder, enumConstant.name(), offset, metaDataOffset++);
                        
                        // Value
                        metaData.metaData[metaDataOffset++] = enumConstant instanceof QtEnumerator 
                                                              ? ((QtEnumerator) enumConstant).value()
                                                              : enumConstant.ordinal();
                    }
                }
            }
            
            // EOD
            metaData.metaData[metaDataOffset++] = 0;
            
            int stringDataOffset = 0;
            metaData.stringData = new byte[offset + 1];
            
            for (String s : stringsInOrder) {                
                assert stringDataOffset == strings.get(s);
                System.arraycopy(s.getBytes(), 0, metaData.stringData, stringDataOffset, s.length());
                stringDataOffset += s.length();
                metaData.stringData[stringDataOffset++] = 0;                
            }
            
            
            
        }
        
        return metaData;
    }
    
    public native static List<QtProperty> properties(long nativeId);
    public static int indexOfProperty(long nativeId, String name) {
        List<QtProperty> properties = properties(nativeId);
        
        for (int i=0; i<properties.size(); ++i) {
            if (name.equals(properties.get(i).name())) 
                return i;
        }
        
        return -1;
    }
    
    private static String bunchOfClassNamesInARow(Class<?> classes[]) {
        return bunchOfClassNamesInARow(classes, null);
    }
    private static String bunchOfClassNamesInARow(Class<?> classes[], int arrayDimensions[]) {
        String classNames = "";
        
        for (int i=0; i<classes.length; ++i) {
            Class<?> clazz = classes[i];
            if (classNames.length() > 0)
                classNames += ",";
            
            String className = clazz.getName();
            if (arrayDimensions != null) {
                for (int j=0; j<arrayDimensions[i]; ++j) 
                    className = "java.lang.Object";
            }                                     
            
            classNames += className;
        }
        
        return classNames;
    }
    
    private static String methodParameters(Method m) {
        return bunchOfClassNamesInARow(m.getParameterTypes());
    }
    
    /**
     * Returns the signature of the method m excluding the modifiers and the
     * return type.
     */    
    private static String methodSignature(Method m, boolean includeReturnType) {
        return (includeReturnType ? m.getReturnType().getName() + " " : "")
               + m.getName() + "(" + methodParameters(m) + ")";
    }
    
    private static String methodSignature(Method m) {
        return methodSignature(m, false);
    }
    
    /*friendly*/ static String signalParameters(QSignalEmitter.AbstractSignal signal) 
    {
        return bunchOfClassNamesInARow(signal.resolveSignal(), signal.arrayDimensions());
    }
    
    private static String signalParameters(QSignalEmitter.ResolvedSignal resolvedSignal) {
        return bunchOfClassNamesInARow(resolvedSignal.types, resolvedSignal.arrayDimensions);
    }
    
    @SuppressWarnings("unused")
    private static String signalParameters(Field field, Class<?> declaringClass) {
        QSignalEmitter.ResolvedSignal resolvedSignal = QSignalEmitter.resolveSignal(field, declaringClass);
        return signalParameters(resolvedSignal);
    }
    
    private static int addString(int metaData[], 
                                 Hashtable<String, Integer> strings,
                                 List<String> stringsInOrder,
                                 String string,
                                 int offset,
                                 int metaDataOffset) {
        if (strings.containsKey(string)) {
            metaData[metaDataOffset] = strings.get(string);
            return 0;
        }
        
        metaData[metaDataOffset] = offset;
        strings.put(string, offset);
        stringsInOrder.add(string);
        return string.length() + 1;
    }
    
    public static Object readSerializableJavaObject(final QDataStream s) {
        Object res = null;
        try {
            ObjectInputStream in = new ObjectInputStream(new InputStream(){
                @Override
                public int read() throws IOException {
                    return s.readByte();
                }
            });
            res  = in.readObject();
            in.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
