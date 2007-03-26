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
import com.trolltech.qt.gui.*;

import java.lang.reflect.*;
import java.util.*;

public class QtJambiInternal {

    static {
        QtJambi_LibraryInitializer.init();
    }

    public static final char SlotPrefix = '1';
    public static final char SignalPrefix = '2';

    public static class QMetaCallEvent extends QEvent {

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
            } catch (InvocationTargetException e) {
                System.err.printf("Exception while executing queued connection: sender=%s, receiver=%s %s\n",
                        sender != null ? sender.getClass().getName() : "N/A",
                        connection.receiver,
                        connection.slot.toString());
                e.getCause().printStackTrace();
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

    @SuppressWarnings("unused")
    private static void endPaint(QWidget widget) {
        List <QPainter> ps = painters.get(widget);
        if (ps != null) {
            for (QPainter p : ps) {
                p.dispose();
            }
            painters.remove(widget);
        }
    }

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

}
