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

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QEvent;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QRegExp;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.*;

public class QtJambiInternal {

    static {
        QtJambi_LibraryInitializer.init();
    }

    /**
     * Internal superclass of all signals
     */
    public abstract static class InternalSignal {

        private boolean             m_in_cpp_emission      = false;
        private List<Connection>    m_connections          = new ArrayList<Connection>();
        private Class<?>            m_types[]              = null;
        private int                 m_arrayDims[]          = null;
        private String              m_name                 = "";
        private Class<?>            m_declaring_class      = null;
        private boolean             m_inEmit               = false;
        private boolean             m_connected_to_cpp     = false;
        private boolean             m_in_disconnect        = false;

        /**
         * Contains book holding info about a single connection
         */
        protected class Connection {
            public int      flags           = 0;
            public Object   receiver        = null;
            public Method   slot            = null;
            public byte     returnType      = 0;
            public int      convertTypes[]  = null;
            public long     slotId          = 0;
            public Object   args[]          = null;

            public static final int DIRECT_CONNECTION = 0x0001;
            public static final int QUEUED_CONNECTION = 0x0002;
            public static final int PUBLIC_SLOT       = 0x0010;

            public final boolean isSlotPublic() {
                return (flags & PUBLIC_SLOT) != 0;
            }

            public final boolean isQueuedConnection() {
                return (flags & QUEUED_CONNECTION) != 0;
            }

            public final boolean isDirectConnection() {
                return (flags & DIRECT_CONNECTION) != 0;
            }

            public final boolean isAutoConnection() {
                return (flags & (QUEUED_CONNECTION | DIRECT_CONNECTION)) == 0;
            }

            public Connection(Object receiver, Method slot, byte returnType,
                    byte connectionType) {
                this.receiver = receiver;
                this.slot = slot;
                this.slotId = resolveSlot(slot);
                this.returnType = returnType;

                if (connectionType == Qt.ConnectionType.QueuedConnection.value())
                    flags |= QUEUED_CONNECTION;
                else if (connectionType == Qt.ConnectionType.DirectConnection.value())
                    flags |= DIRECT_CONNECTION;

                if (Modifier.isPublic(slot.getModifiers())
                        && Modifier.isPublic(receiver.getClass().getModifiers())) {
                    flags |= PUBLIC_SLOT;
                }

                Class<?> slotParameterTypes[] = slot.getParameterTypes();
                Class<?> signalParameterTypes[] = resolveSignal();
                convertTypes = new int[slotParameterTypes.length];
                for (int i = 0; i < convertTypes.length; ++i) {
                    convertTypes[i] = 'L';
                    if (slotParameterTypes[i].isPrimitive())
                        convertTypes[i] = primitiveToByte(signalParameterTypes[i]);
                }
            }
        } // public class Connection

        /**
         * Connects the signal to a method in an object. Whenever it is emitted, the method will be invoked
         * on the given object.
         *
         * @param receiver The object containing the method to be invoked upon signal emission
         * @param method The signature of the method to call, excluding the return type and variable names
         * @param connectionType One of the connection types defined in the Qt interface.
         * @throws QNoSuchSlotException Raised if the method passed in the slot object was not found
         * @throws QRuntimeException Raised if the signal object could not be successfully introspected.
         */
        public final boolean connect(QObject receiver, String method,
                Qt.ConnectionType type) {
            Method slotMethod = lookupSlot(receiver, method);
            if (slotMethod == null)
                throw new QNoSuchSlotException(method);

            return connectSignalMethod(slotMethod, receiver, type.value());
        }

        /**
         * Disconnects the signal from a method in an object if the two were previously connected by a call to connect.
         *
         * @param receiver The object to which the signal is connected
         * @param method The method in the receiver object to which the signal is connected
         * @return true if the connection was successfully removed, otherwise false. The method will return false if the
         * connection has not been previously established by a call to connect.
         * @throws QNoSuchSlotException Raised if the method passed in the slot object was not found
         */
        public boolean disconnect(QObject receiver, String method) {
            if (method != null && receiver == null)
                throw new IllegalArgumentException("Receiver cannot be null if you specify a method");

            Method slotMethod = null;
            if (method != null) {
                slotMethod = lookupSlot(receiver, method);
                if (slotMethod == null)
                    throw new QNoSuchSlotException(method);
            }

            return removeConnection(receiver, slotMethod);
        }

