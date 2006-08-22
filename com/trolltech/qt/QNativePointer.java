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

public class QNativePointer {
    static {
        QtJambi_LibraryInitializer.init();
    }

    // Keep this in sync with the values in common/nativepointer.h
    public enum Type {
        Boolean, Byte, Char, Short, Int, Long, Float, Double, Pointer,
        String
    }

    public enum AutoDeleteMode {
        Free, Delete, DeleteArray, None
    }

    public QNativePointer(Type type) {
        this(type, 1);
    }

    public QNativePointer(Type type, int size) {
        this(type, size, 1);
    }

    public QNativePointer(Type type, int size, int indirections) {
        if (indirections < 1)
            throw new IllegalArgumentException("level of indirection must be at least 1");
        if (size == 0)
            throw new IllegalArgumentException("size must be at least 1");
        m_ptr = createPointer(type.ordinal(), size, indirections);
        m_type = type;
        m_knownSize = size;
        m_indirections = indirections;
        m_autodelete = size == 1 ? AutoDeleteMode.Delete : AutoDeleteMode.DeleteArray;
    }

    private QNativePointer(int type, int size, int indirections) {
        this(typeOf(type), size, indirections);
    }

    private QNativePointer() {
        m_knownSize = -1;
        m_autodelete = AutoDeleteMode.None;
    };

    public boolean booleanValue() {
        return booleanAt(0);
    }

    public byte byteValue() {
        return byteAt(0);
    }

    public char charValue() {
        return charAt(0);
    }

    public short shortValue() {
        return shortAt(0);
    }

    public int intValue() {
        return intAt(0);
    }

    public long longValue() {
        return longAt(0);
    }

    public float floatValue() {
        return floatAt(0);
    }

    public double doubleValue() {
        return doubleAt(0);
    }

    public QNativePointer pointerValue() {
        return pointerAt(0);
    }
    
    public String stringValue() {
        return stringAt(0);
    }

    public void setBooleanValue(boolean value) {
        setBooleanAt(0, value);
    }

    public void setByteValue(byte value) {
        setByteAt(0, value);
    }

    public void setCharValue(char value) {
        setCharAt(0, value);
    }

    public void setShortValue(short value) {
        setShortAt(0, value);
    }

    public void setIntValue(int value) {
        setIntAt(0, value);
    }

    public void setLongValue(long value) {
        setLongAt(0, value);
    }

    public void setFloatValue(float value) {
        setFloatAt(0, value);
    }

    public void setDoubleValue(double value) {
        setDoubleAt(0, value);
    }

    public void setPointerValue(QNativePointer value) {
        setPointerAt(0, value);
    }
    
    public void setStringValue(String value) {
        setStringAt(0, value);
    }

    public boolean booleanAt(int pos) {
        verifyAccess(Type.Boolean, pos);
        return readBoolean(m_ptr, pos);
    }

    public byte byteAt(int pos) {
        verifyAccess(Type.Byte, pos);
        return readByte(m_ptr, pos);
    }

    public char charAt(int pos) {
        verifyAccess(Type.Char, pos);
        return readChar(m_ptr, pos);
    }

    public short shortAt(int pos) {
        verifyAccess(Type.Short, pos);
        return readShort(m_ptr, pos);
    }

    public int intAt(int pos) {
        verifyAccess(Type.Int, pos);
        return readInt(m_ptr, pos);
    }

    public long longAt(int pos) {
        verifyAccess(Type.Long, pos);
        return readLong(m_ptr, pos);
    }

    public float floatAt(int pos) {
        verifyAccess(Type.Float, pos);
        return readFloat(m_ptr, pos);
    }

    public double doubleAt(int pos) {
        verifyAccess(Type.Double, pos);
        return readDouble(m_ptr, pos);
    }

    public QNativePointer pointerAt(int pos) {
        verifyAccess(Type.Pointer, pos);
        long ptr = readPointer(m_ptr, pos);
        return fromNative(ptr, m_type, m_indirections - 1);
    }
    
    public String stringAt(int pos) {
        verifyAccess(Type.String, pos);
        return readString(m_ptr, pos);
    }

    public void setBooleanAt(int pos, boolean value) {
        verifyAccess(Type.Boolean, pos);
        writeBoolean(m_ptr, pos, value);
    }

    public void setByteAt(int pos, byte value) {
        verifyAccess(Type.Byte, pos);
        writeByte(m_ptr, pos, value);
    }

    public void setCharAt(int pos, char value) {
        verifyAccess(Type.Char, pos);
        writeChar(m_ptr, pos, value);
    }

    public void setShortAt(int pos, short value) {
        verifyAccess(Type.Short, pos);
        writeShort(m_ptr, pos, value);
    }

    public void setIntAt(int pos, int value) {
        verifyAccess(Type.Int, pos);
        writeInt(m_ptr, pos, value);
    }

    public void setLongAt(int pos, long value) {
        verifyAccess(Type.Long, pos);
        writeLong(m_ptr, pos, value);
    }

    public void setFloatAt(int pos, float value) {
        verifyAccess(Type.Float, pos);
        writeFloat(m_ptr, pos, value);
    }

    public void setDoubleAt(int pos, double value) {
        verifyAccess(Type.Double, pos);
        writeDouble(m_ptr, pos, value);
    }

    public void setPointerAt(int pos, QNativePointer value) {
        verifyAccess(Type.Pointer, pos);
        writePointer(m_ptr, pos, value == null ? 0 : value.m_ptr);
    }
    
    public void setStringAt(int pos, String value) {
        verifyAccess(Type.String, pos);
        writeString(m_ptr, pos, value);
    }

