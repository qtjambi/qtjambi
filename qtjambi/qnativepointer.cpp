/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $CPP_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#include "qtjambi_global.h"
#include "qnativepointer.h"

#include <stdlib.h>

/*******************************************************************************
 * Read functions
 */

extern "C" JNIEXPORT jboolean JNICALL Java_com_trolltech_qt_QNativePointer_readBoolean
  (JNIEnv *, jclass, jlong ptr, jint pos)
{
    return (reinterpret_cast<bool *>(ptr))[pos];
}

extern "C" JNIEXPORT jbyte JNICALL Java_com_trolltech_qt_QNativePointer_readByte
  (JNIEnv *, jclass, jlong ptr, jint pos)
{
    return (reinterpret_cast<qint8 *>(ptr))[pos];
}

extern "C" JNIEXPORT jchar JNICALL Java_com_trolltech_qt_QNativePointer_readChar
  (JNIEnv *, jclass, jlong ptr, jint pos)
{
    return (reinterpret_cast<quint16 *>(ptr))[pos];
}

extern "C" JNIEXPORT jshort JNICALL Java_com_trolltech_qt_QNativePointer_readShort
  (JNIEnv *, jclass, jlong ptr, jint pos)
{
    return (reinterpret_cast<qint16 *>(ptr))[pos];
}

extern "C" JNIEXPORT jint JNICALL Java_com_trolltech_qt_QNativePointer_readInt
  (JNIEnv *, jclass, jlong ptr, jint pos)
{
    return (reinterpret_cast<qint32 *>(ptr))[pos];
}

extern "C" JNIEXPORT jlong JNICALL Java_com_trolltech_qt_QNativePointer_readLong
  (JNIEnv *, jclass, jlong ptr, jint pos)
{
    return (reinterpret_cast<qint64 *>(ptr))[pos];
}

extern "C" JNIEXPORT jfloat JNICALL Java_com_trolltech_qt_QNativePointer_readFloat
  (JNIEnv *, jclass, jlong ptr, jint pos)
{
    return (reinterpret_cast<float *>(ptr))[pos];
}

extern "C" JNIEXPORT jdouble JNICALL Java_com_trolltech_qt_QNativePointer_readDouble
  (JNIEnv *, jclass, jlong ptr, jint pos)
{
    return (reinterpret_cast<double *>(ptr))[pos];
}

extern "C" JNIEXPORT jlong JNICALL Java_com_trolltech_qt_QNativePointer_readPointer
  (JNIEnv *, jclass, jlong ptr, jint pos)
{
    return reinterpret_cast<jlong>((reinterpret_cast<void **>(ptr))[pos]);
}

extern "C" JNIEXPORT jstring JNICALL Java_com_trolltech_qt_QNativePointer_readString
  (JNIEnv *env, jclass, jlong ptr, jint pos)
{
    return qtjambi_from_qstring(env, reinterpret_cast<QString *>(ptr)[pos]);
}

/*******************************************************************************
 * Write functions
 */

extern "C" JNIEXPORT void JNICALL Java_com_trolltech_qt_QNativePointer_writeBoolean
  (JNIEnv *, jclass, jlong ptr, jint pos, jboolean value)
{
    ((reinterpret_cast<bool *>(ptr))[pos]) = value;
}

extern "C" JNIEXPORT void JNICALL Java_com_trolltech_qt_QNativePointer_writeByte
  (JNIEnv *, jclass, jlong ptr, jint pos, jbyte value)
{
    (((reinterpret_cast<qint8 *>(ptr)))[pos]) = value;
}

extern "C" JNIEXPORT void JNICALL Java_com_trolltech_qt_QNativePointer_writeChar
  (JNIEnv *, jclass, jlong ptr, jint pos, jchar value)
{
    (((reinterpret_cast<quint16 *>(ptr)))[pos]) = value;
}

extern "C" JNIEXPORT void JNICALL Java_com_trolltech_qt_QNativePointer_writeShort
  (JNIEnv *, jclass, jlong ptr, jint pos, jshort value)
{
    (((reinterpret_cast<qint16 *>(ptr)))[pos]) = value;
}

extern "C" JNIEXPORT void JNICALL Java_com_trolltech_qt_QNativePointer_writeInt
  (JNIEnv *, jclass, jlong ptr, jint pos, jint value)
{
    (((reinterpret_cast<qint32 *>(ptr)))[pos]) = value;
}

extern "C" JNIEXPORT void JNICALL Java_com_trolltech_qt_QNativePointer_writeLong
  (JNIEnv *, jclass, jlong ptr, jint pos, jlong value)
{
    (((reinterpret_cast<qint64 *>(ptr)))[pos]) = value;
}
extern "C" JNIEXPORT void JNICALL Java_com_trolltech_qt_QNativePointer_writeFloat
  (JNIEnv *, jclass, jlong ptr, jint pos, jfloat value)
{
    (((reinterpret_cast<float *>(ptr)))[pos]) = value;
}

extern "C" JNIEXPORT void JNICALL Java_com_trolltech_qt_QNativePointer_writeDouble
  (JNIEnv *, jclass, jlong ptr, jint pos, jdouble value)
{
    (((reinterpret_cast<double *>(ptr)))[pos]) = value;
}

