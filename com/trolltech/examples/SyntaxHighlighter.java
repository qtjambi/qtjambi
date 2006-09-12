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

import java.util.Vector;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class SyntaxHighlighter extends QMainWindow {

    private QTextEdit editor;

    public static void main(String args[]) {
        QApplication.initialize(args);

        SyntaxHighlighter syntaxHighlighter = new SyntaxHighlighter();
        syntaxHighlighter.show();

        QApplication.exec();
    }

    public SyntaxHighlighter() {
        setupFileMenu();
        setupHelpMenu();
        setupEditor();

        setCentralWidget(editor);
        resize(640, 480);
        setWindowTitle(tr("Syntax Highlighter"));
    }

    public void about() {
        QMessageBox.about(this, tr("About Syntax Highlighter"),
                tr("<p>The <b>Syntax Highlighter</b> example shows how "
                        + "to perform simple syntax highlighting by subclassing "
                        + "the QSyntaxHighlighter class and describing "
                        + "highlighting rules using regular expressions.</p>"));
    }

    public void aboutQt() {
        QApplication.aboutQt();
    }

    public void quit() {
        QApplication.quit();
    }

    public void newFile() {
        editor.clear();
    }

    public void openFile() {
        openFile("");
    }

    public void openFile(String fileName) {
        if (fileName.equals(""))
            fileName = QFileDialog
                    .getOpenFileName(this, tr("Open File"), "", "Java Files (*.java)");

        if (!fileName.equals("")) {
            QFile file = new QFile(fileName);
            if (file.open(new QFile.OpenMode(QFile.OpenModeFlag.ReadOnly, QFile.OpenModeFlag.Text)))
                editor.setPlainText(file.readAll().toString());
        }
    }

    private void setupEditor() {
        QFont font = new QFont();
        font.setFamily("Courier");
        font.setFixedPitch(true);
        font.setPointSize(10);

        editor = new QTextEdit();
        editor.setLineWrapMode(QTextEdit.LineWrapMode.NoWrap);
        editor.setFont(font);

        new Highlighter(editor.document());

        QFile file = new QFile("classpath:com/trolltech/examples/SyntaxHighlighter.java");
        if (file.open(new QFile.OpenMode(QFile.OpenModeFlag.ReadOnly, QFile.OpenModeFlag.Text)))
            editor.setPlainText(file.readAll().toString());
    }

    private void setupFileMenu() {
        QMenu fileMenu = new QMenu(tr("&File"), this);
        menuBar().addMenu(fileMenu);

        QAction newAct = new QAction(tr("&New"), this);
        newAct.triggered.connect(this, "newFile()");
        fileMenu.addAction(newAct);

        QAction openAct = new QAction(tr("&Open..."), this);
        openAct.triggered.connect(this, "openFile()");
        fileMenu.addAction(openAct);

        QAction quitAct = new QAction(tr("E&xit"), this);
        quitAct.triggered.connect(this, "quit()");
        fileMenu.addAction(quitAct);
    }

    private void setupHelpMenu() {
        QMenu helpMenu = new QMenu(tr("&Help"), this);
        menuBar().addMenu(helpMenu);

        QAction aboutAct = new QAction(tr("&About"), this);
        aboutAct.triggered.connect(this, "about()");
        helpMenu.addAction(aboutAct);

        QAction aboutQtAct = new QAction(tr("About &Qt"), this);
        aboutQtAct.triggered.connect(this, "aboutQt()");
        helpMenu.addAction(aboutQtAct);
    }

    private class Highlighter extends QSyntaxHighlighter {

        public class HighlightingRule {
            public QRegExp pattern;
            public QTextCharFormat format;

            public HighlightingRule(QRegExp pattern, QTextCharFormat format) {
                this.pattern = pattern;
                this.format = format;
            }
        }

        Vector<HighlightingRule> highlightingRules = new Vector<HighlightingRule>();

        QRegExp commentStartExpression;
        QRegExp commentEndExpression;

        QTextCharFormat keywordFormat = new QTextCharFormat();
        QTextCharFormat classFormat = new QTextCharFormat();
        QTextCharFormat commentFormat = new QTextCharFormat();
        QTextCharFormat quotationFormat = new QTextCharFormat();
        QTextCharFormat functionFormat = new QTextCharFormat();

        public Highlighter(QTextDocument doc) {
            super(doc);

            QBrush brush = new QBrush(Qt.BrushStyle.SolidPattern);
            brush.setColor(QColor.darkBlue);
            keywordFormat.setForeground(brush);
            keywordFormat.setFontWeight(QFont.Weight.Bold.value());

            // All the java keywords
            String[] keywords = { "abstract", "continue", "for", "new", "switch", "assert",
                    "default", "goto", "package", "synchronized", "boolean", "do", "if", "private",
                    "this", "break", "double", "implements", "protected", "throw", "byte", "else",
                    "import", "public", "throws", "case", "enum", "instanceof", "return",
                    "transient", "catch", "extends", "int", "short", "try", "char", "final",
                    "interface", "static", "void", "class", "finally", "long", "strictfp",
                    "volatile", "const", "float", "native", "super", "while" };

            for (String keyword : keywords) {
                highlightingRules.add(new HighlightingRule(new QRegExp("\\b" + keyword + "\\b"),
                        keywordFormat));
            }

            // Any word starting with Q
            classFormat.setFontWeight(QFont.Weight.Bold.value());
            classFormat.setForeground(new QBrush(QColor.darkMagenta));
            highlightingRules
                    .add(new HighlightingRule(new QRegExp("\\bQ[A-Za-z]+\\b"), classFormat));

            // Comment starting with //
            commentFormat.setForeground(new QBrush(QColor.gray, Qt.BrushStyle.SolidPattern));
            highlightingRules.add(new HighlightingRule(new QRegExp("//[^\n]*"), commentFormat));

            // String
            quotationFormat.setForeground(new QBrush(QColor.blue, Qt.BrushStyle.SolidPattern));
            highlightingRules.add(new HighlightingRule(new QRegExp("\".*\""), quotationFormat));

            // Function
            functionFormat.setFontItalic(true);
            functionFormat.setForeground(new QBrush(QColor.darkGreen, Qt.BrushStyle.SolidPattern));
            highlightingRules.add(new HighlightingRule(new QRegExp("\\b[A-Za-z0-9_]+(?=\\()"),
                    functionFormat));

            // Block comment
            commentStartExpression = new QRegExp("/\\*");
            commentEndExpression = new QRegExp("\\*/");
        }

        public void highlightBlock(String text) {

            for (HighlightingRule rule : highlightingRules) {
                QRegExp expression = rule.pattern;
                int index = expression.indexIn(text);
                while (index >= 0) {
                    int length = expression.matchedLength();
                    setFormat(index, length, rule.format);
                    index = expression.indexIn(text, index + length);
                }
            }
            setCurrentBlockState(0);

            int startIndex = 0;
            if (previousBlockState() != 1)
                startIndex = commentStartExpression.indexIn(text);

            while (startIndex >= 0) {
                int endIndex = commentEndExpression.indexIn(text, startIndex);
                int commentLength;
                if (endIndex == -1) {
                    setCurrentBlockState(1);
                    commentLength = text.length() - startIndex;
                } else {
                    commentLength = endIndex - startIndex + commentEndExpression.matchedLength();
                }
                setFormat(startIndex, commentLength, commentFormat);
                startIndex = commentStartExpression.indexIn(text, startIndex + commentLength);
            }
        }
    }
}