        /**
         * Removes any connection from this signal to the specified receiving object
         *
         *  @param receiver The object to which the signal has connections
         *  @return true if any connection was successfully removed, otherwise false. The method will return false if no
         *  connection has previously been establish to the receiver.
         *
         *  @see #disconnect(QObject, String)
         **/
        public boolean disconnect(QObject receiver) {
            return disconnect(receiver, null);
        }

        /**
         * Removes all connections from this signal.
         *
         * @see #disconnect(QObject, String)
         **/
        public boolean disconnect() {
            return disconnect(null, null);
        }

        /**
         * Creates an auto-connection from this signal to the specified object and method.
         *
         * @see #connect(com.trolltech.qt.core.QObject, String, com.trolltech.qt.core.Qt.ConnectionType)
         **/
        public final boolean connect(QObject receiver, String method) {
            return connect(receiver, method, Qt.ConnectionType.AutoConnection);
        }

        /**
         * Connects this signal to another. Whenever this signal is emitted, it will cause the second signal to be emitted as well.
         * @param signalOut The second signal. This will be emitted whenever this signal is emitted.
         * @return true if the connection was successfully established. Otherwise false. The method will return false if the signatures
         * of the emit functions in the two signal objects were incompatible.
         * @throws RuntimeException Raised if either of the signal objects could not be successfully be introspected.
         */
        public final boolean connect(QObject.AbstractSignal signalOut) {
            return connectSignalMethod(findEmitMethod(signalOut), signalOut,
                    Qt.ConnectionType.DirectConnection.value());
        }

        /**
         * Disconnects a signal from another signal if the two were previously connected by a call to connect.
         * A call to this function will assure that the emission of the first signal will not cause the emission of the second.
         *
         * @param signalIn The first signal.
         * @param signalOut The second signal.
         * @return true if the two signals were successfully disconnected, or false otherwise.
         */
        public boolean disconnect(QObject.AbstractSignal signalOut) {

            return removeConnection(signalOut, findEmitMethod(signalOut));
        }

        public final String name() {
            resolveSignal();
            return m_name;
        }

        public final String declaringClassName() {
            resolveSignal();
            return m_declaring_class == null ? "" : m_declaring_class.getName();
        }

        public final String fullName() {
            return declaringClassName() + "." + name();
        }

        /**
         * Returns true if the connection receiver is the emission of the C++ version of the current
         * signal. This is used to avoid recursion from C++ emissions. Whenever we have a C++ emission
         * we know we will have a function with the same name in the same declaring class as the signal.
         */
        private boolean slotIsCppEmit(Connection connection) {
            return (connection.slot.getName().equals(name())
                    && connection.receiver == qobject()
                    && connection.slot.getDeclaringClass().equals(m_declaring_class));
        }

        private final boolean connectSignalMethod(Method slotMethod,
                                                  Object receiver,
                                                  int connectionType) {
            if (slotMethod.getAnnotation(QtBlockedSlot.class) != null)
                throw new QNoSuchSlotException(slotMethod.toString());

            if (!matchSlot(slotMethod))
                return false;

            addConnection(receiver, slotMethod, connectionType);
            return true;
        }

        private final int[] arrayDimensions() {
            resolveSignal();
            return m_arrayDims;
        }

