package com.trolltech.tests;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

class MainWindowState extends QMainWindow {

    public MainWindowState() {
        QDockWidget w1 = new QDockWidget();
        w1.setWindowTitle("Dock 1");
        w1.setObjectName("a");

        QDockWidget w2 = new QDockWidget();
        w2.setWindowTitle("Dock 2");
        w2.setObjectName("b");

        addDockWidget(Qt.DockWidgetArea.LeftDockWidgetArea, w1);
        addDockWidget(Qt.DockWidgetArea.RightDockWidgetArea, w2);

        setCentralWidget(new QTextEdit());
    }


    @Override
    protected void hideEvent(QHideEvent e) {
        QSettings settings = new QSettings("MySoft", "testing");
        QByteArray array = saveState();
        settings.setValue("state", array);
    }


    @Override
    protected void showEvent(QShowEvent e) {
        QSettings settings = new QSettings("MySoft", "testing");
        QByteArray state = (QByteArray) settings.value("state");
        restoreState(state);
    }

    public static void main(String args[]) {
        QApplication.initialize(args);
        
        MainWindowState widget = new MainWindowState();
        widget.show();
        
        QApplication.exec();
    }
}
