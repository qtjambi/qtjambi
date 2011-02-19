/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
**
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
**
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package generator;

import com.trolltech.qt.*;
import com.trolltech.qt.QtJambiObject.QPrivateConstructor;
import com.trolltech.qt.core.*;

class QObject___ extends QObject {
        @com.trolltech.qt.QtBlockedSlot
        public final java.util.List<QObject> findChildren() {
            return findChildren(null, (QRegExp) null);
        }

        @com.trolltech.qt.QtBlockedSlot
        public final java.util.List<QObject> findChildren(Class < ? > cl) {
            return findChildren(cl, (QRegExp) null);
        }

        @com.trolltech.qt.QtBlockedSlot
        public final java.util.List<QObject> findChildren(Class < ? > cl, String name) {
            return com.trolltech.qt.internal.QtJambiInternal.findChildren(this, cl, name);
        }

        @com.trolltech.qt.QtBlockedSlot
        public final java.util.List<QObject> findChildren(Class < ? > cl, QRegExp name) {
            return com.trolltech.qt.internal.QtJambiInternal.findChildren(this, cl, name);
        }

        @com.trolltech.qt.QtBlockedSlot
        public final QObject findChild() {
            return findChild(null, null);
        }

        @com.trolltech.qt.QtBlockedSlot
        public final QObject findChild(Class < ? > cl) {
            return findChild(cl, null);
        }

        @com.trolltech.qt.QtBlockedSlot
        public final QObject findChild(Class < ? > cl, String name) {
            return com.trolltech.qt.internal.QtJambiInternal.findChild(this, cl, name);
        }

        @com.trolltech.qt.QtBlockedSlot
        public final void setProperty(String name, Object value) {
            setProperty(QNativePointer.createCharPointer(name), value);
        }

        @com.trolltech.qt.QtBlockedSlot
        public final Object property(String name) {
            return property(QNativePointer.createCharPointer(name));
        }

        @com.trolltech.qt.QtBlockedSlot
        public final QtProperty userProperty() {
            return com.trolltech.qt.internal.QtJambiInternal.userProperty(nativeId());
        }

        @com.trolltech.qt.QtBlockedSlot
        public final java.util.List<com.trolltech.qt.QtProperty> properties() {
            return com.trolltech.qt.internal.QtJambiInternal.properties(nativeId());
        }

        @com.trolltech.qt.QtBlockedSlot
        public final int indexOfProperty(String name) {
            return com.trolltech.qt.internal.QtJambiInternal.indexOfProperty(nativeId(), name);
        }