        private final Class<?>[] resolveSignal() {
            if (m_types == null) {
                m_types = new Class[0]; // For signals with no parameters
                m_arrayDims = new int[0];

                Class cls = qobject().getClass();
                while (cls != null) {
                    Field fields[] = cls.getDeclaredFields();
                    for (int i = 0; i < fields.length; ++i) {
                        if (QObject.AbstractSignal.class
                                .isAssignableFrom(fields[i].getType())) {
                            QObject.AbstractSignal sig = fetchSignal(qobject(),
                                    fields[i]);
                            if (sig == null) {
                                throw new RuntimeException(
                                        "Error reflecting on signal: "
                                                + fields[i].getName());
                            }

                            if (sig == this) {
                                m_name = fields[i].getName();
                                m_declaring_class = fields[i].getDeclaringClass();

                                Type t = fields[i].getGenericType();

                                // either t is a parameterized type, or it is Signal0
                                if (t instanceof ParameterizedType) {
                                    ParameterizedType p = (ParameterizedType) t;
                                    Type actualTypes[] = p
                                            .getActualTypeArguments();

                                    m_types = new Class[actualTypes.length];
                                    m_arrayDims = new int[actualTypes.length];
                                    for (int j = 0; j < m_types.length; ++j) {

                                        Type actualType = actualTypes[j];
                                        int arrayDims = 0;
                                        while (actualType instanceof GenericArrayType
                                                || actualType instanceof ParameterizedType) {
                                            if (actualType instanceof GenericArrayType) {
                                                actualType = ((GenericArrayType) actualType)
                                                        .getGenericComponentType();
                                                ++arrayDims;
                                            } else { // ParameterizedType
                                                actualType = ((ParameterizedType) actualType)
                                                        .getRawType();
                                            }
                                        }

                                        if (actualType instanceof Class) {
                                            m_types[j] = (Class) actualType;
                                            m_arrayDims[j] = arrayDims;
                                        } else {
                                            throw new RuntimeException(
                                                    "Signals of generic types not supported: "
                                                            + actualTypes[j]
                                                                    .toString());
                                        }
                                    }
                                }
                                break;
                            }
                        }
                    }

                    cls = cls.getSuperclass();
                }
            }

            if (m_types.length == 0 && !(this instanceof QObject.Signal0))
                throw new RuntimeException("Signal initialization failed");

            return m_types;
        }

        protected void emit_helper(Object... args) {
            if (qobject().signalsBlocked())
                return;

            if (m_inEmit)
                return;

            m_inEmit = true; // recursion block

            for (Connection c : m_connections) {

                if (m_in_cpp_emission && slotIsCppEmit(c))
                    continue ;

                if (c.isDirectConnection()
                        || (c.isAutoConnection()
                                && c.receiver instanceof QtObject
                                && ((QtObject) c.receiver).thread() == Thread.currentThread()
                                && ((QtObject) c.receiver).thread() == qobject().thread())) {
                    try {
                        boolean updateSender = c.receiver instanceof QObject;
                        QObject oldSender = null;
                        if (updateSender) {
                            oldSender = QtJambiInternal.swapQObjectSender((QObject) c.receiver,
                                                                           qobject(), true);
                        }

                        try {
                            if (args.length == c.convertTypes.length) {
                                // Same number of arguments... direct call
                                c.slot.invoke(c.receiver, args);
                            } else {
                                if (c.args == null)
                                    c.args = new Object[c.convertTypes.length];
                                System.arraycopy(args, 0, c.args, 0,
                                        c.args.length);
                                c.slot.invoke(c.receiver, c.args);
                            }
                        } catch (IllegalAccessException e) {
                            invokeSlot(c.receiver, c.slotId, c.returnType,
                                    args, c.convertTypes);
                        }

                        if (updateSender) {
                            QtJambiInternal.swapQObjectSender((QObject) c.receiver,
                                                              (QObject) oldSender, false);
                        }

                    } catch (Exception e) {
                        System.err.println("Exception caught while after "
                                           + "invoking slot:");
                        e.printStackTrace();
                    }
                } else {
                    if (c.receiver instanceof QObject) {
                        QMetaCallEvent event = new QMetaCallEvent(c, args);
                        QCoreApplication.postEvent(((QObject) c.receiver), event);
                    } else {
                        throw new RuntimeException(
                                "Queued connections only valid for QObjects");
                    }
                }
            }

            m_inEmit = false;
        }

        protected abstract QObject qobject();

        protected abstract void initSignals();