    public Type type() {
        return m_type;
    }

    public int indirections() {
        return m_indirections;
    }

    public boolean isNull() {
        return m_ptr == 0;
    }

    public AutoDeleteMode autoDeleteMode() {
        return m_autodelete;
    }

    public void setAutoDeleteMode(AutoDeleteMode autodelete) {
        m_autodelete = autodelete;
    }

    public void free() {
        if (isNull())
            return;
        deletePointer(m_ptr, m_type.ordinal(), 0);
        m_ptr = 0;
    }

    public void delete() {
        if (isNull())
            return;
        deletePointer(m_ptr, m_type.ordinal(), 1);
        m_ptr = 0;
    }

    public void deleteArray() {
        if (isNull())
            return;
        deletePointer(m_ptr, m_type.ordinal(), 2);
        m_ptr = 0;
    }

    public long pointer() {
        return m_ptr;
    }

    public static QNativePointer fromNative(long ptr, Type type, int indirections) {
        QNativePointer nativePointer = new QNativePointer();
        nativePointer.m_ptr = ptr;
        nativePointer.m_type = type;
        nativePointer.m_indirections = indirections;
        return nativePointer;
    }

    /**
     * Returns if verification is enabled or not.
     */
    public boolean verificationEnabled() {
        return m_verification_enabled;
    }

    /**
     * Sets if the any accesses should be type verified or not. By default this
     * value is set to true, meaning that trying to access a char pointer as an
     * int pointer will trigger an exception. Disabling this value allows
     * complete access, but without any safty, so incorrect usage may lead to
     * memory corruption in the C++ implementation.
     * 
     * @param a Set to true if verification should be enabled.
     */
    public void setVerificationEnabled(boolean a) {
        m_verification_enabled = a;
    }

    private static Type typeOf(int type) {
        switch (type) {
        case 0:
            return Type.Boolean;
        case 1:
            return Type.Byte;
        case 2:
            return Type.Char;
        case 3:
            return Type.Short;
        case 4:
            return Type.Int;
        case 5:
            return Type.Long;
        case 6:
            return Type.Float;
        case 7:
            return Type.Double;
        case 8:
            return Type.Pointer;
        }
        throw new IllegalArgumentException("Unknown type id: " + type);
    }

    public static QNativePointer fromNative(long ptr, int type, int indirections) {
        return fromNative(ptr, typeOf(type), indirections);
    }

    /**
     * Creates a char** native pointer from the input string.
     * @param strings the input strings
     * @return a char **
     */
    public static QNativePointer createCharPointerPointer(String strings[]) {
        QNativePointer ptrs = new QNativePointer(Type.Byte, strings.length + 1, 2);
        for (int j = 0; j < strings.length; ++j) {
            String string = strings[j];
            ptrs.setPointerAt(j, createCharPointer(string));
        }
        ptrs.setPointerAt(strings.length, null);
        return ptrs;
    }
    
    /**
     * Creates a char * from the input string
     * @param string The input string
     * @return The char *
     */
    public static QNativePointer createCharPointer(String string) {
        QNativePointer s = new QNativePointer(QNativePointer.Type.Byte, string.length() + 1);
        for (int i = 0; i < string.length(); ++i)
            s.setByteAt(i, (byte) string.charAt(i));
        s.setByteAt(string.length(), (byte) 0);
        return s;
    }

    protected void finalize() {
        switch (m_autodelete) {
        case Free:
            free();
            break;
        case Delete:
            delete();
            break;
        case DeleteArray:
            deleteArray();
            break;
        }
    }

    private void verifyAccess(Type type, int pos) {
        if (!m_verification_enabled)
            return;

        if (isNull())
            throw new NullPointerException("native pointer is null");

        if (pos < 0)
            throw new IndexOutOfBoundsException("negative index: " + pos);

        if (m_knownSize >= 0 && pos >= m_knownSize)
            throw new IndexOutOfBoundsException("size: " + m_knownSize + ", access at: " + pos);

        if (m_indirections > 1) {
            if (type != Type.Pointer)
                throw new ClassCastException("accessing pointer with " + m_indirections
                        + " levels of indirection as " + type);
        } else if (type != m_type) {
            throw new ClassCastException("type: " + m_type + ", accessed as: " + type);
        }
    }

    private static native boolean readBoolean(long ptr, int pos);
    private static native byte readByte(long ptr, int pos);
    private static native char readChar(long ptr, int pos);
    private static native short readShort(long ptr, int pos);
    private static native int readInt(long ptr, int pos);
    private static native long readLong(long ptr, int pos);
    private static native float readFloat(long ptr, int pos);
    private static native double readDouble(long ptr, int pos);
    private static native long readPointer(long ptr, int pos);
    private static native String readString(long ptr, int pos);
    private static native void writeBoolean(long ptr, int pos, boolean value);
    private static native void writeByte(long ptr, int pos, byte value);
    private static native void writeChar(long ptr, int pos, char value);
    private static native void writeShort(long ptr, int pos, short value);
    private static native void writeInt(long ptr, int pos, int value);
    private static native void writeLong(long ptr, int pos, long value);
    private static native void writeFloat(long ptr, int pos, float value);
    private static native void writeDouble(long ptr, int pos, double value);
    private static native void writePointer(long ptr, int pos, long value);
    private static native void writeString(long ptr, int pos, String value);
    private static native long createPointer(int type, int size, int indirections);
    private static native void deletePointer(long ptr, int type, int deleteMode);

    private long m_ptr;
    private Type m_type;
    private int m_knownSize;
    private AutoDeleteMode m_autodelete;
    private int m_indirections;

    private boolean m_verification_enabled = true;
}
