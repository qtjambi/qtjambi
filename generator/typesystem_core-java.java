package generator;

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;

class QObject___ extends QObject {
    @com.trolltech.qt.QtBlockedSlot
    public java.util.List<QObject> findChildren() {
        return findChildren(null, (QRegExp) null);
    }

    @com.trolltech.qt.QtBlockedSlot
    public java.util.List<QObject> findChildren(Class<?> cl) {
        return findChildren(cl, (QRegExp) null);
    }

    @com.trolltech.qt.QtBlockedSlot
    public java.util.List<QObject> findChildren(Class<?> cl, String name) {
        return com.trolltech.qt.QtJambiInternal.findChildren(this, cl, name);
    }

    @com.trolltech.qt.QtBlockedSlot
    public java.util.List<QObject> findChildren(Class<?> cl, QRegExp name) {
        return com.trolltech.qt.QtJambiInternal.findChildren(this, cl, name);
    }

    @com.trolltech.qt.QtBlockedSlot
    public QObject findChild() {
        return findChild(null, null);
    }

    @com.trolltech.qt.QtBlockedSlot
    public QObject findChild(Class<?> cl) {
        return findChild(cl, null);
    }

    @com.trolltech.qt.QtBlockedSlot
    public QObject findChild(Class<?> cl, String name) {
        return com.trolltech.qt.QtJambiInternal.findChild(this, cl, name);
    }

    @com.trolltech.qt.QtBlockedSlot
    public void setProperty(String name, Object value) {
        try {
            com.trolltech.qt.QtPropertyManager.writeProperty(this, name, value);
        } catch (QNoSuchPropertyException e) {
            // If property doesn't exist, we'll use the default Qt framework to
            // insert a dynamic property.
            setProperty(QNativePointer.createCharPointer(name), value);
        }
    }

    @com.trolltech.qt.QtBlockedSlot
    public Object property(String name) {
        try {
            return com.trolltech.qt.QtPropertyManager.readProperty(this, name);
        } catch (QNoSuchPropertyException e) {
            // If a property doesn't exist, we'll use the default Qt framework
            // to read it as a dynamic property
            return property(QNativePointer.createCharPointer(name));
        }
    }

    @com.trolltech.qt.QtBlockedSlot
    public void connectSlotsByName() {
        com.trolltech.qt.QtJambiInternal.connectSlotsByName(this);
    }
}// class

abstract class QAbstractItemModel___ extends QAbstractItemModel {
    private native boolean setData_native(long id, int row, int col, Object value, int role);

    public boolean setData(int row, int col, Object value) {
        return setData_native(nativeId(), row, col, value, com.trolltech.qt.core.Qt.ItemDataRole.DisplayRole);
    }

    public boolean setData(int row, int col, Object value, int role) {
        return setData_native(nativeId(), row, col, value, role);
    }

    private native Object data_native(long id, int row, int col, int role);

    public Object data(int row, int col, int role) {
        return data_native(nativeId(), row, col, role);
    }

    public Object data(int row, int col) {
        return data_native(nativeId(), row, col, Qt.ItemDataRole.DisplayRole);
    }
}// class

class QByteArray___ extends QByteArray {
    public QByteArray(String s) {
        this();
        append(s);
    }

    public final byte[] toByteArray() {
        byte[] res = new byte[size()];

        for (int i = 0; i < size(); i++) {
            res[i] = at(i);
        }
        return res;
    }
}// class

class QTimer___ extends QTimer {
    static private class QSingleShotTimer extends QObject {
        private int timerId = -1;
        public Signal0 timeout = new Signal0();

        public QSingleShotTimer(int msec, QObject obj, String method) {
            super(obj);
            timeout.connect(obj, method);
            timerId = startTimer(msec);
        }

        protected void disposed() {
            if (timerId > 0)
                killTimer(timerId);
            super.disposed();
        }

        protected void timerEvent(QTimerEvent e) {
            if (timerId > 0)
                killTimer(timerId);
            timerId = -1;
            timeout.emit();
            disposeLater();
        }
    }