        @com.trolltech.qt.QtBlockedSlot
        public final void connectSlotsByName() {
            com.trolltech.qt.internal.QtJambiInternal.connectSlotsByName(this);
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

        public QCoreApplication(String applicationName, String args[]) {
            this(argc(args), argv(applicationName, args));
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

        public static void initialize(String applicationName, String args[]) {
            com.trolltech.qt.internal.HelperFunctions.setAsMainThread();

            if (m_instance != null)
                throw new RuntimeException("QCoreApplication can only be initialized once");

            String path = Utilities.unpackPlugins();
            if (path != null)
                addLibraryPath(path);
            else
                com.trolltech.qt.internal.QtJambiInternal.setupDefaultPluginPath();
            m_instance = new QCoreApplication(applicationName, args);
            m_instance.aboutToQuit.connect(m_instance, "disposeOfMyself()");
        }

        public static void initialize(String args[]) {
            com.trolltech.qt.internal.HelperFunctions.setAsMainThread();

            if (m_instance != null)
                throw new RuntimeException("QCoreApplication can only be initialized once");

            String path = Utilities.unpackPlugins();
            if (path != null)
                addLibraryPath(path);
            else
                com.trolltech.qt.internal.QtJambiInternal.setupDefaultPluginPath();
            m_instance = new QCoreApplication(args);
            m_instance.aboutToQuit.connect(m_instance, "disposeOfMyself()");
        }

        private void disposeOfMyself() {
            m_instance = null;
            System.gc();
            this.disposeLater();
        }

        protected final static com.trolltech.qt.QNativePointer argv(String args[]) {
            String newArgs[] = new String[args.length + 1];
            System.arraycopy(args, 0, newArgs, 1, args.length);
            try {
                newArgs[0] = System.getProperty("qt.application.path", "Qt Jambi application");
            } catch (Exception e) {
                newArgs[0] = "Qt Jambi application";
            }
            argv = com.trolltech.qt.QNativePointer.createCharPointerPointer(newArgs);
            return argv;
        }

        protected final static com.trolltech.qt.QNativePointer argv(String applicationName, String args[]) {
            String newArgs[] = new String[args.length + 1];
            System.arraycopy(args, 0, newArgs, 1, args.length);
            if (applicationName != null && applicationName.length() != 0) {
                newArgs[0] = applicationName;
            } else {
                newArgs[0] = "Qt Jambi application";
            }
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
            // Specialcase invoke and wait for the case of running on the current thread...
            if (Thread.currentThread() == instance().thread()) {
                runnable.run();
                return;
            }

            QSynchronousInvokable invokable = new QSynchronousInvokable(runnable);
            QCoreApplication.postEvent(invokable, new QEvent(QSynchronousInvokable.SYNCHRONOUS_INVOKABLE_EVENT));
            invokable.waitForInvoked();
            invokable.disposeLater();
        }


        /**
         * Executes the task in the application's main thread after the
         * specified timeout. This is done by starting a timer so this
         * method does not block.
         * @param timeout The time to wait, in milliseconds
         * @param task The task to perform...
         */
        public static void invokeLater(int timeout, final Runnable task) {
            QTimer.singleShot(timeout, new QObject() {
                public void todo() {
                    task.run();
                    disposeLater();
                }
            }
            , "todo()");
        }


        private static com.trolltech.qt.QNativePointer argc, argv;

}// class

class QTranslator___ extends QTranslator {
        public final boolean load(byte data[]) {
            return load(com.trolltech.qt.internal.QtJambiInternal.byteArrayToNativePointer(data), data.length);
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

        public final QTextStream writeChar(char c) {
            writeShort_char(c);
            return this;
        }

        public final char readChar() {
            operator_shift_right_short(srb);
            return srb.charValue();
        }

        public final String readString() {
            return readString_native(nativeId());
        }

        public final void writeString(String string) {
            writeString_native(nativeId(), string);
        }

        /**
            This function makes the text tream operate on a string instead
            of a QIODevice.
            <p>
            The <tt>string</tt> parameter is the initial contents of the string that the text stream
            will work on. If <tt>string</tt> is <tt>null</tt>, an empty string will be created.
            The stream can both be written to and read from, depending on <tt>openMode</tt>.
            The contents of the string can at any time be retrieved by <tt>string()</tt>.
            For example:
            <pre class="snippet">
                QTextStream stream = QTextStream.createStringStream("TestString\n55",
                    new QIODevice.OpenMode(QIODevice.OpenModeFlag.ReadWrite));
                stream.writeString(" ");
                stream.writeString("Hei");
                stream.seek(0);
                System.err.println(stream.readString()); // == "TestString"
                System.err.println(stream.string()); // == "TestString\n55 Hei"
            </pre>
            <p>
            If the text stream already has a QIODevice set, it will flush this.
            @param string The initial content of the string.
            @param openMode Specifies access priveledges to the stream.
            @return A QTextStream operating on a String instead of a QIODevice.
        */
        public static QTextStream createStringStream(String string, com.trolltech.qt.core.QIODevice.OpenMode openMode) {
            QTextStream instance = new StringStream(string, openMode);
            return instance;
        }

        /**
            Returns the contents of the string that this text stream is working on.
            <p>
            After creating a string stream with the createStringStream() method,
            you can fetch the contents of the string the text stream is working on with this
            method.
            <p>
            If this QTextStream was not created using createStringStream(), this function returns <tt>null</tt>.
            @return Returns the contents of QTextStreams created with createStringStream()
        */
        public String string() {
            return null;
        }

        private static final class StringStream extends QTextStream {
                protected StringStream(String string, com.trolltech.qt.core.QIODevice.OpenMode openMode) {
                    stringPtr = createString(nativeId(), string, openMode.value());
                }

                public String string() {
                    return string_native(nativeId());
                }

                @Override
                public void disposed() {
                    deleteString(stringPtr);
                }

                private native long createString(long id, String str, int openMode);
                private native void deleteString(long strPtr);
                private native String string_native(long id);

                private long stringPtr = 0;
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
            this(com.trolltech.qt.internal.QtJambiInternal.byteArrayToNativePointer(data), data.length);
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
            QNativePointer np = com.trolltech.qt.internal.QtJambiInternal.byteArrayToNativePointer(data);
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
            QNativePointer np = com.trolltech.qt.internal.QtJambiInternal.byteArrayToNativePointer(data);
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
            return np != null ? com.trolltech.qt.internal.QtJambiInternal.charPointerToString(np) : null;
        }
}// class

class QAbstractFileEngine___ extends QAbstractFileEngine {
        /**
         * Adds <tt>path</tt> to the set of paths in which Qt Jambi should search for resources. Resources
         * can be accessed using the "classpath:" scheme.
         */
        public static void addSearchPathForResourceEngine(String path) {
            com.trolltech.qt.internal.QClassPathEngine.addSearchPath(path);
        }

        /**
         * Removes <tt>path</tt> from the set of paths in which Qt Jambi searches
         * for resources.
         */
        public static void removeSearchPathForResourceEngine(String path) {
            com.trolltech.qt.internal.QClassPathEngine.removeSearchPath(path);
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
            return np != null ? com.trolltech.qt.internal.QtJambiInternal.charPointerToString(np) : null;
        }
}// class

class QFutureWatcher___ extends QFutureWatcher {

        public final QFuture<T> future() {
            if (nativeId() == 0)
                throw new QNoNativeResourcesException("Function call on incomplete object of type: " + getClass().getName());
            return __qt_future(nativeId());
        }
        private native QFuture<T> __qt_future(long nativeId);

}// class

class QFutureWatcherVoid___ extends QFutureWatcherVoid {

        public final QFutureVoid future() {
            if (nativeId() == 0)
                throw new QNoNativeResourcesException("Function call on incomplete object of type: " + getClass().getName());
            return __qt_future(nativeId());
        }

        private native QFutureVoid __qt_future(long nativeId);

}// class

class QFutureSynchronizer___ extends QFutureSynchronizer {

        public final java.util.List<QFuture<T>> futures() {
            if (nativeId() == 0)
                throw new QNoNativeResourcesException("Function call on incomplete object of type: " + getClass().getName());
            return __qt_futures(nativeId());
        }
        private native java.util.List<QFuture<T>> __qt_futures(long nativeId);

}// class

class QFutureSynchronizerVoid___ extends QFutureSynchronizerVoid {

        public final java.util.List<QFutureVoid> futures() {
            if (nativeId() == 0)
                throw new QNoNativeResourcesException("Function call on incomplete object of type: " + getClass().getName());
            return __qt_futures(nativeId());
        }
        private native java.util.List<QFutureVoid> __qt_futures(long nativeId);

}// class

/**
The QtConcurrent class contains static methods for running computations in parallel (using separate threads)
on the items in a java.util.Collection, such as a Vector or LinkedList. We will now describe these methods.

The QtConcurrent::map(), QtConcurrent::mapped() and QtConcurrent::mappedReduced() functions run computations in parallel on the items in a sequence such as a QList or a QVector. QtConcurrent::map() modifies a sequence in-place, QtConcurrent::mapped() returns a new sequence containing the modified content, and QtConcurrent::mappedReduced() returns a single result.
Concurrent Map
<p>
QtConcurrent::mapped() takes an input sequence and a map function. This map function is then called for each item in the sequence, and a new sequence containing the return values from the map function is returned.
<p>
The map function must be of the form:
<pre>
     U function(const T &amp;t);
</pre>
T and U can be any type (and they can even be the same type), but T must match the type stored in the sequence. The function returns the modified or mapped content.
<p>
This example shows how to apply a scale function to all the items in a sequence:
<pre>
     QImage scaled(const QImage &amp;image)
     {
         return image.scaled(100, 100);
     }

     QList &lt;QImage&gt; images = ...;
     QFuture&lt;QImage&gt; thumbnails = QtConcurrent::mapped(images, scaled);
</pre>
The results of the map are made available through QFuture. See the QFuture and QFutureWatcher documentation for more information on how to use QFuture in your applications.
<p>
If you want to modify a sequence in-place, use QtConcurrent::map(). The map function must then be of the form:
<pre>
     U function(T &amp;t);
</pre>
Note that the return value and return type of the map function are not used.
<p>
Using QtConcurrent::map() is similar to using QtConcurrent::mapped():
<pre>
     void scale(QImage &amp;image)
     {
         image = image.scaled(100, 100);
     }

     QList&lt;QImage&gt; images = ...;
     QFuture&lt;void&gt; future = QtConcurrent::map(images, scale);
</pre>
Since the sequence is modified in place, QtConcurrent::map() does not return any results via QFuture. However, you can still use QFuture and QFutureWatcher to monitor the status of the map.
Concurrent Map-Reduce
<p>
QtConcurrent::mappedReduced() is similar to QtConcurrent::mapped(), but instead of returning a sequence with the new results, the results are combined into a single value using a reduce function.
<p>
The reduce function must be of the form:
<pre>
     V function(T &amp;result, const U &amp;intermediate)
</pre>
T is the type of the final result, U is the return type of the map function. Note that the return value and return type of the reduce function are not used.
<p>
Call QtConcurrent::mappedReduced() like this:
<pre>
     void addToCollage(QImage &amp;collage, const QImage &amp;thumbnail)
     {
         QPainter p(&amp;collage);
         static QPoint offset = QPoint(0, 0);
         p.drawImage(offset, thumbnail);
         offset += ...;
     }

     QList&lt;QImage&gt; images = ...;
     QFuture&lt;QImage&gt; collage = QtConcurrent::mappedReduced(images, scaled, addToCollage);
</pre>
The reduce function will be called once for each result returned by the map function, and should merge the intermediate into the result variable. QtConcurrent::mappedReduced() guarantees that only one thread will call reduce at a time, so using a mutex to lock the result variable is not neccesary. The QtConcurrent::ReduceOptions enum provides a way to control the order in which the reduction is done. If QtConcurrent::UnorderedReduce is used (the default), the order is undefined, while QtConcurrent::OrderedReduce ensures that the reduction is done in the order of the original sequence.
Additional API Features
Using Iterators instead of Sequence
<p>
Each of the above functions has a variant that takes an iterator range instead of a sequence. You use them in the same way as the sequence variants:
<pre>
     QList&lt;QImage&gt; images = ...;

     QFuture&lt;QImage&gt; thumbnails = QtConcurrent::mapped(images.constBegin(), images.constEnd(), scaled);

     // map in-place only works on non-const iterators
     QFuture&lt;void&gt; future = QtConcurrent::map(images.begin(), images.end(), scale);

     QFuture&lt;QImage&gt; collage = QtConcurrent::mappedReduced(images.constBegin(), images.constEnd(), scaled, addToCollage);
</pre>
Blocking Variants
<p>
Each of the above functions has a blocking variant that returns the final result instead of a QFuture. You use them in the same way as the asynchronous variants.
<pre>
     QList&lt;QImage&gt; images = ...;

     // each call blocks until the entire operation is finished
     QList&lt;QImage&gt; future = QtConcurrent::blockingMapped(images, scaled);

     QtConcurrent::blockingMap(images, scale);

     QImage collage = QtConcurrent::blockingMappedReduced(images, scaled, addToCollage);
</pre>
Note that the result types above are not QFuture objects, but real result types (in this case, QList&lt;QImage&gt; and QImage).
Using Member Functions
<p>
QtConcurrent::map(), QtConcurrent::mapped(), and QtConcurrent::mappedReduced() accept pointers to member functions. The member function class type must match the type stored in the sequence:
<pre>
     // squeeze all strings in a QStringList
     QStringList strings = ...;
     QFuture&lt;void&gt; squeezedStrings = QtConcurrent::map(strings, &amp;QString::squeeze);

     // swap the rgb values of all pixels on a list of images
     QList&lt;QImage&gt; images = ...;
     QFuture&lt;QImage&gt; bgrImages = QtConcurrent::mapped(images, &amp;QImage::rgbSwapped);

     // create a set of the lengths of all strings in a list
     QStringList strings = ...;
     QFuture&lt;QSet&lt;int&gt; &gt; wordLengths = QtConcurrent::mappedReduced(string, &amp;QString::length, &amp;QSet&lt;int&gt;::insert);
</pre>
Note that when using QtConcurrent::mappedReduced(), you can mix the use of normal and member functions freely:
<p>
<pre>
     // can mix normal functions and member functions with QtConcurrent::mappedReduced()

     // compute the average length of a list of strings
     extern void computeAverage(int &amp;average, int length);
     QStringList strings = ...;
     QFuture&lt;int&gt; averageWordLength = QtConcurrent::mappedReduced(strings, &amp;QString::length, computeAverage);

     // create a set of the color distribution of all images in a list
     extern int colorDistribution(const QImage &amp;string);
     QList&lt;QImage&gt; images = ...;
     QFuture&lt;QSet&lt;int&gt; &gt; totalColorDistribution = QtConcurrent::mappedReduced(images, colorDistribution, QSet&lt;int&gt;::insert);
</pre>
Using Function Objects
<p>
QtConcurrent::map(), QtConcurrent::mapped(), and QtConcurrent::mappedReduced() accept function objects, which can be used to add state to a function call. The result_type typedef must define the result type of the function call operator:
<pre>
     struct Scaled
     {
         Scaled(int size)
         : m_size(size) { }

         typedef QImage result_type;

         QImage operator()(const QImage &amp;image)
         {
             return image.scaled(m_size, m_size);
         }

         int m_size;
     };

     QList&lt;QImage&gt; images = ...;
     QFuture&lt;QImage&gt; thumbnails = QtConcurrent::mapped(images, Scaled(100));
</pre>
Using Bound Function Arguments
<p>
Note that Qt does not provide support for bound functions. This is provided by 3rd party libraries like Boost or C++ TR1 Library Extensions.
<p>
If you want to use a map function that takes more than one argument you can use boost::bind() or std::tr1::bind() to transform it onto a function that takes one argument.
<p>
As an example, we'll use QImage::scaledToWidth():
<pre>
     QImage QImage::scaledToWidth(int width, Qt::TransformationMode) const;
</pre>
scaledToWidth takes three arguments (including the "this" pointer) and can't be used with QtConcurrent::mapped() directly, because QtConcurrent::mapped() expects a function that takes one argument. To use QImage::scaledToWidth() with QtConcurrent::mapped() we have to provide a value for the width and the transformation mode:
<pre>
     boost::bind(&amp;QImage::scaledToWidth, 100 Qt::SmoothTransformation)
</pre>
The return value from boost::bind() is a function object (functor) with the following signature:
<pre>
     QImage scaledToWith(const QImage &amp;image)
</pre>
This matches what QtConcurrent::mapped() expects, and the complete example becomes:
<pre>
     QList&gt;QImage&lt; images = ...;
     QFuture&gt;QImage&lt; thumbnails = QtConcurrent::mapped(images, boost::bind(&amp;QImage::scaledToWidth, 100 Qt::SmoothTransformation));
</pre>
*/
class QtConcurrent___ extends QtConcurrent {

        static {
            com.trolltech.qt.QtJambi_LibraryInitializer.init();
            com.trolltech.qt.core.QtJambi_LibraryInitializer.init();
        }

        /**
         * An implemetation of this interface is given one to QtConcurrent's map() methods.
         * The map() method of this interface is called for each object in a java.util.Collection.
         *
         */
        public interface MapFunctor<T> {

            /**
             * This function is called for each item in the Collection. The function is then free to alter <tt>object</tt> as it see fit.
             */
            public void map(T object);
        }
        /**
         *  Calls function once for each item in sequence. The function is passed a reference to the item, so that any modifications done to the item will appear in sequence.
         */
        public static native <T> QFutureVoid map(java.util.Collection<T> sequence, MapFunctor<T> functor);

        /**
         *  Calls function once for each item in sequence. The function is passed a reference to the item, so that any modifications done to the item will appear in sequence.
         */
        public static native <T> void blockingMap(java.util.Collection<T> sequence, MapFunctor<T> functor);

        /**
         * Implement this interface to perform a mapped operation. An implementation of the interface is sendt
         * to ome of the mapped methods of QtConcurrent, which applies the MappedFunctor.map() method to all elements in a collection,
         * and returns the result.
         */
        public interface MappedFunctor<U, T> {

            /**
             * This method is called for each object in a collection. It should returned a new altered
             * object.
             */
            public U map(T object);
        }

        /**
         * Calls function once for each item in sequence and returns a future with each mapped item as a result. You can QFutureIterator to iterate through the results.
         *
         */
        public static native <U, T> QFuture<U> mapped(java.util.Collection<T> sequence, MappedFunctor<U, T> functor);

        /**
         * Calls function once for each item in sequence and returns a future with each mapped item as a result. You can QFutureIterator to iterate through the results.
         */
        public static native <U, T> java.util.List<U> blockingMapped(java.util.Collection<T> sequence, MappedFunctor<U, T> functor);

        /**
         * Implement this interface in order to perform a reduce operation.
         * <p>
         * The reduce method will be called once per intermediate result (the result of the mapping of the data)
         * and the very first time the reduce() method is called for the particular data set, the result is set to
         * the returned value of the defaultResult() method.
         */
        public interface ReducedFunctor<U, T> {
            public U defaultResult();

            /**
             *  Performs a reduce operation on <tt>intermediate</tt>. <tt>result</tt> is the result of the reduction.
             */
            public void reduce(U result, T intermediate);
        }

        /**
         * This is an overloaded method provided for convenience.
         * <p>
         * It is equivalent of mappedReduced(sequence, functor, reducedFunctor, ReduceOption.UnorderedReduce, ReduceOption.SequentialReduce)
         */
        public static <U, V, T> QFuture<U> mappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor) {
            return mappedReduced(sequence, functor, reducedFunctor, ReduceOption.UnorderedReduce, ReduceOption.SequentialReduce);
        }


        /**
         * This is an overloaded method provided for convenience.
         * <p>
         * Note that while mapFunction is called concurrently, only one thread at a time will call reduceFunction. The order in which reduceFunction is called is determined by reduceOptions.
         *
         */
        public static <U, V, T> QFuture<U> mappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor, ReduceOption ... options) {
            return mappedReduced(sequence, functor, reducedFunctor, new ReduceOptions(options));
        }

        /**
         * Calls mapFunction once for each item in sequence. The return value of each mapFunction is passed to reduceFunction.
         * <p>
         * Note that while mapFunction is called concurrently, only one thread at a time will call reduceFunction. The order in which reduceFunction is called is determined by reduceOptions.
         *
         */
        public static <U, V, T> QFuture<U> mappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor, ReduceOptions options) {
            return mappedReduced(sequence, functor, reducedFunctor, options.value());
        }

