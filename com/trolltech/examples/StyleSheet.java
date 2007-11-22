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

package com.trolltech.examples;

import com.trolltech.examples.stylesheet.Ui_MainWindow;
import com.trolltech.examples.stylesheet.Ui_StyleSheetEditor;
import com.trolltech.qt.core.*;
import com.trolltech.qt.core.QIODevice.OpenModeFlag;
import com.trolltech.qt.gui.*;

@QtJambiExample(name = "Style Sheets")
public class StyleSheet extends QMainWindow {

    private Ui_MainWindow ui = new Ui_MainWindow();
    private StyleSheetEditor styleSheetEditor;

    public static void main(String args[]) {
        QApplication.initialize(args);

        StyleSheet styleSheet = new StyleSheet();
        styleSheet.show();

        QApplication.exec();
    }


    public StyleSheet() {
        ui.setupUi(this);

        ui.nameLabel.setProperty("class", "mandatory");

        styleSheetEditor = new StyleSheetEditor(this);

        statusBar().addWidget(new QLabel(tr("Ready")));

        ui.exitAction.triggered.connect(this, "close()");
        ui.aboutQtAction.triggered.connect(QApplication.instance(), "aboutQt()");
        ui.aboutQtJambiAction.triggered.connect(QApplication.instance(), "aboutQtJambi()");

        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
    }

    @SuppressWarnings("unused")
    private void on_editStyleAction_triggered() {
        styleSheetEditor.show();
        styleSheetEditor.activateWindow();
    }

    @SuppressWarnings("unused")
    private void on_aboutAction_triggered() {
        QMessageBox.about(this, tr("About Style sheet"),
                tr("The <b>Style Sheet</b> example shows how widgets can be styled "
                        + "using <a href=\"http://doc.trolltech.com/4.2/stylesheet.html\">Qt "
                        + "Style Sheets</a>. Click <b>File|Edit Style Sheet</b> to pop up the "
                        + "style editor, and either choose an existing style sheet or design "
                        + "your own."));
    }

    private class StyleSheetEditor extends QDialog {

        private Ui_StyleSheetEditor ui = new Ui_StyleSheetEditor();
        private QWidget main;

        StyleSheetEditor(QWidget parent) {
            super(parent);
            main = parent;
            ui.setupUi(this);

            QRegExp regExp = new QRegExp("Q(.*)Style");
            String defaultStyle = QApplication.style().getClass().getSimpleName();
            if (regExp.exactMatch(defaultStyle))
                defaultStyle = regExp.cap(1);

            ui.styleCombo.addItems(QStyleFactory.keys());
            ui.styleCombo.setCurrentIndex(ui.styleCombo.findText(defaultStyle));
            ui.styleSheetCombo.setCurrentIndex(ui.styleSheetCombo.findText("Coffee"));
            loadStyleSheet("Coffee");
        }

        void on_styleCombo_activated(final String styleName) {
            QStyle style = QStyleFactory.create(styleName);
            setStyle(style, main);
            setStyle(style, this);
            ui.applyButton.setEnabled(false);
        }

        void setStyle(QStyle style, QObject object)
        {
            for (QObject obj : object.children()) {
                if (obj instanceof QWidget) {
                    ((QWidget) obj).setStyle(style);
                    setStyle(style, obj);
                }
            }
        }

        void on_styleSheetCombo_activated(final String sheetName) {
            loadStyleSheet(sheetName);
        }

        void on_styleTextEdit_textChanged() {
            ui.applyButton.setEnabled(true);
        }

        void on_applyButton_clicked() {
            main.setStyleSheet(ui.styleTextEdit.toPlainText());
            ui.applyButton.setEnabled(false);
        }

        void loadStyleSheet(final String sheetName) {
            QFile file = new QFile("classpath:com/trolltech/examples/stylesheet/qss/"
                    + sheetName.toLowerCase() + ".qss");

            file.open(OpenModeFlag.ReadOnly);
            String styleSheet = file.readAll().toString();

            ui.styleTextEdit.setPlainText(styleSheet);
            main.setStyleSheet(styleSheet);
            ui.applyButton.setEnabled(false);
            file.close();
        }
    }

}
