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
import com.trolltech.autotests.generated.QObjectType;
import com.trolltech.autotests.generated.InvalidatorQObjectType;
import org.junit.Test;

public class TestMemoryManagementQObjectType extends TestMemoryManagement {

    @Override
    protected QtJambiObject createInstanceInJava() {
        return new QObjectType();
    }

    @Override
    protected QtJambiObject createInstanceInNative() {
        return QObjectType.newInstance();
    }

    @Override
    protected boolean isQObject() {
        return true;
    }

    @Override
    protected boolean needsEventProcessing() {
        return true;
    }

    @Override
    protected boolean supportsSplitOwnership() {
        return false;
    }

    @Override
    protected void deleteLastInstance() {
        QObjectType.deleteLastInstance();
    }

    QtJambiObject temporaryObject = null;
    @Override
    protected QtJambiObject invalidateObject(QtJambiObject obj, final boolean returnReference) {

        new InvalidatorQObjectType() {
            @Override
            public void overrideMe(QObjectType t) {
                if (returnReference) {
                    temporaryObject = t;
                    temporaryObject.setJavaOwnership();
                }
            }
        }.invalidateObject((QObjectType) obj);

        QtJambiObject tmp = temporaryObject;
        temporaryObject = null;
        return tmp;
    }

    @Override
    protected String className() {
        return "QObjectType";
    }

    @Override
    protected boolean hasShellDestructor() {
        return true;
    }

    @Override
    protected boolean hasVirtualDestructor() {
        return true;
    }

    @Test
    public void dummy() {
        
    }
}
