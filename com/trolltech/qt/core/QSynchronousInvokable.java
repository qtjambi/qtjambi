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

import com.trolltech.qt.gui.QApplication;

/**
 * The QSynchronousInvokable class is used internally by QCoreApplication.invokeLaterAndWait()
 * to synchronously execute a java.lang.Runnable from the gui-thread.
 *
 * @author eskil
 * @see QCoreApplication
 */
class QSynchronousInvokable extends QObject {
    static QEvent.Type SYNCHRONOUS_INVOKABLE_EVENT = QEvent.Type.resolve(QEvent.Type.User.value() + 1);
    
    private Runnable runnable;
    public QSynchronousInvokable(Runnable runnable) {
        disableGarbageCollection();
        if(QCoreApplication.instance().nativeId() != 0) {
            moveToThread(QCoreApplication.instance().thread());
            this.runnable = runnable;
        }                       
        
        if (runnable == null || Thread.currentThread().equals(QApplication.instance().thread()))
            invoked = true;
        
    }

    private Boolean invoked = false;
    synchronized void waitForInvoked() {
        while (!invoked) {
            try { 
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        invoked = false;
    }                   
    
    @Override
    public final boolean event(com.trolltech.qt.core.QEvent e) {
        if (e.type() == SYNCHRONOUS_INVOKABLE_EVENT && runnable != null) {
            runnable.run();
            synchronized (this) {
                invoked = true;
                notifyAll();
            }                
            return true;
        }
        return super.event(e);
    }
}