    public static void singleShot(int msec, QObject obj, String method) {
        new QSingleShotTimer(msec, obj, method);
    }
}// class

class QCoreApplication___ extends QCoreApplication {

    protected static QCoreApplication m_instance = null;

    public QCoreApplication(String args[]) {
        this(argc(args), argv(args));
    }

    public static String translate(String context, String sourceText, String comment) {
        QTextCodec codec = QTextCodec.codecForName("UTF-8");
        return translate(context != null ? codec.fromUnicode(context).data() : null, sourceText != null ? codec.fromUnicode(sourceText).data() : null,
                comment != null ? codec.fromUnicode(comment).data() : null, Encoding.CodecForTr);
    }

    public static String translate(String context, String sourceText) {
        return translate(context, sourceText, null);
    }

    public static String translate(String context, String sourceText, String comment, int n) {
        QTextCodec codec = QTextCodec.codecForName("UTF-8");
        return translate(context != null ? codec.fromUnicode(context).data() : null, sourceText != null ? codec.fromUnicode(sourceText).data() : null,
                comment != null ? codec.fromUnicode(comment).data() : null, Encoding.CodecForTr, n);
    }

    public static void initialize(String args[]) {
        if (m_instance != null)
            throw new RuntimeException("QApplication can only be initialized once");

        m_instance = new QCoreApplication(args);
        m_instance.aboutToQuit.connect(m_instance, "disposeOfMyself()");
        String path = Utilities.unpackPlugins();
        if (path != null)
            addLibraryPath(path);
        else
            QtJambiInternal.setupDefaultPluginPath();
    }

    @SuppressWarnings("unused")
    private void disposeOfMyself() {
        m_instance = null;
        System.gc();
        this.dispose();
    }

    protected final static com.trolltech.qt.QNativePointer argv(String args[]) {
        String newArgs[] = new String[args.length + 1];
        System.arraycopy(args, 0, newArgs, 1, args.length);
        newArgs[0] = "Qt Jambi application";
        argv = com.trolltech.qt.QNativePointer.createCharPointerPointer(newArgs);
        return argv;
    }

    protected final static com.trolltech.qt.QNativePointer argc(String args[]) {
        if (argc != null) {
            throw new RuntimeException("There can only exist one QCoreApplication instance");
        }
        argc = new com.trolltech.qt.QNativePointer(com.trolltech.qt.QNativePointer.Type.Int);
        argc.setIntValue(args.length + 1);
        return argc;
    }

    @Override
    protected void disposed() {
        argc = null;
        argv = null;
        m_instance = null;
        super.disposed();
    }

    public static void invokeLater(java.lang.Runnable runnable) {
        postEvent(new QInvokable(runnable), new QEvent(QInvokable.INVOKABLE_EVENT));
    }

    /**
     * Executes the runnable's run() method in the main thread and waits for it
     * to return. If the current thread is not the main thread, an event loop
     * must be running in the main thread, or this method will wait
     * indefinitely.
     */
    public static void invokeAndWait(Runnable runnable) {
        QSynchronousInvokable invokable = new QSynchronousInvokable(runnable);
        QCoreApplication.postEvent(invokable, new QEvent(QSynchronousInvokable.SYNCHRONOUS_INVOKABLE_EVENT));
        invokable.waitForInvoked();
        invokable.disposeLater();
    }

    private static com.trolltech.qt.QNativePointer argc, argv;

}// class

class QTranslator___ extends QTranslator {
    public final boolean load(byte data[]) {
        return load(com.trolltech.qt.QtJambiInternal.byteArrayToNativePointer(data), data.length);
    }

    public String translate(String context, String sourceText, String comment) {
        QTextCodec codec = QTextCodec.codecForName("UTF-8");
        return translate(context != null ? codec.fromUnicode(context).data() : null, sourceText != null ? codec.fromUnicode(sourceText).data() : null,
                comment != null ? codec.fromUnicode(comment).data() : null);
    }