        private native static <U, V, T> QFuture<U> mappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor, int options);


        /**
         * This is an overloaded method provided for convenience.
         * <p>
         * It is equivalent of calling blockingMappedReduced(sequence, functor, reducedFunctor, ReduceOption.UnorderedReduce, ReduceOption.SequentialReduce)
         *
         */
        public static <U, V, T> U blockingMappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor) {
            return blockingMappedReduced(sequence, functor, reducedFunctor, ReduceOption.UnorderedReduce, ReduceOption.SequentialReduce);
        }

        /**
         * Calls mapFunction once for each item in sequence. The return value of each mapFunction is passed to reduceFunction.
         * <p>
         * Note that while mapFunction is called concurrently, only one thread at a time will call reduceFunction. The order in which reduceFunction is called is determined by reduceOptions.
         * <p>
         * Note: This function will block until all items in the sequence have been processed.
         */
        public static <U, V, T> U blockingMappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor, ReduceOption ... options) {
            return blockingMappedReduced(sequence, functor, reducedFunctor, new ReduceOptions(options));
        }

        /**
         * Calls mapFunction once for each item in sequence. The return value of each mapFunction is passed to reduceFunction.
         * <p>
         * Note that while mapFunction is called concurrently, only one thread at a time will call reduceFunction. The order in which reduceFunction is called is determined by reduceOptions.
         * <p>
         * Note: This function will block until all items in the sequence have been processed.
         */
        public static <U, V, T> U blockingMappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor, ReduceOptions options) {
            return blockingMappedReduced(sequence, functor, reducedFunctor, options.value());
        }

        private native static <U, V, T> U blockingMappedReduced(java.util.Collection<T> sequence, MappedFunctor<V, T> functor, ReducedFunctor<U, V> reducedFunctor, int options);

        /**
         * An implementation of this interface is given to one of QtConcurrent's filtered() methods.
         * The filter method if this interface is called for each item in a java.util.Collection.
         *
         */
        public interface FilteredFunctor<T> {

            /**
             *  This method is called for each item in a java.util.Collection. The items for which
             *  this method returns true are removed from the collection.
             */
            public boolean filter(T object);
        }

        /**
         * Calls filterFunctor's filtered() method once for each item in sequence and returns a new Sequence of kept items. If filterFunction returns true, a copy of the item is put in the new Sequence. Otherwise, the item will not appear in the new Sequence.
         */
        public native static <T> QFuture<T> filtered(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor);

        /**
         * Calls filterFunctor's filtered() method once for each item in sequence and returns a new Sequence of kept items. If filterFunction returns true, a copy of the item is put in the new Sequence. Otherwise, the item will not appear in the new Sequence.
         */
        public native static <T> java.util.List<T> blockingFiltered(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor);


        /**
         * This is an overloaded method provided for convenience. It is equivalent of calling filteredReduced(sequence, filteredFunctor, ReduceOption.UnorderedReduce, ReduceOption.Seq This is an overloaded method provided for convenience. It is equivalent of calling filteredReduced)
         */
        public static <U, T> QFuture<U> filteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor) {
            return filteredReduced(sequence, filteredFunctor, reducedFunctor, ReduceOption.UnorderedReduce, ReduceOption.SequentialReduce);
        }

        /**
         * Calls filterFunction once for each item in sequence. If filterFunction returns true for an item, that item is then passed to reduceFunction. In other words, the return value is the result of reduceFunction for each item where filterFunction returns true.
         * <p>
         * Note that while filterFunction is called concurrently, only one thread at a time will call reduceFunction. The order in which reduceFunction is called is undefined if reduceOptions is QtConcurrent::UnorderedReduce. If reduceOptions is QtConcurrent::OrderedReduce, reduceFunction is called in the order of the original sequence.
         */
        public static <U, T> QFuture<U> filteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor, ReduceOption ... options) {
            return filteredReduced(sequence, filteredFunctor, reducedFunctor, new ReduceOptions(options));
        }

        /**
         * Calls filterFunction once for each item in sequence. If filterFunction returns true for an item, that item is then passed to reduceFunction. In other words, the return value is the result of reduceFunction for each item where filterFunction returns true.
         * <p>
         * Note that while filterFunction is called concurrently, only one thread at a time will call reduceFunction. The order in which reduceFunction is called is undefined if reduceOptions is QtConcurrent::UnorderedReduce. If reduceOptions is QtConcurrent::OrderedReduce, reduceFunction is called in the order of the original sequence.
         */
        public static <U, T> QFuture<U> filteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor, ReduceOptions options) {
            return filteredReduced(sequence, filteredFunctor, reducedFunctor, options.value());
        }
        private native static <U, T> QFuture<U> filteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor, int options);

        /**
         * This is an overloaded method provided for convenience. It is the equivalent of calling blockingFilteredReduced(sequence, filteredFunctor, reducedFunctor, ReduceOption.UnorderedReduce, ReduceOption.SequentialReduce)
         */
        public static <U, T> U blockingFilteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor) {
            return blockingFilteredReduced(sequence, filteredFunctor, reducedFunctor, ReduceOption.UnorderedReduce, ReduceOption.SequentialReduce);
        }

        /**
         * Calls filterFunction once for each item in sequence. If filterFunction returns true for an item, that item is then passed to reduceFunction. In other words, the return value is the result of reduceFunction for each item where filterFunction returns true.
         * <p>
         * Note that while filterFunction is called concurrently, only one thread at a time will call reduceFunction. The order in which reduceFunction is called is undefined if reduceOptions is QtConcurrent::UnorderedReduce. If reduceOptions is QtConcurrent::OrderedReduce, reduceFunction is called in the order of the original sequence.
         */
        public static <U, T> U blockingFilteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor, ReduceOption ... options) {
            return blockingFilteredReduced(sequence, filteredFunctor, reducedFunctor, new ReduceOptions(options));
        }

        /**
         * Calls filterFunction once for each item in sequence. If filterFunction returns true for an item, that item is then passed to reduceFunction. In other words, the return value is the result of reduceFunction for each item where filterFunction returns true.
         * <p>
         * Note that while filterFunction is called concurrently, only one thread at a time will call reduceFunction. The order in which reduceFunction is called is undefined if reduceOptions is QtConcurrent::UnorderedReduce. If reduceOptions is QtConcurrent::OrderedReduce, reduceFunction is called in the order of the original sequence.
         */
        public static <U, T> U blockingFilteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor, ReduceOptions options) {
            return blockingFilteredReduced(sequence, filteredFunctor, reducedFunctor, options.value());
        }

        private native static <U, T> U blockingFilteredReduced(java.util.Collection<T> sequence, FilteredFunctor<T> filteredFunctor, ReducedFunctor<U, T> reducedFunctor, int options);

        /**
         * Executes the method <tt>m</tt> through the QtConcurrent framework with the given arguments. The returned QFuture object's result wil be the
         * return value of <tt>m</tt>. Note that this method does not accept function that return void. use runVoidMethod() for this.
         */
        public static <T> QFuture<T> run(Object _this, java.lang.reflect.Method m, Object ... args) {
            if (m.getReturnType() == null || m.getReturnType().equals(Void.TYPE))
                throw new IllegalArgumentException("Cannot call run on method returning void. Use 'runVoidMethod' instead.");

            return runPrivate(_this, m.getDeclaringClass(), m, args, com.trolltech.qt.internal.QtJambiInternal.resolveConversionSchema(m.getParameterTypes(), m.getParameterTypes()), com.trolltech.qt.internal.QtJambiInternal.typeConversionCode(m.getReturnType()));
        }
        private native static <T> QFuture<T> runPrivate(Object _this, Class < ? > declaringClass, java.lang.reflect.Method m, Object args[], int conversionScheme[], byte returnType);

        /**
         * Executes the method <tt>m</tt> with the given arguments using the QtConcurrent framework. Notice that runVoidMethod() does not
         * accept methods that has a return value. Use the run() method for this purpose.
         */
        public static QFutureVoid runVoidMethod(Object _this, java.lang.reflect.Method m, Object ... args) {
            if (m.getReturnType() != null && !m.getReturnType().equals(Void.TYPE))
                throw new IllegalArgumentException("Cannot call runVoidMethod on method returning non-void type. Use 'run' instead.");

            return runVoidMethodPrivate(_this, m.getDeclaringClass(), m, args, com.trolltech.qt.internal.QtJambiInternal.resolveConversionSchema(m.getParameterTypes(), m.getParameterTypes()));
        }
        private native static QFutureVoid runVoidMethodPrivate(Object _this, Class < ? > declaringClass, java.lang.reflect.Method m, Object args[], int conversionScheme[]);


}// class
