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

#ifndef QNATIVEPOINTER_H
#define QNATIVEPOINTER_H

// Make sure this in sync with QNativePointer.java's enum Type
enum PointerType {
    BooleanType         = 0,
    ByteType            = 1,
    CharType            = 2,
    ShortType           = 3,
    IntType             = 4,
    LongType            = 5,
    FloatType           = 6,
    DoubleType          = 7,
    PointerType         = 8
};

#endif // QNATIVEPOINTER_H

