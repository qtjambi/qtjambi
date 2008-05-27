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
import com.trolltech.qt.internal.MetaObjectTools;
import com.trolltech.qt.internal.QSignalEmitterInternal;

import java.lang.reflect.*;
import java.util.*;

/**
 * This class implements the functionality to emit signals. All
 * objects in QtJambi can emit signals, so the class is inherited by
 * QtJambiObject.
 */
public class QSignalEmitter extends QSignalEmitterInternal {


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

    /**
      Searches recursively for all child objects of the given <tt>object</tt>, and connects matching signals from them to
      slots of <tt>object</tt> that follow the following form:

     <code>
            void on_&lt;widget name&gt;_&lt;signal name&gt;(&lt;signal parameters&gt;);
     </code>

      Let's assume our object has a child object of type <tt>QPushButton</tt> with the object name <tt>button1</tt>.
      The slot to catch the button's <tt>clicked()</tt> signal would be:

     <code>
            void on_button1_clicked();
     </code>
    */
    public static void connectSlotsByName(QObject object) {
        com.trolltech.qt.internal.QtJambiInternal.connectSlotsByName(object);
    }


    private boolean signalsBlocked = false;
}
