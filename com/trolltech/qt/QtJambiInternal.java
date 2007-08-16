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
    }
    
    private final static int MethodAccessPrivate = 0x00;
    private final static int MethodAccessProtected = 0x01;
    private final static int MethodAccessPublic = 0x02;
    private final static int MethodSignal = 0x04;
    private final static int MethodSlot = 0x8;
    private final static int PropertyReadable = 0x1;
    private final static int PropertyWritable = 0x2;
    private final static int PropertyResettable = 0x4;    
    
    public static boolean isGeneratedClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(QtJambiGeneratedClass.class);
    }
    
    private native static String internalTypeName(String s, int varContext);
    
    public static MetaData buildMetaData(Class<? extends QObject> clazz, QObject object) {
        MetaData metaData = new MetaData();
                
        List<Method> slots = new ArrayList<Method>();
        List<QSignalEmitter.AbstractSignal> signals = new ArrayList<QSignalEmitter.AbstractSignal>();
        
        Hashtable<String, Method> propertyReaders = new Hashtable<String, Method>();
        Hashtable<String, Method> propertyWriters = new Hashtable<String, Method>();
        Hashtable<String, Method> propertyDesignables = new Hashtable<String, Method>();
        Hashtable<String, Method> propertyResetters = new Hashtable<String, Method>();
        
        Method declaredMethods[] = clazz.getDeclaredMethods();        
        for (Method declaredMethod : declaredMethods) {
            
            if (!declaredMethod.isAnnotationPresent(QtBlockedSlot.class) 
                    && ((declaredMethod.getModifiers() & Modifier.STATIC) != Modifier.STATIC)) {
                
                // If we can't convert the type, we don't list it
                String methodParameters = methodParameters(declaredMethod);
                String returnType = declaredMethod.getReturnType().getName();                
                if ((methodParameters.isEmpty() || !internalTypeName(methodParameters, 1).isEmpty())
                    &&(returnType.isEmpty() || returnType.equals("void") || !internalTypeName(returnType, 0).isEmpty())) {                     
                    slots.add(declaredMethod);             
                }
            }

            // Rules for readers:
            // 1. Zero arguments
            // 2. Return something other than void
            // 3. We can convert the type            
            {
                QtPropertyReader reader = declaredMethod.getAnnotation(QtPropertyReader.class);
                
                if (reader != null 
                    && declaredMethod.getParameterTypes().length == 0 
                    && declaredMethod.getReturnType() != Void.TYPE
                    && !internalTypeName(declaredMethod.getReturnType().getName(), 0).isEmpty()) {
                    propertyReaders.put(reader.name(), declaredMethod);
                }
            }
            
            // Rules for writers:
            // 1. Takes exactly one argument
            // 2. Return void
            // 3. We can convert the type
            {
                QtPropertyWriter writer = declaredMethod.getAnnotation(QtPropertyWriter.class);
                
                Class<?> parameterTypes[] = declaredMethod.getParameterTypes();
                if (writer != null 
                    && parameterTypes.length == 1
                    && declaredMethod.getReturnType() == Void.TYPE                     
                    && !internalTypeName(methodParameters(declaredMethod), 1).isEmpty()) {
                    propertyWriters.put(writer.name(), declaredMethod);
                }
            }
        
            // Rules for resetters:
            // 1. No arguments
            // 2. Return void            
            {
                QtPropertyResetter resetter = declaredMethod.getAnnotation(QtPropertyResetter.class);
                
                if (resetter != null 
                    && declaredMethod.getParameterTypes().length == 0
                    && declaredMethod.getReturnType() == Void.TYPE) {
                    propertyResetters.put(resetter.name(), declaredMethod);
                } 
            }

            // ### Designable properties
            
        }
        
        Field declaredFields[] = clazz.getDeclaredFields();
        List<Field> signalFields = new ArrayList<Field>();        
        for (Field declaredField : declaredFields) {
            QSignalEmitter.AbstractSignal signal = null;
            if (isSignal(declaredField.getType())) try {
                declaredField.setAccessible(true);      
                signal = (QSignalEmitter.AbstractSignal) declaredField.get(object);
            } catch (Exception e) {                
                signal = (QSignalEmitter.AbstractSignal) fetchFieldNative(object, declaredField);                
            }
        
            // If we can't convert all the types we don't list the signal
            if (signal != null) {
                String signalParameters = signalParameters(signal);
                if (signalParameters.isEmpty() || !internalTypeName(signalParameters, 1).isEmpty()) {
                    signals.add(signal);
                    signalFields.add(declaredField);
                }
            }            
        }
        metaData.signalsArray = signalFields.toArray(new Field[0]);
        
        {
            int functionCount = slots.size() + signals.size();
            
            metaData.metaData = new int[12 + functionCount * 5 + 1]; // Header size(10) + functionCount*5 + EOD
            
            // Add static header
            metaData.metaData[0] = 1; // Revision
            // metaData[1] = 0 // class name  (ints default to 0) 
                        
            metaData.metaData[2] = 1;  // class info count
            metaData.metaData[3] = 10; // class info offset 
              
            // Functions always start at offset 12 (header has size 10, class info size 2)
            metaData.metaData[4] = functionCount;
            metaData.metaData[5] = functionCount > 0 ? 12 : 0;
            
            
            metaData.metaData[6] = propertyReaders.keySet().size();
            metaData.metaData[7] = 12 + functionCount * 5; // Each function takes 5 ints 
            
            // 0, 0 (### enums not supported yet) 
                                                
            int offset = 0;
            int metaDataOffset = 10; // Header is always 10 ints long
            Hashtable<String, Integer> strings = new Hashtable<String, Integer>();
            List<String> stringsInOrder = new ArrayList<String>();
            // Class name
            {
                stringsInOrder.add(clazz.getName());
                strings.put(clazz.getName(), offset); offset += clazz.getName().length() + 1;                
            }
            
            // Class info
            {
                offset += addString(metaData.metaData, strings, stringsInOrder, "__qt__binding_shell_language", offset, metaDataOffset++);
                offset += addString(metaData.metaData, strings, stringsInOrder, "Qt Jambi", offset, metaDataOffset++);                 
            }
            
            // Signals
            for (QSignalEmitter.AbstractSignal signal : signals) {
                String signalParameters = internalTypeName(signalParameters(signal), 1);
                                                
                // Signal name
                offset += addString(metaData.metaData, strings, stringsInOrder, signal.name() + "(" + signalParameters + ")", offset, metaDataOffset++);
                
                
                
                // Signal parameters
                offset += addString(metaData.metaData, strings, stringsInOrder, signalParameters, offset, metaDataOffset++);
                
                // Signal type (signals are always void in Qt Jambi)
                offset += addString(metaData.metaData, strings, stringsInOrder, "", offset, metaDataOffset++);
                
                // Signal tag (### not implemented)
                offset += addString(metaData.metaData, strings, stringsInOrder, "", offset, metaDataOffset++);
                
                // Signal flags (### implement access types)
                int flags = (MethodAccessPublic | MethodSignal);
                metaData.metaData[metaDataOffset++] = flags;
            }
            
            // Slots
            for (Method slot : slots) {
                // Slot signature
                offset += addString(metaData.metaData, strings, stringsInOrder, internalTypeName(methodSignature(slot),1), offset, metaDataOffset++);
                
                // Slot parameters
                offset += addString(metaData.metaData, strings, stringsInOrder, internalTypeName(methodParameters(slot),1), offset, metaDataOffset++);
                
                // Slot type 
                String returnType = slot.getReturnType().getName();
                if (returnType.equals("void")) returnType = "";
                offset += addString(metaData.metaData, strings, stringsInOrder, internalTypeName(returnType,0), offset, metaDataOffset++);
                
                // Slot tag (### not implemented)
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
            for (int i=0; i<propertyNames.length; ++i) {
                Method reader = propertyReaders.get(propertyNames[i]);
                Method writer = propertyWriters.get(propertyNames[i]);
                Method resetter = propertyResetters.get(propertyNames[i]);
                
                if (!reader.getReturnType().isAssignableFrom(writer.getParameterTypes()[0])) {
                    System.err.println("QtJambiInternal.buildMetaData: Writer for property " 
                            + propertyNames[i] + " takes a type which is incompatible with reader's return type.");
                    writer = null;
                }
                
                // Name
                offset += addString(metaData.metaData, strings, stringsInOrder, propertyNames[i], offset, metaDataOffset++);
                
                // Type
                offset += addString(metaData.metaData, strings, stringsInOrder, internalTypeName(reader.getReturnType().getName(), 0), offset, metaDataOffset++);
                
                // Flags
                metaData.metaData[metaDataOffset++] = PropertyReadable 
                    | (writer != null ? PropertyWritable : 0)
                    | (resetter != null ? PropertyResettable : 0);
                                
                metaData.propertyReadersArray[i] = reader;
                metaData.propertyWritersArray[i] = writer;
                metaData.propertyResettersArray[i] = resetter;
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
    
    private static String bunchOfClassNamesInARow(Class<?> classes[]) {
        String classNames = "";
        
        for (Class<?> clazz : classes) {
            if (classNames.length() > 0)
                classNames += ",";
            classNames += clazz.getName();
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
    private static String methodSignature(Method m) {
        return m.getName() + "(" + methodParameters(m) + ")";
    }
    
    private static String signalParameters(QSignalEmitter.AbstractSignal signal) {
        return bunchOfClassNamesInARow(signal.resolveSignal());
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
