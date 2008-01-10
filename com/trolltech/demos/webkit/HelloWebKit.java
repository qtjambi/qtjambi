package com.trolltech.demos.webkit;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.webkit.*;

class HelloWebKit extends QWidget {

    private QWebView browser;
    private QLineEdit field;
    private QLabel state;

    public HelloWebKit() {
        field = new QLineEdit();
        browser = new QWebView();
        state = new QLabel("No page loaded...");
        state.setFixedWidth(200);

        QGridLayout layout = new QGridLayout(this);
        layout.addWidget(field, 0, 0);
        layout.addWidget(state, 0, 1);
        layout.addWidget(browser, 1, 0, 1, 2);

        field.returnPressed.connect(this, "open()");

        browser.loadStarted.connect(this, "loadStarted()");
        browser.loadProgressChanged.connect(this, "loadProgress(int)");
        browser.loadFinished.connect(this, "loadDone()");
    }

    public void loadStarted() {
        state.setText("Starting to load: " + field.text());
    }

    public void loadDone() {
        state.setText("Loading done...");
    }

    public void loadProgress(int x) {
        state.setText("Loading: " + x + " %");
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
