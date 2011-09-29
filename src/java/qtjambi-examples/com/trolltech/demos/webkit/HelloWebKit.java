/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
** 
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

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

	setWindowTitle("Hello WebKit");
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));	

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

        QApplication.execStatic();
        QApplication.shutdown();
    }
}
