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

package com.trolltech.qt.internal;

import com.trolltech.qt.*;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.QObject;
import com.trolltech.qt.core.QCoreApplication;

import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public abstract class QSignalEmitterInternal {
    /**
     * QSignalEmitter is a class used internally by Qt Jambi.
     * You should never have to concern yourself with this class.
     * @exclude
     */
    public abstract class AbstractSignalInternal {

        private boolean             inCppEmission       = false;
        private List<Connection> connections         = null;
        private Class<?>            types[]             = null;
        private int                 arrayDimensions[]   = null;
        private String              name                = "";
        private Class<?>            declaringClass      = null;
        private boolean             connectedToCpp      = false;
        private boolean             inDisconnect        = false;
        private boolean             inJavaEmission      = false;

        /**
         * Contains book holding info about a single connection
         * @exclude
         */
        protected class Connection {
            public int      flags           = 0;
            public Object   receiver        = null;
            public Method slot            = null;
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
                this.slotId = com.trolltech.qt.internal.QtJambiInternal.resolveSlot(slot);
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
        public final QSignalEmitterInternal containingObject() {
            return QSignalEmitterInternal.this;
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
                    && connection.receiver == QSignalEmitterInternal.this
                    && connection.slot.getDeclaringClass().equals(declaringClass));
        }

        protected void connectSignalMethod(Method slotMethod,
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
        int[] arrayDimensions() {
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
        Class<?>[] resolveSignal() {
            if (types == null) {
                boolean found = false;
                types = new Class[0]; // For signals with no parameters
                arrayDimensions = new int[0];

                Class<?> cls = QSignalEmitterInternal.this.getClass();
                while (cls != null) {
                    Field fields[] = cls.getDeclaredFields();
                    for (Field field : fields) {
                        if (AbstractSignalInternal.class.isAssignableFrom(field.getType())) {
                            AbstractSignalInternal sig = QtJambiInternal.fetchSignal(QSignalEmitterInternal.this, field);
                            if (sig == this) {
                                found = true;
                                declaringClass = field.getDeclaringClass();

                                QtJambiInternal.ResolvedSignal resolvedSignal = QtJambiInternal.resolveSignal(field, declaringClass);

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

            if (types.length == 0
                && !(this instanceof QSignalEmitter.Signal0)
                && !(this instanceof QSignalEmitter.PrivateSignal0))
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
                cppSignalSignature = MetaObjectTools.cppSignalSignature(this);

            return cppSignalSignature;
        }

        /**
         * @exclude
         */
        protected synchronized final void emit_helper(Object ... args) {

            // When you dispose() a QObject, the first thing that happens is
            // that the native ID is set to 0. Then we proceed to delete the
            // native object. If this has a destructor which emits signals,
            // they will be passed into Java, which will lead to a threadCheck()
            // from signalsBlocked, and you will get an exception. We cannot
            // support listening to signals emitted in the C++ destructor.
            if (QSignalEmitterInternal.this instanceof QObject
                && (((QObject) QSignalEmitterInternal.this).nativeId()) == 0) {
                return ;
            }

            if (QSignalEmitterInternal.this.signalsBlocked())
                return;

            List<Connection> cons = connections;
            List<Connection> toRemove = null;

            // If the signal is generated, it will automatically be connected
            // to the original C++ function for the signal, so the native
            // signal will be emitted by this mechanism. In other cases, we
            // need to make magic and dynamically fake a signal emission
            // in c++ for the signal.
            if (!isGenerated() && QSignalEmitterInternal.this instanceof QObject) {
                MetaObjectTools.emitNativeSignal((QObject) QSignalEmitterInternal.this, name() + "(" + signalParameters() + ")", cppSignalSignature(), args);
            }

            if (connections == null)
                return;


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
                        QSignalEmitterInternal oldEmitter = currentSender.get();
                        currentSender.set(QSignalEmitterInternal.this);
                        try {
                            boolean updateSender = c.receiver instanceof QObject && QSignalEmitterInternal.this instanceof QObject;
                            long oldSender = 0;
                            if (updateSender) {
                                oldSender = QtJambiInternal.setQObjectSender(((QObject) c.receiver).nativeId(),
                                                                             ((QObject) QSignalEmitterInternal.this).nativeId());
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
                        if(c.receiver instanceof QObject && QSignalEmitterInternal.this instanceof QObject) {
                            sender = (QObject) QSignalEmitterInternal.this;
                        }

                        QtJambiInternal.QMetaCallEvent event = new QtJambiInternal.QMetaCallEvent(c, sender, c.args);
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
                returnSig = com.trolltech.qt.internal.QtJambiInternal.primitiveToByte(returnType);

            try {
                slot.setAccessible(true);
            } catch (SecurityException e) {
                // We don't care about the exception, as we'll use a fall back
                // if the slot turns out to be inaccessible
            }


            List<Connection> newList = cloneConnectionsForceInstance();
            newList.add(new Connection(receiver, slot, returnSig,(byte) connectionType));
            connections = newList;
        }


        private List<Connection> cloneConnectionsForceInstance() {
            List<Connection> newList = new ArrayList<Connection>();
            if (connections != null)
                newList.addAll(connections);
            return newList;
        }

        private List<Connection> cloneConnections() {
            if (connections == null)
                return null;
            List<Connection> newList = new ArrayList<Connection>();
            newList.addAll(connections);
            return newList;
        }

        protected synchronized boolean removeConnection(Object receiver, Method slot) {
            if (inDisconnect)
                return false;
            inDisconnect = true;

            if (!connectedToCpp) {
                connectedToCpp = true;
                __qt_signalInitialization(name());
            }

            boolean returned = false;
            if (connections != null) {
                List<Connection> toRemove = null;
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
            }

            if (QSignalEmitterInternal.this instanceof QObject && (receiver instanceof QObject || receiver == null)) {
                String methodSignature = null;
                if (slot != null) {
                    methodSignature = slot.toString();
                    int paren_pos = methodSignature.indexOf('(');
                    methodSignature = methodSignature.substring(methodSignature.lastIndexOf(' ', paren_pos) + 1);
                }
                returned |= com.trolltech.qt.internal.QtJambiInternal.cppDisconnect((QObject) QSignalEmitterInternal.this,
                        this.fullName(), (QObject) receiver, methodSignature);
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
     * @exclude
     */
    protected boolean __qt_signalInitialization(String name) {
        return false;
    }

    public abstract boolean signalsBlocked();

    public abstract Thread thread();

    protected static ThreadLocal<QSignalEmitterInternal> currentSender = new ThreadLocal<QSignalEmitterInternal>();

}
