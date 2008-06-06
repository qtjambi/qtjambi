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

import java.lang.reflect.*;

import com.trolltech.qt.core.Qt;
import com.trolltech.qt.internal.*;

/**
 * This class implements the functionality to emit signals. All
 * objects in QtJambi can emit signals, so the class is inherited by
 * QtJambiObject.
 */
public class QSignalEmitter extends QSignalEmitterInternal {


    public abstract class AbstractSignal extends QSignalEmitter.AbstractSignalInternal {

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

        Method slotMethod = com.trolltech.qt.internal.QtJambiInternal.lookupSlot(receiver, method);
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
            slotMethod = com.trolltech.qt.internal.QtJambiInternal.lookupSlot(receiver, method);
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
        connectSignalMethod(com.trolltech.qt.internal.QtJambiInternal.findEmitMethod(signalOut), signalOut,
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
        return removeConnection(signalOut, com.trolltech.qt.internal.QtJambiInternal.findEmitMethod(signalOut));
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
    @Override
    public Thread thread() { return Thread.currentThread(); }

    /**
     * Returns true if this QSignalEmitter is blocked. If it is
     * blocked, no signals will be emitted.
     */
    @Override
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
     * If a signal is currently being emitted (e.g. if this method is called from within a slot that has been invoked by a signal),
     * then this function will return the object containing the signal that was emitted.
     * @return Current sender, or null if a signal is not currently being emitted.
     */
    public static QSignalEmitter signalSender() {
        return (QSignalEmitter) currentSender.get();
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
        com.trolltech.qt.internal.QtJambiInternal.disconnect(this, other);
    }

    private boolean signalsBlocked = false;
}
