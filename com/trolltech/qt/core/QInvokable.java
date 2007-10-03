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

package com.trolltech.qt.core;


/**
 * The QInvokable class is used internally by QCoreApplication.invokeLater()
 * to execute a java.lang.Runnable from the gui-thread.
 *
 * @see QCoreApplication
 */
class QInvokable extends QObject {

    static QEvent.Type INVOKABLE_EVENT = QEvent.Type.resolve(QEvent.Type.User.value() + 1);

    QInvokable(Runnable r) {
        disableGarbageCollection();
        if(QCoreApplication.instance() != null && QCoreApplication.instance().nativeId() != 0) {
            moveToThread(QCoreApplication.instance().thread());
            runnable = r;
        }
    }

    public final boolean event(com.trolltech.qt.core.QEvent e) {
        if (e.type() == INVOKABLE_EVENT && runnable != null) {
            runnable.run();
            disposeLater();
            return true;
        }
        return super.event(e);
    }

    private Runnable runnable;
}
