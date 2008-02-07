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

import com.trolltech.qt.QtJambiInternal.*;
import com.trolltech.qt.core.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * This class implements the functionality to emit signals. All
 * objects in QtJambi can emit signals, so the class is inherited by
 * QtJambiObject.
 */
public class QSignalEmitter {

    static class ResolvedSignal {
        Class<?> types[] = new Class[0];
        int arrayDimensions[] = new int[0];
        String name = "";
    }

    /* friendly */ static ResolvedSignal resolveSignal(Field field, Class<?> declaringClass) {
        ResolvedSignal resolvedSignal = new ResolvedSignal();
        resolvedSignal.name = field.getName();

        Type t = field.getGenericType();

        // either t is a parameterized type, or it is Signal0
        if (t instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) t;
            Type actualTypes[] = p.getActualTypeArguments();

            resolvedSignal.types = new Class[actualTypes.length];
            resolvedSignal.arrayDimensions = new int[actualTypes.length];
            for (int j = 0; j < resolvedSignal.types.length; ++j) {

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
                    resolvedSignal.types[j] = (Class<?>) actualType;
                    resolvedSignal.arrayDimensions[j] = arrayDims;
                } else {
                    throw new RuntimeException(
                            "Signals of generic types not supported: "
                                    + actualTypes[j]
                                    .toString());
                }
            }
        }

        return resolvedSignal;
    }



    /**
     * QSignalEmitter is a class used internally by Qt Jambi.
     * You should never have to concern yourself with this class.
     * @exclude
     */
    public abstract class AbstractSignal {

        private boolean             inCppEmission       = false;
        private List<Connection>    connections         = new ArrayList<Connection>();
        private Class<?>            types[]             = null;
        private int                 arrayDimensions[]   = null;
        private String              name                = "";
        private Class<?>            declaringClass      = null;
        private boolean             connectedToCpp      = false;
        private boolean             inDisconnect        = false;
        
        @SuppressWarnings("unused")
        private boolean 			inJavaEmission	    = false;

        @SuppressWarnings("unused")
        private int                 cppConnections      = 0;

        /**
         * Contains book holding info about a single connection
         * @exclude
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
                this.slotId = QtJambiInternal.resolveSlot(slot);
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
                convertTypes = QtJambiInternal.resolveConversionSchema(signalParameterTypes, slotParameterTypes);
            }
        } // public class Connection


        /**
         * Returns the object containing this signal
         *
         * @exclude
         */
        public final QSignalEmitter containingObject() {
            return QSignalEmitter.this;
        }

        /**
         * Connects the signal to a method in an object. Whenever it is emitted, the method will be invoked
         * on the given object.
         *
         * @param receiver  The object that owns the method
         * @param method    The signature of the method excluding return type and argument names, such as "setText(String)".
         * @param type      One of the connection types defined in the Qt interface.
         * @throws QNoSuchSlotException Raised if the method passed in the slot object was not found
         * @throws java.lang.RuntimeException Raised if the signal object could not be successfully introspected or if the
         *                                    signatures of the signal and slot are incompatible.
         */
        public final void connect(Object receiver, String method,
                                     Qt.ConnectionType type) {
            if (receiver == null)
                throw new NullPointerException("Receiver must be non-null");

            Method slotMethod = QtJambiInternal.lookupSlot(receiver, method);
            if (slotMethod == null)
                throw new QNoSuchSlotException(receiver, method);

            connectSignalMethod(slotMethod, receiver, type.value());
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
        public final boolean disconnect(Object receiver, String method) {
            if (method != null && receiver == null)
                throw new IllegalArgumentException("Receiver cannot be null if you specify a method");

            Method slotMethod = null;
            if (method != null) {
                slotMethod = QtJambiInternal.lookupSlot(receiver, method);
                if (slotMethod == null)
                    throw new QNoSuchSlotException(receiver, method);
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
         *  @see #disconnect(Object, String)
         **/
        public final boolean disconnect(Object receiver) {
            return disconnect(receiver, null);
        }

        /**
         * Removes all connections from this signal.
         *
         * @return  True if the disconnection was successful.
         * @see #disconnect(Object, String)
         */
        public final boolean disconnect() {
            return disconnect(null, null);
        }

        /**
         * Creates an auto-connection from this signal to the specified object and method.
         *
         * @param receiver The object that owns the method
         * @param method The signature of the method excluding return type and argument names, such as "setText(String)".
         *
         * @see #connect(Object, String, com.trolltech.qt.core.Qt.ConnectionType)
         **/
        public final void connect(Object receiver, String method) {
            connect(receiver, method, Qt.ConnectionType.AutoConnection);
        }

        /**
         * Creates an auto connection from this signal to another. Whenever this signal is emitted, it will cause the second
         * signal to be emitted as well.
         *
         * @param signalOut The second signal. This will be emitted whenever this signal is emitted.
         * @throws RuntimeException Raised if either of the signal objects could not be successfully be introspected or if their
         *                                    signatures are incompatible.
         */
        public final void connect(AbstractSignal signalOut) {
            connect(signalOut, Qt.ConnectionType.AutoConnection);
        }

        /**
         * Creates a connection from this signal to another. Whenever this signal is emitted, it will cause the second
         * signal to be emitted as well.
         *
         * @param signalOut The second signal. This will be emitted whenever this signal is emitted.
         * @param type      One of the connection types defined in the Qt interface.
         * @throws RuntimeException Raised if either of the signal objects could not be successfully be introspected or if their
         *                                    signatures are incompatible.
         */
        public final void connect(AbstractSignal signalOut, Qt.ConnectionType type) {
            connectSignalMethod(QtJambiInternal.findEmitMethod(signalOut), signalOut,
                    type.value());
        }

        /**
         * Disconnects a signal from another signal if the two were previously connected by a call to connect.
         * A call to this function will assure that the emission of the first signal will not cause the emission of the second.
         *
         * @param signalOut The second signal.
         * @return true if the two signals were successfully disconnected, or false otherwise.
         */
        public final boolean disconnect(AbstractSignal signalOut) {
            return removeConnection(signalOut, QtJambiInternal.findEmitMethod(signalOut));
        }

        /**
         * Returns the name of the signal
         * @return The signal name
         */
        public final String name() {
            resolveSignal();
            return name;
        }

        /**
         * Returns the name of the class containing the signal
         * @return The fully qualified name of the class declaring the signal
         */
        public final String declaringClassName() {
            resolveSignal();
            return declaringClass == null ? "" : declaringClass.getName();
        }

        /**
         * Returns the full name of the signal, on the form "package.class.signalName"
         *
         *  @return The fully qualified name of the signal
         */
        public final String fullName() {
            return declaringClassName() + "." + name();
        }

        /**
         * @return True if the signal is generated (declared in a generated class)
         */
        private boolean isGenerated() {
            resolveSignal();
            return declaringClass == null ? false : declaringClass.isAnnotationPresent(QtJambiGeneratedClass.class);
        }

        /**
         * Returns true if the connection receiver is the emission of the C++ version of the current
         * signal. This is used to avoid recursion from C++ emissions. Whenever we have a C++ emission
         * we know we will have a function with the same name in the same declaring class as the signal.
         *
         * @param connection The connection to check
         * @return True if true...
         */
        private boolean slotIsCppEmit(Connection connection) {
            return (connection.slot.getName().equals(name())
                    && connection.receiver == QSignalEmitter.this
                    && connection.slot.getDeclaringClass().equals(declaringClass));
        }

        private void connectSignalMethod(Method slotMethod,
                                            Object receiver,
                                            int connectionType) {
            if (slotMethod.getAnnotation(QtBlockedSlot.class) != null)
                throw new QNoSuchSlotException(slotMethod.toString());

            if (!matchSlot(slotMethod))
                throw new RuntimeException("Signature of signal '" + fullName() + "' does not match slot '" + slotMethod.toString() + "'");

            addConnection(receiver, slotMethod, connectionType);
        }

        /**
         * Array dimensions for each of the signal arguments in order of declaration. Used in combination with
         * resolveSignal(). An array dimension of 0 means the argument is not of an array type.
         *
         * @return An array of integers indicating the number of dimensions of each of the signal arguments
         */
        /* friendly */ int[] arrayDimensions() {
            resolveSignal();
            return arrayDimensions;
        }

        /**
         * Base types of all signal arguments in order of declaration. If the argument is of an array type,
         * then the base type of the array is returned by resolveSignal, and the actual number of dimensions
         * of the array can be retrieved using arrayDimensions(). If the argument is not of an array type,
         * the argument's type is returned.
         *
         * @return An array of Class objects specifying the base type of each of the signal arguments.
         */
        /* friendly */ Class<?>[] resolveSignal() {
            if (types == null) {
                boolean found = false;
                types = new Class[0]; // For signals with no parameters
                arrayDimensions = new int[0];

                Class<?> cls = QSignalEmitter.this.getClass();
                while (cls != null) {
                    Field fields[] = cls.getDeclaredFields();
                    for (Field field : fields) {
                        if (AbstractSignal.class.isAssignableFrom(field.getType())) {
                            AbstractSignal sig = QtJambiInternal.fetchSignal(QSignalEmitter.this, field);
                            if (sig == this) {
                                found = true;
                                declaringClass = field.getDeclaringClass();

                                ResolvedSignal resolvedSignal = QSignalEmitter.resolveSignal(field, declaringClass);

                                name = resolvedSignal.name;
                                types = resolvedSignal.types;
                                arrayDimensions = resolvedSignal.arrayDimensions;

                                break;
                            }
                        }
                    }

                    cls = cls.getSuperclass();
                }

                if (!found) {
                    throw new RuntimeException("Signals must be declared as members of QSignalEmitter subclasses");
                }
            }

            if (types.length == 0 && !(this instanceof Signal0))
                throw new RuntimeException("Signal initialization failed");

            return types;
        }

        // Cache string containing list of Java argument types for signal
        private String signalParameters = null;
        private String signalParameters() {
            if (signalParameters == null)
                signalParameters = QtJambiInternal.signalParameters(this);

            return signalParameters;
        }


        // Cache string containing cpp signature for signal
        private String cppSignalSignature = null;
        private String cppSignalSignature() {
            if (cppSignalSignature == null)
                cppSignalSignature = QtJambiInternal.cppSignalSignature(this);

            return cppSignalSignature;
        }

        /**
         * @exclude
         */
        protected synchronized final void emit_helper(Object ... args) {
            if (QSignalEmitter.this.signalsBlocked())
                return;

            List<Connection> cons = connections;
            List<Connection> toRemove = null;

            // If the signal is generated, it will automatically be connected
            // to the original C++ function for the signal, so the native
            // signal will be emitted by this mechanism. In other cases, we
            // need to make magic and dynamically fake a signal emission
            // in c++ for the signal.
            if (!isGenerated() && QSignalEmitter.this instanceof QObject) {
                QtJambiInternal.emitNativeSignal((QObject) QSignalEmitter.this, name() + "(" + signalParameters() + ")", cppSignalSignature(), args);
            }

            inJavaEmission = true;
            try {
	            for (Connection c : cons) {
	
	                // If the receiver has been deleted we take the connection out of the list
	                if (c.receiver instanceof QtJambiObject && ((QtJambiObject)c.receiver).nativeId() == 0) {
	                    if (toRemove == null)
	                        toRemove = new ArrayList<Connection>();
	                    toRemove.add(c);
	                    continue;
	                }
	
	                if (inCppEmission && slotIsCppEmit(c))
	                    continue;
	
	
	                if (args.length == c.convertTypes.length) {
	                    c.args = args;
	                } else {
	                    if (c.args == null)
	                        c.args = new Object[c.convertTypes.length];
	                    System.arraycopy(args, 0, c.args, 0, c.args.length);
	                }
	
	                // We do a direct connection in three cases:
	                // 1. If the connection is explicitly set to be direct
	                // 2. If it is automatic and the receiver is not a QObject (no thread() function)
	                // 3. If it is automatic, the receiver is a QObject and the sender and receiver
	                //    are both in the current thread
	                if (c.isDirectConnection()
	                        || (c.isAutoConnection()
	                            && !(c.receiver instanceof QSignalEmitter))
	                        || (c.isAutoConnection()
	                                && c.receiver instanceof QSignalEmitter
	                                && ((QSignalEmitter) c.receiver).thread() == Thread.currentThread()
	                                && ((QSignalEmitter) c.receiver).thread() == thread())) {
	                    QSignalEmitter oldEmitter = currentSender.get();
	                    currentSender.set(QSignalEmitter.this);
	                    try {
	                        boolean updateSender = c.receiver instanceof QObject && QSignalEmitter.this instanceof QObject;
	                        long oldSender = 0;
	                        if (updateSender) {
	                            oldSender = QtJambiInternal.setQObjectSender(((QObject) c.receiver).nativeId(),
	                                                                         ((QObject) QSignalEmitter.this).nativeId());
	                        }
	
	                        try {
	                            c.slot.invoke(c.receiver, c.args);
	                        } catch (IllegalAccessException e) {
	                            QtJambiInternal.invokeSlot(c.receiver, c.slotId, c.returnType,
	                                    c.args, c.convertTypes);
	                        }
	
	                        if (updateSender) {
	                            QtJambiInternal.resetQObjectSender(((QObject) c.receiver).nativeId(),
	                                                              oldSender);
	                        }
	
	                    } catch (InvocationTargetException e) {
	                        System.err.println("Exception caught after invoking slot");
	                        e.getCause().printStackTrace();
	
	                    } catch (Exception e) {
	                        System.err.println("Exception caught after invoking slot:");
	                        e.printStackTrace();
	                    }
	                    currentSender.set(oldEmitter);
	                } else {
	
	                    QObject sender = null;
	                    if(c.receiver instanceof QObject && QSignalEmitter.this instanceof QObject) {
	                        sender = (QObject) QSignalEmitter.this;
	                    }
	
	                    QMetaCallEvent event = new QMetaCallEvent(c, sender, c.args);
	                    QObject eventReceiver = null;
	                    if (c.receiver instanceof QObject)
	                        eventReceiver = (QObject) c.receiver;
	                    else
	                        eventReceiver = QCoreApplication.instance();
	
	                    QCoreApplication.postEvent(eventReceiver, event);
	                }
	            }
	
	            // Remove the ones marked for removal..
	            removeConnection_helper(toRemove);

            } finally {
            	inJavaEmission = false;
            }
        }

        private boolean matchSlot(Method slot) {
            Class<?> slotArguments[] = slot.getParameterTypes();
            Class<?> signalArguments[] = resolveSignal();
            int signalArrayDims[] = arrayDimensions();

            if (slotArguments.length > signalArguments.length){
                return false;
            }

            for (int i = 0; i < slotArguments.length; ++i) {
                if (!matchTwoTypes(slotArguments[i],
                                   signalArguments[i],
                                   signalArrayDims[i])) {
                    return false;
                }
            }

            return true;
        }

        private boolean matchTwoTypes(Class<?> slotArgument, Class<?> signalArgument, int signalArrayDims) {
            return matchTwoTypes(slotArgument, signalArgument, signalArrayDims, false);
        }

        private boolean matchTwoTypes(Class<?> slotArgument,
                                      Class<?> signalArgument,
                                      int signalArrayDims,
                                      boolean wasArray) {

            if (slotArgument.isArray() || signalArrayDims < 0) {
                int slotArrayDims = 0;
                while (slotArgument.isArray()) {
                    slotArgument = slotArgument.getComponentType();
                    ++slotArrayDims;
                }
                return slotArrayDims == signalArrayDims && matchTwoTypes(slotArgument, signalArgument, 0, true);
            } else if (slotArgument.isPrimitive() && !wasArray) {
                return matchTwoTypes(QtJambiInternal.getComplexType(slotArgument),
                        signalArgument, signalArrayDims);
            } else if (!slotArgument.isAssignableFrom(signalArgument)) {
                return false;
            }

            return true;
        }

        private synchronized void addConnection(Object receiver, Method slot,
                int connectionType) {

            if (!connectedToCpp) {
                connectedToCpp = true;
                __qt_signalInitialization(name());
            }

            Class<?> returnType = slot.getReturnType();
            byte returnSig;
            if (!returnType.isPrimitive())
                returnSig = 'L';
            else
                returnSig = QtJambiInternal.primitiveToByte(returnType);

            try {
                slot.setAccessible(true);
            } catch (SecurityException e) {
                // We don't care about the exception, as we'll use a fall back
                // if the slot turns out to be inaccessible
            }


            List<Connection> newList = cloneConnections();
            newList.add(new Connection(receiver, slot, returnSig,(byte) connectionType));
            connections = newList;
        }

        private List<Connection> cloneConnections() {
            List<Connection> newList = new ArrayList<Connection>();
            newList.addAll(connections);
            return newList;
        }

        private synchronized boolean removeConnection(Object receiver, Method slot) {
            if (inDisconnect)
                return false;
            inDisconnect = true;

            if (!connectedToCpp) {
                connectedToCpp = true;
                __qt_signalInitialization(name());
            }

            List<Connection> toRemove = null;
            boolean returned = false;
            for (Connection c : connections) {
                if ((receiver == null || c.receiver == receiver)
                    && (slot == null || slot.equals(c.slot))) {
                    if (toRemove == null)
                        toRemove = new ArrayList<Connection>();
                    toRemove.add(c);
                    returned = true;
                }
            }

            removeConnection_helper(toRemove);

            if (QSignalEmitter.this instanceof QObject && (receiver instanceof QObject || receiver == null)) {
                String methodSignature = null;
                if (slot != null) {
                    methodSignature = slot.toString();
                    int paren_pos = methodSignature.indexOf('(');
                    methodSignature = methodSignature.substring(methodSignature.lastIndexOf(' ', paren_pos) + 1);
                }
                returned |= QtJambiInternal.cppDisconnect((QObject) QSignalEmitter.this, this.fullName(), (QObject) receiver,
                                                          methodSignature);
                if (receiver == null && slot == null)
                    connectedToCpp = false;
            }


            inDisconnect = false;
            return returned;
        }

        private void removeConnection_helper(List<Connection> toRemove) {
            if (toRemove != null) {
                List<Connection> newList = cloneConnections();
                for (Connection c : toRemove)
                    newList.remove(c);
                connections = newList;
            }
        }
    }

    /**
     * Declare and instantiate a field of this class in your QSignalEmitter subclass to declare a signal that takes
     * no parameters.
     */
    public final class Signal0 extends AbstractSignal {
        public Signal0() {
            super();
        }

        /**
         * Emits the signal.
         */
        public void emit() {
            emit_helper();
        }
    }

    /**
     * Declare and instantiate a field of this class in your QSignalEmitter subclass to declare a signal that takes
     * one parameter.
     *
     * @param <A> The type of the single parameter of the signal.
     */
    public final class Signal1<A> extends AbstractSignal {
        public Signal1() {
            super();
        }

        /**
         * Emits the signal.
         */
        public void emit(A arg1) {
            emit_helper(arg1);
        }

    }

    /**
     * Declare and instantiate a field of this class in your QSignalEmitter subclass to declare a signal that takes
     * two parameters.
     *
     * @param <A> The type of the first parameter of the signal.
     * @param <B> The type of the second parameter of the signal.
     */
    public final class Signal2<A, B> extends AbstractSignal {
        public Signal2() {
            super();
        }

        /**
         * Emits the signal.
         */
        public void emit(A arg1, B arg2) {
            emit_helper(arg1, arg2);
        }
    }

    /**
     * Declare and instantiate a field of this class in your QSignalEmitter subclass to declare a signal that takes
     * three parameters.
     *
     * @param <A> The type of the first parameter of the signal.
     * @param <B> The type of the second parameter of the signal.
     * @param <C> The type of the third parameter of the signal.
     */
    public final class Signal3<A, B, C> extends AbstractSignal {
        public Signal3() {
            super();
        }

        /**
         * Emits the signal.
         */
        public void emit(A arg1, B arg2, C arg3) {
            emit_helper(arg1, arg2, arg3);
        }
    }

    /**
     * Declare and instantiate a field of this class in your QSignalEmitter subclass to declare a signal that takes
     * four parameters.
     *
     * @param <A> The type of the first parameter of the signal.
     * @param <B> The type of the second parameter of the signal.
     * @param <C> The type of the third parameter of the signal.
     * @param <D> The type of the fourth parameter of the signal.
     */

    public final class Signal4<A, B, C, D> extends AbstractSignal {
        public Signal4() {
            super();
        }

        /**
         * Emits the signal.
         */
        public void emit(A arg1, B arg2, C arg3, D arg4) {
            emit_helper(arg1, arg2, arg3, arg4);
        }
    }

    /**
     * Declare and instantiate a field of this class in your QSignalEmitter subclass to declare a signal that takes
     * five parameters.
     *
     * @param <A> The type of the first parameter of the signal.
     * @param <B> The type of the second parameter of the signal.
     * @param <C> The type of the third parameter of the signal.
     * @param <D> The type of the fourth parameter of the signal.
     * @param <E> The type of the fifth parameter of the signal.
     */
    public final class Signal5<A, B, C, D, E> extends AbstractSignal {
        public Signal5() {
            super();
        }

        /**
         * Emits the signal.
         */
        public void emit(A arg1, B arg2, C arg3, D arg4, E arg5) {
            emit_helper(arg1, arg2, arg3, arg4, arg5);
        }
    }

    /**
     * Declare and instantiate a field of this class in your QSignalEmitter subclass to declare a signal that takes
     * six parameters.
     *
     * @param <A> The type of the first parameter of the signal.
     * @param <B> The type of the second parameter of the signal.
     * @param <C> The type of the third parameter of the signal.
     * @param <D> The type of the fourth parameter of the signal.
     * @param <E> The type of the fifth parameter of the signal.
     * @param <F> The type of the sixth parameter of the signal.
     */
    public final class Signal6<A, B, C, D, E, F> extends AbstractSignal {
        public Signal6() {
            super();
        }

        /**
         * Emits the signal.
         */
        public void emit(A arg1, B arg2, C arg3, D arg4, E arg5, F arg6) {
            emit_helper(arg1, arg2, arg3, arg4, arg5, arg6);
        }
    }

    /**
     * Declare and instantiate a field of this class in your QSignalEmitter subclass to declare a signal that takes
     * seven parameters.
     *
     * @param <A> The type of the first parameter of the signal.
     * @param <B> The type of the second parameter of the signal.
     * @param <C> The type of the third parameter of the signal.
     * @param <D> The type of the fourth parameter of the signal.
     * @param <E> The type of the fifth parameter of the signal.
     * @param <F> The type of the sixth parameter of the signal.
     * @param <G> The type of the seventh parameter of the signal.
     */
    public final class Signal7<A, B, C, D, E, F, G> extends AbstractSignal {
        public Signal7() {
            super();
        }

        /**
         * Emits the signal.
         */
        public void emit(A arg1, B arg2, C arg3, D arg4, E arg5, F arg6, G arg7) {
            emit_helper(arg1, arg2, arg3, arg4, arg5, arg6, arg7);
        }
    }

    /**
     * Declare and instantiate a field of this class in your QSignalEmitter subclass to declare a signal that takes
     * eight parameters.
     *
     * @param <A> The type of the first parameter of the signal.
     * @param <B> The type of the second parameter of the signal.
     * @param <C> The type of the third parameter of the signal.
     * @param <D> The type of the fourth parameter of the signal.
     * @param <E> The type of the fifth parameter of the signal.
     * @param <F> The type of the sixth parameter of the signal.
     * @param <G> The type of the seventh parameter of the signal.
     * @param <H> The type of the eighth parameter of the signal.
     */
    public final class Signal8<A, B, C, D, E, F, G, H> extends
            AbstractSignal {
        public Signal8() {
            super();
        }

        /**
         * Emits the signal.
         */
        public void emit(A arg1, B arg2, C arg3, D arg4, E arg5, F arg6,
                G arg7, H arg8) {
            emit_helper(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
        }
    }

    /**
     * Declare and instantiate a field of this class in your QSignalEmitter subclass to declare a signal that takes
     * nine parameters.
     *
     * @param <A> The type of the first parameter of the signal.
     * @param <B> The type of the second parameter of the signal.
     * @param <C> The type of the third parameter of the signal.
     * @param <D> The type of the fourth parameter of the signal.
     * @param <E> The type of the fifth parameter of the signal.
     * @param <F> The type of the sixth parameter of the signal.
     * @param <G> The type of the seventh parameter of the signal.
     * @param <H> The type of the eighth parameter of the signal.
     * @param <I> The type of the ninth parameter of the signal.
     */
    public final class Signal9<A, B, C, D, E, F, G, H, I> extends
            AbstractSignal {
        public Signal9() {
            super();
        }

        /**
         * Emits the signal.
         */
        public void emit(A arg1, B arg2, C arg3, D arg4, E arg5, F arg6,
                G arg7, H arg8, I arg9) {
            emit_helper(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
        }
    }


    /**
     * Returns the thread affinity of the object. If this is an instance of
     * QObject the thread that owns the object is returned. For non-QObjects
     * the current thread is returned.
     */
    public Thread thread() { return Thread.currentThread(); }

    /**
     * Returns true if this QSignalEmitter is blocked. If it is
     * blocked, no signals will be emitted.
     */
    public boolean signalsBlocked() {
        return signalsBlocked;
    }

    /**
     * Blocks this QSignalEmitter from emiting its signals.
     */
    public boolean blockSignals(boolean b) {
        boolean returned = signalsBlocked;
        signalsBlocked = b;
        return returned;
    }

    /**
     * @exclude
     */
    protected boolean __qt_signalInitialization(String name) {
        return false;
    }

    /**
     * If a signal is currently being emitted (e.g. if this method is called from within a slot that has been invoked by a signal),
     * then this function will return the object containing the signal that was emitted.
     * @return Current sender, or null if a signal is not currently being emitted.
     */
    public static QSignalEmitter signalSender() {
        return currentSender.get();
    }

    /**
     * Disconnect all connections originating in this signal emitter.
     */
    public final void disconnect() {
        disconnect(null);
    }

    /**
     * Disconnect all connections made from this signal emitter to a specific object.
     *
     * @param other The receiver to disconnect, or null to disconnect all receivers
     */
    public final void disconnect(Object other) {
        QtJambiInternal.disconnect(this, other);
    }

    static ThreadLocal<QSignalEmitter> currentSender = new ThreadLocal<QSignalEmitter>();
    private boolean signalsBlocked = false;
}
