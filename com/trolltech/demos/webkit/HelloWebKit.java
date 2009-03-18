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

package com.trolltech.demos.webkit;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.webkit.*;

@com.trolltech.examples.QtJambiExample(name="Hello WebKit")
public class HelloWebKit extends QMainWindow {

    private QWebView browser;
    private QLineEdit field;

    private QAction forward;
    private QAction backward;
    private QAction reload;
    private QAction stop;

    public HelloWebKit() {
    this(null);
    }

    public HelloWebKit(QWidget parent) {
    super(parent);

        field = new QLineEdit();
        browser = new QWebView();

        // Toolbar...
        QToolBar toolbar = addToolBar("Actions");
        backward = toolbar.addAction("Backward");
        forward = toolbar.addAction("Forward");
        reload = toolbar.addAction("Reload");
        stop = toolbar.addAction("Stop");
        toolbar.addWidget(field);
        toolbar.setFloatable(false);
        toolbar.setMovable(false);

        setCentralWidget(browser);
        statusBar().show();

        // Connections
        field.returnPressed.connect(this, "open()");

        browser.loadStarted.connect(this, "loadStarted()");
        browser.loadProgress.connect(this, "loadProgress(int)");
        browser.loadFinished.connect(this, "loadDone()");
        browser.urlChanged.connect(this, "urlChanged(QUrl)");

        forward.triggered.connect(browser, "forward()");
        backward.triggered.connect(browser, "back()");
        reload.triggered.connect(browser, "reload()");
        stop.triggered.connect(browser, "stop()");



        // Set an initial loading page once its up and showing...
        QApplication.invokeLater(new Runnable() {
                public void run() {
                    field.setText("http://www.qtsoftware.com");
                    open();
                }
            });
    }

    public void urlChanged(QUrl url) {
        field.setText(url.toString());
    }

    public void loadStarted() {
        statusBar().showMessage("Starting to load: " + field.text());
    }

    public void loadDone() {
        statusBar().showMessage("Loading done...");
    }

    public void loadProgress(int x) {
        statusBar().showMessage("Loading: " + x + " %");
    }

    public void open() {
        String text = field.text();

        if (text.indexOf("://") < 0)
            text = "http://" + text;

        browser.load(new QUrl(text));
    }

    @Override
    protected void closeEvent(QCloseEvent event) {
        browser.loadProgress.disconnect(this);
        browser.loadFinished.disconnect(this);
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        HelloWebKit widget = new HelloWebKit();
        widget.show();

        QApplication.exec();
    }
}
