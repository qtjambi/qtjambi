/****************************************************************************
 **
 ** Copyright (C) 2008-$THISYEAR$ $TROLLTECH$. All rights reserved.
 **
 ** This file is part of $PRODUCT$.
 **
 ** $JAVA_LICENSE$
 **
 ** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 ** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 **
 ****************************************************************************/

package com.trolltech.examples;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xmlpatterns.*;

@QtJambiExample(name = "Xml Query")
public class XMLQuery extends QWidget {

    private QLineEdit queryLine;
    private QTextBrowser sourceBrowser;
    private QTextBrowser resultBrowser;

    private final String fileName = "classpath:com/trolltech/examples/frank.xbel";

    public XMLQuery() {
        this(null);
    }

    public XMLQuery(QWidget parent) {
        super(parent);
        QGridLayout layout = new QGridLayout(this);

        sourceBrowser = new QTextBrowser(this);
        sourceBrowser.setToolTip(tr("This window contains the source file used in the example.\n" + 
                "The file is accessed using the classpath file engine. '"
                + fileName + "'"));
        resultBrowser = new QTextBrowser(this);
        resultBrowser.setToolTip(tr("This is the result of the query."));
        queryLine = new QLineEdit(this);
        queryLine.setToolTip(tr("Try to change query, and press enter."));

        layout.addWidget(sourceBrowser, 1, 1);
        layout.addWidget(resultBrowser, 1, 2);
        layout.addWidget(queryLine, 2, 1, 1, 2);

        queryLine.returnPressed.connect(this, "executeQuery()");

        QFile file = new QFile(fileName);
        if (file.open(QIODevice.OpenModeFlag.ReadOnly)) {
            sourceBrowser.setPlainText(file.readAll().toString());
            new XMLHighlighter(sourceBrowser.document());
        } else {
            sourceBrowser.setPlainText(tr("Could not open file: ") + fileName);
        }

        queryLine.setText("doc(\"" + fileName + "\")/xbel/folder/title");
        executeQuery();
    }

    private void executeQuery() {
        resultBrowser.clear();

        QXmlQuery query = new QXmlQuery();
        query.setQuery(queryLine.text());

        String res = "";

        if (query.isValid()) {

            QXmlResultItems result = new QXmlResultItems();
            query.evaluateToResult(result);

            QXmlItem item = result.next();
            while (!item.isNull()) {

                if (item.isNode()) {
                    QAbstractXmlNodeModel model = item.toNodeModelIndex().model();
                    res += model.stringValue(item.toNodeModelIndex()) + "\n";
                } else if (item.isAtomicValue()) {
                    res += item.toAtomicValue() + "\n";
                }

                item = result.next();
            }
        } else {
            res = "Query was not valid.\n";
        }
        resultBrowser.setText(res);
    }

    public static void main(String[] args) {
        QApplication.initialize(args);

        XMLQuery test = new XMLQuery();
        test.show();

        QApplication.exec();
    }
}