        private static Method findEmitMethod(QObject.AbstractSignal signal) {
            Method methods[] = signal.getClass().getDeclaredMethods();

            Method slotMethod = null;
            for (int i = 0; i < methods.length; ++i) {
                if (methods[i].getName().equals("emit")) {
                    slotMethod = methods[i];
                    break;
                }
            }
            return slotMethod;
        }

        private boolean matchSlot(Method slot) {
            Class<?> slotArguments[] = slot.getParameterTypes();
            Class<?> signalArguments[] = resolveSignal();
            int signalArrayDims[] = arrayDimensions();

            if (slotArguments.length > signalArguments.length)
                return false;

            for (int i = 0; i < slotArguments.length; ++i) {
                if (!matchTwoTypes(slotArguments[i],
                                   signalArguments[i],
                                   signalArrayDims[i])) {
                    return false;
                }
            }

            return true;
        }

        private boolean matchTwoTypes(Class<?> slotArgument,
                Class<?> signalArgument, int signalArrayDims) {
            if (slotArgument.isArray() || signalArrayDims < 0) {
                int slotArrayDims = 0;
                while (slotArgument.isArray()) {
                    slotArgument = slotArgument.getComponentType();
                    ++slotArrayDims;
                }

                if (slotArrayDims != signalArrayDims)
                    return false;
                else
                    return matchTwoTypes(slotArgument, signalArgument, 0);
            } else if (slotArgument.isPrimitive()) {
                return matchTwoTypes(getComplexType(slotArgument),
                        signalArgument, signalArrayDims);
            } else if (!slotArgument.isAssignableFrom(signalArgument)) {
                return false;
            }

            return true;
        }

