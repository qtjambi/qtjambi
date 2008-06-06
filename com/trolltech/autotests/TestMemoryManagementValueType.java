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

package com.trolltech.autotests;

import com.trolltech.qt.QtJambiObject;
import com.trolltech.qt.QNativePointer;
import com.trolltech.autotests.generated.ValueType;
import com.trolltech.autotests.generated.InvalidatorValueType;
import org.junit.Test;

public class TestMemoryManagementValueType extends TestMemoryManagement {

    @Override
    protected QtJambiObject createInstanceInJava() {
        return new ValueType();
    }

    @Override
    protected QtJambiObject createInstanceInNative() {
        return ValueType.fromNativePointer(ValueType.newInstance());
    }

    @Override
    protected void deleteLastInstance() {
        ValueType.deleteLastInstance();
    }

    @Override
    protected boolean isValueType() {
        return true;
    }

    QtJambiObject temporaryObject = null;
    @Override
    protected QtJambiObject invalidateObject(QtJambiObject obj, final boolean returnReference) {
        new InvalidatorValueType() {
            @Override
            public void overrideMe(QNativePointer t) {
                if (returnReference) {
                    temporaryObject = ValueType.fromNativePointer(t);
                    temporaryObject.setJavaOwnership();
                }
            }
        }.invalidateObject(obj.nativePointer());

        QtJambiObject tmp = temporaryObject;
        temporaryObject = null;
        return tmp;
    }

    @Override
    protected boolean hasShellDestructor() {
        return false;
    }

    @Override
    protected boolean hasVirtualDestructor() {
        return false;
    }

    @Override
    protected String className() {
        return "ValueType";
    }

    @Override
    protected boolean supportsSplitOwnership() {
        return false;
    }

     /*
        Required for IntelliJ to realize that the method is a junit test
        since it doesn't check super classes.
    */
    @Test
    public void dummy() {}
}