extern "C" JNIEXPORT void JNICALL Java_com_trolltech_qt_QNativePointer_writePointer
  (JNIEnv *, jclass, jlong ptr, jint pos, jlong value)
{
    (reinterpret_cast<void **>(ptr))[pos] = reinterpret_cast<void *>(value);
}

extern "C" JNIEXPORT void JNICALL Java_com_trolltech_qt_QNativePointer_writeString
  (JNIEnv *env, jclass, jlong ptr, jint pos, jstring value)
{
    *reinterpret_cast<QString *>(ptr) = qtjambi_to_qstring(env, value);
}


/*******************************************************************************
 * other stuff...
 */


extern "C" JNIEXPORT jlong JNICALL Java_com_trolltech_qt_QNativePointer_createPointer
  (JNIEnv *, jobject, jint type, jint size, jint indirections)
{
    Q_ASSERT(indirections > 0);
    Q_ASSERT(size > 0);

    if (indirections != 1) {
        return reinterpret_cast<jlong>(new void*[size]);
    }

    if (size == 1) {
        switch (type) {
        case BooleanType: return reinterpret_cast<jlong>(new bool);
        case ByteType: return reinterpret_cast<jlong>(new qint8);
        case CharType: return reinterpret_cast<jlong>(new quint16);
        case ShortType: return reinterpret_cast<jlong>(new qint16);
        case IntType: return reinterpret_cast<jlong>(new qint32);
        case LongType: return reinterpret_cast<jlong>(new qint64);
        case FloatType: return reinterpret_cast<jlong>(new float);
        case DoubleType: return reinterpret_cast<jlong>(new double);
        case PointerType: return reinterpret_cast<jlong>(new void *);
        case StringType: return reinterpret_cast<jlong>(new QString());
        }
    } else if (size > 1) {
        switch (type) {
        case BooleanType: return reinterpret_cast<jlong>(new bool[size]);
        case ByteType: return reinterpret_cast<jlong>(new qint8[size]);
        case CharType: return reinterpret_cast<jlong>(new quint16[size]);
        case ShortType: return reinterpret_cast<jlong>(new qint16[size]);
        case IntType: return reinterpret_cast<jlong>(new qint32[size]);
        case LongType: return reinterpret_cast<jlong>(new qint64[size]);
        case FloatType: return reinterpret_cast<jlong>(new float[size]);
        case DoubleType: return reinterpret_cast<jlong>(new double[size]);
        case PointerType: return reinterpret_cast<jlong>(new void *[size]);
        case StringType: return reinterpret_cast<jlong>(new QString[size]);
        }
    }
    return 0;
}

extern "C" JNIEXPORT void JNICALL Java_com_trolltech_qt_QNativePointer_deletePointer
  (JNIEnv *, jobject, jlong ptr, jint type, jint deleteMode)
{
    if (deleteMode == 0) { // free()
        switch (type) {
        case BooleanType:       free((reinterpret_cast<bool *>(ptr))); break;
        case ByteType:          free((reinterpret_cast<qint8 *>(ptr))); break;
        case CharType:          free((reinterpret_cast<quint16 *>(ptr))); break;
        case ShortType:         free((reinterpret_cast<qint16 *>(ptr))); break;
        case IntType:           free((reinterpret_cast<int *>(ptr))); break;
        case LongType:          free((reinterpret_cast<qint64 *>(ptr))); break;
        case FloatType:         free((reinterpret_cast<float *>(ptr))); break;
        case DoubleType:        free((reinterpret_cast<double *>(ptr))); break;
        default:
            qWarning("Unhandled free of type: %d\n", type);
            break;
        }

    } else if (deleteMode == 1) { // delete
        switch (type) {
        case BooleanType:       delete ((reinterpret_cast<bool *>(ptr))); break;
        case ByteType:          delete ((reinterpret_cast<qint8 *>(ptr))); break;
        case CharType:          delete ((reinterpret_cast<quint16 *>(ptr))); break;
        case ShortType:         delete ((reinterpret_cast<qint16 *>(ptr))); break;
        case IntType:           delete ((reinterpret_cast<int *>(ptr))); break;
        case LongType:          delete ((reinterpret_cast<qint64 *>(ptr))); break;
        case FloatType:         delete ((reinterpret_cast<float *>(ptr))); break;
        case DoubleType:        delete ((reinterpret_cast<double *>(ptr))); break;
        case StringType:        delete ((reinterpret_cast<QString *>(ptr))); break;
        default:
            qWarning("Unhandled delete of type: %d\n", type);
            break;
        }

    } else if (deleteMode == 2) { // delete []
        switch (type) {
        case BooleanType:       delete [] ((reinterpret_cast<bool *>(ptr))); break;
        case ByteType:          delete [] ((reinterpret_cast<qint8 *>(ptr))); break;
        case CharType:          delete [] ((reinterpret_cast<quint16 *>(ptr))); break;
        case ShortType:         delete [] ((reinterpret_cast<qint16 *>(ptr))); break;
        case IntType:           delete [] ((reinterpret_cast<int *>(ptr))); break;
        case LongType:          delete [] ((reinterpret_cast<qint64 *>(ptr))); break;
        case FloatType:         delete [] ((reinterpret_cast<float *>(ptr))); break;
        case DoubleType:        delete [] ((reinterpret_cast<double *>(ptr))); break;
        case StringType:        delete [] ((reinterpret_cast<QString *>(ptr))); break;
        default:
            qWarning("Unhandled delete [] of type: %d\n", type);
            break;
        }
    }
}