    public String translate(String context, String sourceText) {
        return translate(context, sourceText, null);
    }
}// class

class QProcess___ extends QProcess {

    public static class DetachedProcessInfo {
        public DetachedProcessInfo(boolean success, long pid) {
            this.success = success;
            this.pid = pid;
        }

        public boolean success;
        public long pid;
    }

    public static DetachedProcessInfo startDetached(String program, java.util.List<String> arguments, String workingDirectory) {
        QNativePointer pid = new QNativePointer(QNativePointer.Type.Long);
        boolean success = startDetached(program, arguments, workingDirectory, pid);
        return new DetachedProcessInfo(success, pid.longValue());
    }
}// class

class QDataStream___ extends QDataStream {

    private QNativePointer srb = new QNativePointer(QNativePointer.Type.Byte, 32) {
        {
            setVerificationEnabled(false);
        }
    };

    public final boolean readBoolean() {
        operator_shift_right_boolean(srb);
        return srb.booleanValue();
    }

    public final byte readByte() {
        operator_shift_right_byte(srb);
        return srb.byteValue();
    }

    public final short readShort() {
        operator_shift_right_short(srb);
        return srb.shortValue();
    }

    public final int readInt() {
        operator_shift_right_int(srb);
        return srb.intValue();
    }

    public final long readLong() {
        operator_shift_right_long(srb);
        return srb.longValue();
    }

    public final float readFloat() {
        operator_shift_right_float(srb);
        return srb.floatValue();
    }

    public final double readDouble() {
        operator_shift_right_double(srb);
        return srb.doubleValue();
    }

    public final QDataStream writeShort(short s) {
        writeShort_char((char) s);
        return this;
    }

    private native String readString_private(long nativeId);

    private native void writeString_private(long nativeId, String string);

    public final String readString() {
        if (nativeId() == 0)
            throw new QNoNativeResourcesException("Function call on incomplete object of type: " + getClass().getName());
        return readString_private(nativeId());
    }

    public final void writeString(String string) {
        if (nativeId() == 0)
            throw new QNoNativeResourcesException("Function call on incomplete object of type: " + getClass().getName());
        writeString_private(nativeId(), string);
    }

    private native int writeBytes(long id, byte buffer[], int length);

    private native int readBytes(long id, byte buffer[], int length);

    public final int writeBytes(byte buffer[]) {
        return writeBytes(buffer, buffer.length);
    }

    public final int writeBytes(byte buffer[], int length) {
        return writeBytes(nativeId(), buffer, length);
    }

    public final int readBytes(byte buffer[]) {
        return readBytes(buffer, buffer.length);
    }

    public final int readBytes(byte buffer[], int length) {
        return readBytes(nativeId(), buffer, length);
    }
}// class

class QTextStream___ extends QTextStream {
    public final void setCodec(String codecName) {
        setCodec(QNativePointer.createCharPointer(codecName));
        if (codec() != __rcCodec)
            __rcCodec = null;
    }

    private QNativePointer srb = new QNativePointer(QNativePointer.Type.Byte, 32) {
        {
            setVerificationEnabled(false);
        }
    };

    public final byte readByte() {
        operator_shift_right_byte(srb);
        return srb.byteValue();
    }

    public final short readShort() {
        operator_shift_right_short(srb);
        return srb.shortValue();
    }

    public final int readInt() {
        operator_shift_right_int(srb);
        return srb.intValue();
    }

    public final long readLong() {
        operator_shift_right_long(srb);
        return srb.longValue();
    }

    public final float readFloat() {
        operator_shift_right_float(srb);
        return srb.floatValue();
    }

    public final double readDouble() {
        operator_shift_right_double(srb);
        return srb.doubleValue();
    }

    public final QTextStream writeShort(short s) {
        writeShort_char((char) s);
        return this;
    }

    public final String readString() {
        return readString_native(nativeId());
    }

    public final void writeString(String string) {
        writeString_native(nativeId(), string);
    }

    private final native String readString_native(long id);

    private final native void writeString_native(long id, String string);

}// class
