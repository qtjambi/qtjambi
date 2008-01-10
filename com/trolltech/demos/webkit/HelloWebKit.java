package com.trolltech.demos.webkit;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.webkit.*;

class HelloWebKit extends QMainWindow {

    private QWebView browser;
    private QLineEdit field;

    private QAction forward;
    private QAction backward;
    private QAction reload;
    private QAction stop;

    public HelloWebKit() {
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
        browser.loadProgressChanged.connect(this, "loadProgress(int)");
        browser.loadFinished.connect(this, "loadDone()");

        forward.triggered.connect(browser, "forward()");
        backward.triggered.connect(browser, "backward()");
        reload.triggered.connect(browser, "reload()");
        stop.triggered.connect(browser, "stop()");
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

        System.out.println(text);

        browser.load(new QUrl(text));
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        HelloWebKit widget = new HelloWebKit();
        widget.show();

        QApplication.exec();
    }
}