        private static byte primitiveToByte(Class<?> primitiveType) {
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

        private static Class<?> getComplexType(Class<?> primitiveType) {
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

        private final void addConnection(Object receiver, Method slot,
                int connectionType) {

            if (!m_connected_to_cpp) {
                m_connected_to_cpp = true;
                initSignals();
            }

            Class<?> returnType = slot.getReturnType();
            byte returnSig;
            if (!returnType.isPrimitive())
                returnSig = 'L';
            else
                returnSig = primitiveToByte(returnType);

            try {
                slot.setAccessible(true);
            } catch (SecurityException e) {
                // We don't care about the exception, as we'll use a fall back
                // if the slot turns out to be inaccessible
            }

            m_connections.add(new Connection(receiver, slot, returnSig,(byte) connectionType));
        }

        private final boolean removeConnection(Object receiver, Method slot) {
            if (m_in_disconnect)
                return false;
            m_in_disconnect = true;

            if (!m_connected_to_cpp) {
                m_connected_to_cpp = true;
                initSignals();
            }

            ListIterator<Connection> i = m_connections.listIterator();
            boolean returned = false;
            while (i.hasNext()) {
                Connection c = i.next();

                if ((receiver == null || c.receiver == receiver)
                        && (slot == null || slot.equals(c.slot))) {
                    i.remove();
                    returned = true;
                }
            }

            if (receiver instanceof QObject || receiver == null) {
                String methodSignature = null;
                if (slot != null) {
                    methodSignature = slot.toString();
                    int paren_pos = methodSignature.indexOf('(');
                    methodSignature = methodSignature.substring(methodSignature.lastIndexOf(' ', paren_pos) + 1);
                }
                returned |= cppDisconnect(qobject(), this.fullName(), (QObject) receiver,
                                          methodSignature);
                if (receiver == null && slot == null)
                    m_connected_to_cpp = false;
            }


            m_in_disconnect = false;
            return returned;
        }

    }

    public static class QMetaCallEvent extends QEvent {

        public static final QEvent.Type MetaCallEventType = QEvent.Type
                .resolve(512);

        public QMetaCallEvent(InternalSignal.Connection connection,
                Object... arguments) {
            super(MetaCallEventType);
            this.arguments = arguments;
            this.connection = connection;
        }

        public final Object[] getArguments() {
            return arguments;
        }

        public final void setArguments(Object[] arguments) {
            this.arguments = arguments;
        }

        public final InternalSignal.Connection getConnection() {
            return connection;
        }

        public final void setConnection(InternalSignal.Connection connection) {
            this.connection = connection;
        }

        final void execute() {
            invokeSlot(connection.receiver, connection.slotId,
                    connection.returnType, arguments, connection.convertTypes);
        }

        private Object arguments[];
        private InternalSignal.Connection connection;
    }

    @SuppressWarnings("unused")
    private static InternalSignal lookupSignal(QObject qobject, String name)
    {
        if (name == null || qobject == null) {
            System.err.println("lookupSignal: Name or object is null");
            return null;
        }

        InternalSignal returned = null;
        for (Class cls = qobject.getClass();
             QObject.class.isAssignableFrom(cls) && returned == null;
             cls = cls.getSuperclass()) {

            Field f = null;
            try {
                f = cls.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                continue;
            }

            try {
                f.setAccessible(true);
            } catch (SecurityException e) { }

            if (InternalSignal.class.isAssignableFrom(f.getType())) {
                try {
                    returned = (InternalSignal) f.get(qobject);
                } catch (Exception e) {
                    returned = fetchSignal(qobject, f);
                }
            }
        }

        return returned;
    }

    private static Method lookupSlot(QObject qobject, String signature) {
        Class cls = qobject.getClass();

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
            boolean found = false;
            if (!m.getName().equals(functionName))
                continue;

            Class a[] = m.getParameterTypes();
            if (a.length != argumentTypes.length)
                continue;

            found = true;
            for (int i = 0; i < a.length; ++i) {
                String arg = a[i].getName();

                Class t = a[i];
                int dims = 0;
                while (t.isArray()) {
                    dims++;
                    t = t.getComponentType();
                }
                if (dims > 0)
                    arg = arg.substring(2, arg.length() - 1) + "[]";
                for (int j = 0; j < dims - 1; ++j)
                    arg = arg.substring(1) + "[]";

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

    private static native QObject nativeSwapQObjectSender(long receiver_id,
            long sender_id, boolean returnPreviousSender);

    public static QObject swapQObjectSender(QObject receiver, QObject newSender, boolean returnPreviousSender) {
        return nativeSwapQObjectSender(receiver.nativeId(),
                newSender != null ? newSender.nativeId() : 0, returnPreviousSender);
    }

    public static void disconnect(QObject sender, QObject receiver) {
        Class cls = sender.getClass();
        while (QObject.class.isAssignableFrom(cls)) {
            Field fields[] = cls.getDeclaredFields();

            for (Field f : fields) {
                if (isSignal(f.getType())) {
                    QObject.AbstractSignal signal = null;
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

    public static native QObject sender(QObject receiver);

    /**
     * Returns true if the class cl represents a Signal.
     */
    public static boolean isSignal(Class cl) {
        return QObject.AbstractSignal.class.isAssignableFrom(cl);
    }

    private static native QObject.AbstractSignal fetchSignal(QObject qobject,
            Field field);

    private static native long resolveSlot(Method method);

    private static native void invokeSlot(Object receiver, long m,
            byte returnType, Object args[], int slotTypes[]);

    private static native void setField(QObject object, Field f,
            QObject.AbstractSignal newValue);

    private static native boolean cppDisconnect(QObject sender, String signal, QObject receiver, String slot);

    /**
     * Initializes the signals for the object
     */
    public static boolean initializeSignals(QObject qobject) {
        Field fields[] = qobject.getClass().getFields();

        for (Field f : fields) {
            if (isSignal(f.getType())) {
                QObject.AbstractSignal newSignal = null;
                try {
                    newSignal = (QObject.AbstractSignal) f.getType()
                            .getConstructors()[0].newInstance(qobject);
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Error getting and calling constructor for signal: "
                                    + f.getName(), e);
                }

                try {
                    f.setAccessible(true);
                    f.set(qobject, newSignal);
                } catch (IllegalAccessException e) {
                    setField(qobject, f, newSignal);
                }
            }
        }

        return true;
    }

    /**
     * Searches the object's class and its superclasses for a method of the given name and returns
     * its signature.
     */
    static private HashMap<String, String> signalMethodSignatureCash = new HashMap<String, String>();
    public static String findSignalMethodSignature(QObject qobject, String name) throws NoSuchFieldException, IllegalAccessException {

        Class cls = qobject.getClass();
        String fullName = cls + "." + name;
        String found = signalMethodSignatureCash.get(fullName);

        if (found != null) {
            return found;
        }

        while (cls != null) {
            Method methods[] = cls.getDeclaredMethods();
            for (int i = 0; i < methods.length; ++i) {
                if (methods[i].getName().equals(name)) {
                    found = name + "(";

                    Class<?> params[] = methods[i].getParameterTypes();
                    for (int j = 0; j < params.length; ++j) {
                        if (j > 0)
                            found += ",";
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
        Iterator<QObject> it = children.iterator();
        while (it.hasNext()) {
            QObject current = it.next();
            if ((name == null || name.equals(current.objectName()))
                    && (cl == null || cl.isAssignableFrom(current.getClass())))
                matching.add(current);
            matching.addAll(current.findChildren(cl, name));
        }

        return matching;
    }

    public static List<QObject> findChildren(QObject qobject, Class<?> cl,
            QRegExp name) {
        List<QObject> children = qobject.children();
        List<QObject> matching = new ArrayList<QObject>();
        Iterator<QObject> it = children.iterator();
        while (it.hasNext()) {
            QObject current = it.next();
            if ((name == null || name.indexIn(current.objectName()) >= 0)
                    && (cl == null || cl.isAssignableFrom(current.getClass())))
                matching.add(current);
            matching.addAll(current.findChildren(cl, name));
        }

        return matching;
    }

    public static QObject findChild(QObject qobject, Class<?> cl, String name) {
        List<QObject> children = qobject.children();
        Iterator<QObject> it = children.iterator();
        while (it.hasNext()) {
            QObject current = it.next();
            if ((name == null || name.equals(current.objectName()))
                    && (cl == null || cl.isAssignableFrom(current.getClass()))) {
                return current;
            }
        }
        return null;
    }

    /**
     * Shows an about box for Qt Jambi
     */
    public static void aboutQtJambi() {
        QMessageBox mb = new QMessageBox(QApplication.activeWindow());
        mb.setWindowTitle("About Qt Jambi");
        mb
                .setText("<h3>About Qt Jambi</h3>"
                        + "<p>Qt Jambi is a Java toolkit based on Qt, a C++ toolkit for"
                        + " cross-platform application development.</p>"
                        + "<p>This program uses Qt version "
                        + QtInfo.versionString()
                        + ".</p>"
                        + "<p>Qt Jambi provides single-source "
                        + "portability across MS&nbsp;Windows, Mac&nbsp;OS&nbsp;X, "
                        + "Linux, and all major commercial Unix variants"
                        + "<p>Qt Jambi is a Trolltech product. See "
                        + "<tt>http://www.trolltech.com/</tt> for more information.</p>");
        mb.setIconPixmap(new QPixmap(
                "classpath:com/trolltech/images/qt-logo.png"));
        mb.exec();
        mb.dispose();
    }

    public native static Object createExtendedEnum(int value, int ordinal,
            Class cl, String name);



    private static HashMap<QWidget, List<QPainter> > painters = new HashMap<QWidget, List<QPainter>>();
    public static boolean beginPaint(QWidget widget, QPainter painter) {
        List<QPainter> l = painters.get(widget);
        if (l == null) {
            l = new LinkedList<QPainter>();
            painters.put(widget, l);
        }        
        if (l.contains(painter)) 
            throw new RuntimeException("Painter opened twice on the same widget");
        if (painter.isActive())
            throw new RuntimeException("Painter already active");
        l.add(painter);        
        return painter.begin((QPaintDeviceInterface) widget);
    }

    public static void endPaint(QWidget widget) {
        List <QPainter> ps = painters.get(widget);
        if (ps != null) {
            for (QPainter p : ps) {
                p.dispose();
            }
            painters.remove(widget);
        }
    }

}
