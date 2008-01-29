package generator;

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;

class QObject___ extends QObject {
    @com.trolltech.qt.QtBlockedSlot
    public final java.util.List<QObject> findChildren() {
        return findChildren(null, (QRegExp) null);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final java.util.List<QObject> findChildren(Class<?> cl) {
        return findChildren(cl, (QRegExp) null);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final java.util.List<QObject> findChildren(Class<?> cl, String name) {
        return com.trolltech.qt.QtJambiInternal.findChildren(this, cl, name);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final java.util.List<QObject> findChildren(Class<?> cl, QRegExp name) {
        return com.trolltech.qt.QtJambiInternal.findChildren(this, cl, name);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final QObject findChild() {
        return findChild(null, null);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final QObject findChild(Class<?> cl) {
        return findChild(cl, null);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final QObject findChild(Class<?> cl, String name) {
        return com.trolltech.qt.QtJambiInternal.findChild(this, cl, name);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final void setProperty(String name, Object value) 
    {
        setProperty(QNativePointer.createCharPointer(name), value);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final Object property(String name) 
    {
        return property(QNativePointer.createCharPointer(name));
    }

    @com.trolltech.qt.QtBlockedSlot
    public final QtProperty userProperty() 
    {
        return com.trolltech.qt.QtJambiInternal.userProperty(nativeId());
    }

    @com.trolltech.qt.QtBlockedSlot
    public final java.util.List<com.trolltech.qt.QtProperty> properties() 
    {
        return com.trolltech.qt.QtJambiInternal.properties(nativeId());
    }

    @com.trolltech.qt.QtBlockedSlot
    public final int indexOfProperty(String name) {
        return com.trolltech.qt.QtJambiInternal.indexOfProperty(nativeId(), name);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final void connectSlotsByName() {
        com.trolltech.qt.QtJambiInternal.connectSlotsByName(this);
    }
}// class

abstract class QAbstractItemModel___ extends QAbstractItemModel {
    private native boolean setData_native(long id, int row, int col, Object value, int role);

    public final boolean setData(int row, int col, Object value) {
        return setData_native(nativeId(), row, col, value, com.trolltech.qt.core.Qt.ItemDataRole.DisplayRole);
    }

    public final boolean setData(int row, int col, Object value, int role) {
        return setData_native(nativeId(), row, col, value, role);
    }

    private native Object data_native(long id, int row, int col, int role);

    public final Object data(int row, int col, int role) {
        return data_native(nativeId(), row, col, role);
    }

    public final Object data(int row, int col) {
        return data_native(nativeId(), row, col, Qt.ItemDataRole.DisplayRole);
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

    public final char readChar() {
        operator_shift_right_char(srb);
        return srb.charValue();
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

class QBitArray___ extends QBitArray {

    @com.trolltech.qt.QtBlockedSlot
    public final void xor(QBitArray other) {
        operator_xor_assign(other);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final void and(QBitArray other) {
        operator_and_assign(other);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final void or(QBitArray other) {
        operator_or_assign(other);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final void set(QBitArray other) {
        operator_assign(other);
    }

    @com.trolltech.qt.QtBlockedSlot
    public final QBitArray inverted() {
        return operator_negate();
    }

}// class

// hfr

class QDate___ extends QDate {

    public final int weekNumber() {
        return weekNumber(null);
    }

    public final int yearOfWeekNumber() {
        QNativePointer np = new QNativePointer(QNativePointer.Type.Int);
        weekNumber(np);
        return np.intValue();
    }

}// class

class QDir___ extends QDir {

    @com.trolltech.qt.QtBlockedSlot
    public String at(int i) {
        return operator_subscript(i);
    }

}// class

class QByteArray___ extends QByteArray {
    public QByteArray(String s) {
        this();
        append(s);
    }

    public QByteArray(byte data[]) {
        this(com.trolltech.qt.QtJambiInternal.byteArrayToNativePointer(data), data.length);
    }

    public final boolean contains(String str) {
        return contains(new QByteArray(str));
    }

    public final int count(String str) {
        return count(new QByteArray(str));
    }

    public final boolean endsWith(String str) {
        return endsWith(new QByteArray(str));
    }

    public final QByteArray prepend(String str) {
        return prepend(new QByteArray(str));
    }

    public final QByteArray replace(QByteArray before, String after) {
        return replace(before, new QByteArray(after));
    }

    public final QByteArray replace(String before, String after) {
        return replace(new QByteArray(before), new QByteArray(after));
    }

    public final boolean startsWith(String str) {
        return startsWith(new QByteArray(str));
    }

    public final byte[] toByteArray() {
        byte[] res = new byte[size()];

        for (int i = 0; i < size(); i++) {
            res[i] = at(i);
        }
        return res;
    }

    @com.trolltech.qt.QtBlockedSlot
    public final QByteArray set(QByteArray other) {
        operator_assign(other);
        return this;
    }

}// class

class QFile___ extends QFile {

    public static String decodeName(String localFileName) {
        return decodeName(com.trolltech.qt.QNativePointer.createCharPointer(localFileName));
    }

}// class

class QIODevice___ extends QIODevice {

    /**
     * Gets a byte from the device.
     * 
     * @return -1 on failure, or the value of the byte on success
     */
    public final int getByte() {
        QNativePointer np = new QNativePointer(QNativePointer.Type.Byte);
        boolean success = getByte(np);
        return success ? np.byteValue() : -1;
    }

}// class

class QCryptographicHash___ extends QCryptographicHash {

    public final void addData(byte data[]) {
        QNativePointer np = com.trolltech.qt.QtJambiInternal.byteArrayToNativePointer(data);
        addData(np, data.length);
    }

}// class

class QTextCodec___ extends QTextCodec {

    static {
        setCodecForTr(QTextCodec.codecForName("UTF-8"));
    }

    public static QTextCodec codecForName(String name) {
        return codecForName(com.trolltech.qt.QNativePointer.createCharPointer(name));
    }

}// class

class QBuffer___ extends QBuffer {

    // retain a reference to avoid gc
    @SuppressWarnings("unused")
    private Object strongDataReference = null;

    public QBuffer(QByteArray byteArray, QObject parent) {
        this(byteArray.nativePointer(), parent);
        strongDataReference = byteArray;
    }

    public QBuffer(QByteArray byteArray) {
        this(byteArray, null);
    }

    public final void setBuffer(QByteArray byteArray) {
        setBuffer(byteArray.nativePointer());
        strongDataReference = byteArray;
    }

    public final void setData(byte data[]) {
        QNativePointer np = com.trolltech.qt.QtJambiInternal.byteArrayToNativePointer(data);
        setData(np, data.length);
    }

}// class

class QSignalMapper___ extends QSignalMapper {
    
    private java.util.Hashtable<QObject, QObject> __rcObjectForObject = new java.util.Hashtable<QObject, QObject>();
    
    private java.util.Hashtable<QObject, Object> __rcWidgetForObject = new java.util.Hashtable<QObject, Object>();
    
}// class

class QAbstractFileEngine_MapExtensionReturn___ extends QAbstractFileEngine_MapExtensionReturn {
    private QNativePointer currentAddressNativePointer; // don't garbage collect while in use
    public final void setAddress(String address) {
        currentAddressNativePointer = address != null ? QNativePointer.createCharPointer(address) : null;
        address_private(currentAddressNativePointer);
    }

    public final String address() {
        QNativePointer np = address_private();
        return np != null ? com.trolltech.qt.QtJambiInternal.charPointerToString(np) : null;
    }
}// class

class QAbstractFileEngine_UnMapExtensionOption___ extends QAbstractFileEngine_UnMapExtensionOption {
    private QNativePointer currentAddressNativePointer; // don't garbage collect while in use
    public final void setAddress(String address) {
        currentAddressNativePointer = address != null ? QNativePointer.createCharPointer(address) : null;
        address_private(currentAddressNativePointer);
    }

    public final String address() {
        QNativePointer np = address_private();
        return np != null ? com.trolltech.qt.QtJambiInternal.charPointerToString(np) : null;
    }
}// class

class QFutureWatcher___ extends QFutureWatcher {

    public final QFuture<T> future() {
        if (nativeId() == 0)
            throw new QNoNativeResourcesException("Function call on incomplete object of type: " +getClass().getName());
        return __qt_future(nativeId());
    }
    private native QFuture<T> __qt_future(long nativeId);

}// class

class QFutureWatcherVoid___ extends QFutureWatcherVoid {

    public final QFutureVoid future() {
        if (nativeId() == 0)
            throw new QNoNativeResourcesException("Function call on incomplete object of type: " +getClass().getName());
        return __qt_future(nativeId());
    }

    private native QFutureVoid __qt_future(long nativeId);

}// class

class QFutureSynchronizer___ extends QFutureSynchronizer {

    public final java.util.List<QFuture<T>> futures() {
        if (nativeId() == 0)
            throw new QNoNativeResourcesException("Function call on incomplete object of type: " +getClass().getName());
        return __qt_futures(nativeId());
    }
    private native java.util.List<QFuture<T>> __qt_futures(long nativeId);

}// class

class QFutureSynchronizerVoid___ extends QFutureSynchronizerVoid {

    public final java.util.List<QFutureVoid> futures() {
        if (nativeId() == 0)
            throw new QNoNativeResourcesException("Function call on incomplete object of type: " +getClass().getName());
        return __qt_futures(nativeId());
    }
    private native java.util.List<QFutureVoid> __qt_futures(long nativeId);

}// class


class QtConcurrent___ extends QtConcurrent {

    static {
        com.trolltech.qt.QtJambi_LibraryInitializer.init();
        com.trolltech.qt.core.QtJambi_LibraryInitializer.init();        
    }

    public interface MapFunctor<T> {
        public void map(T object);
    }
    public static native <T> QFutureVoid map(java.util.Collection<T> sequence, MapFunctor<T> functor);
    public static native <T> void blockingMap(java.util.Collection<T> sequence, MapFunctor<T> functor);

    public interface MappedFunctor<U, T> {
        public U map(T object);
    }
    public static native <U, T> QFuture<U> mapped(java.util.Collection<T> sequence, MappedFunctor<U, T> functor);    
    public static native <U, T> java.util.List<U> blockingMapped(java.util.Collection<T> sequence, MappedFunctor<U, T> functor);

    /**
     * Implement this interface in order to perform a reduce operation. 
     * 
     * The reduce method will be called once per intermediate result (the result of the mapping of the data)
     * and the very first time the reduce() method is called for the particular data set, the result is set to 
     * the returned value of the defaultResult() method. 
     */
    public interface ReducedFunctor<U, T> {
        public U defaultResult();

        public void reduce(U result, T intermediate);
    }
    
    public static <U, V, T> QFuture<U> mappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor) {
        return mappedReduced(sequence, functor, reducedFunctor, ReduceOption.UnorderedReduce, ReduceOption.SequentialReduce);
    }

    public static <U, V, T> QFuture<U> mappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor, ReduceOption ... options) {
        return mappedReduced(sequence, functor, reducedFunctor, new ReduceOptions(options));
    }

    public static <U, V, T> QFuture<U> mappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor, ReduceOptions options) {
        return mappedReduced(sequence, functor, reducedFunctor, options.value());
    }

    private native static <U, V, T> QFuture<U> mappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor, int options);

    
    public static <U, V, T> U blockingMappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor) {
        return blockingMappedReduced(sequence, functor, reducedFunctor, ReduceOption.UnorderedReduce, ReduceOption.SequentialReduce);
    }

    public static <U, V, T> U blockingMappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor, ReduceOption ... options) {
        return blockingMappedReduced(sequence, functor, reducedFunctor, new ReduceOptions(options));
    }

    public static <U, V, T> U blockingMappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor, ReduceOptions options) {
        return blockingMappedReduced(sequence, functor, reducedFunctor, options.value());
    }

    private native static <U, V, T> U blockingMappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor, int options);

    public interface FilteredFunctor<T> {
        public boolean filter(T object);
    }

    public native static <T> QFuture<T> filtered(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor);
    public native static <T> java.util.List<T> blockingFiltered(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor);

    public static <U, T> QFuture<U> filteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor) {
        return filteredReduced(sequence, filteredFunctor, reducedFunctor, ReduceOption.UnorderedReduce, ReduceOption.SequentialReduce);
    }

    public static <U, T> QFuture<U> filteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor, ReduceOption ... options) {
        return filteredReduced(sequence, filteredFunctor, reducedFunctor, new ReduceOptions(options));
    }

    public static <U, T> QFuture<U> filteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor, ReduceOptions options) {
        return filteredReduced(sequence, filteredFunctor, reducedFunctor, options.value());
    }
    private native static <U, T> QFuture<U> filteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor, int options); 

    public static <U, T> U blockingFilteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor) {
        return blockingFilteredReduced(sequence, filteredFunctor, reducedFunctor, ReduceOption.UnorderedReduce, ReduceOption.SequentialReduce);
    }

    public static <U, T> U blockingFilteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor, ReduceOption ... options) {
        return blockingFilteredReduced(sequence, filteredFunctor, reducedFunctor, new ReduceOptions(options));
    }

    public static <U, T> U blockingFilteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor, ReduceOptions options) {
        return blockingFilteredReduced(sequence, filteredFunctor, reducedFunctor, options.value());
    }

    private native static <U, T> U blockingFilteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor, int options); 
 
    public static <T> QFuture<T> run(Object _this, java.lang.reflect.Method m, Object ... args) {
        if (m.getReturnType() == null || m.getReturnType().equals(Void.TYPE))
            throw new IllegalArgumentException("Cannot call run on method returning void. Use 'runVoidMethod' instead.");

        return runPrivate(_this, m.getDeclaringClass(), m, args);
    }
    private native static <T> QFuture<T> runPrivate(Object _this, Class<?> declaringClass, java.lang.reflect.Method m, Object args[]);

    public static QFutureVoid runVoidMethod(Object _this, java.lang.reflect.Method m, Object ... args) {
        if (m.getReturnType() != null && !m.getReturnType().equals(Void.TYPE))
            throw new IllegalArgumentException("Cannot call runVoidMethod on method returning non-void type. Use 'run' instead.");

        return runVoidMethodPrivate(_this, m.getDeclaringClass(), m, args);
    }
    private native static QFutureVoid runVoidMethodPrivate(Object _this, Class<?> declaringClass, java.lang.reflect.Method m, Object args[]);


}// class
