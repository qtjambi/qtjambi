package com.trolltech.qt.core;


/**
 * The QInvokable class is used internally by QCoreApplication.invokeLater()
 * to execute a java.lang.Runnable from the gui-thread.
 * 
 * @author gunnar
 * @see QCoreApplication
 */
class QInvokable extends QObject {
    
    public static QEvent.Type INVOKABLE_EVENT = QEvent.Type.resolve(QEvent.Type.User.value() + 1);
    
    QInvokable(Runnable r) {
        moveToThread(QCoreApplication.instance().thread());
        runnable = r;
    }

    public boolean event(com.trolltech.qt.core.QEvent e) {
        if (e.type() == INVOKABLE_EVENT) {
            runnable.run();
            disposeLater();
            return true;
        } 
        return super.event(e); 
    }
    
    private Runnable runnable;    
}
