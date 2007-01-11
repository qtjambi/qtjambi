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

package com.trolltech.demos;

import com.trolltech.examples.QtJambiExample;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

@QtJambiExample(name = "Text Edit")
public class TextEdit extends QMainWindow {
    private String fileName;
    private QTextEdit textEdit;
    private QAction actionSave;
    private QAction actionUndo;
    private QAction actionRedo;
    private QAction actionCut;
    private QAction actionCopy;
    private QAction actionPaste;
    private QAction actionTextBold;
    private QAction actionTextItalic;
    private QAction actionTextUnderline;
    private QAction actionTextColor;
    private QAction actionAlignLeft;
    private QAction actionAlignRight;
    private QAction actionAlignCenter;
    private QAction actionAlignJustify;
    private QComboBox comboStyle;
    private QComboBox comboFont;
    private QComboBox comboSize;
    private String rsrcPath = "classpath:com/trolltech/images/textedit/win";
    private boolean initialized = false;

    void init() {
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));

        setupFileActions();
        setupEditActions();
        setupTextActions();

        textEdit = new QTextEdit(this);
        textEdit.currentCharFormatChanged.connect(this,
                "currentCharFormatChanged(QTextCharFormat)");

        setCentralWidget(textEdit);
        textEdit.setFocus();
        setCurrentFileName("");

        fontChanged(textEdit.font());
        colorChanged(textEdit.textColor());
        alignmentChanged(textEdit.alignment());

        textEdit.document().modificationChanged.connect(actionSave,
                "setEnabled(boolean)");
        textEdit.document().modificationChanged.connect(this,
                "setWindowModified(boolean)");
        textEdit.document().undoAvailable.connect(actionUndo,
                "setEnabled(boolean)");
        textEdit.document().redoAvailable.connect(actionRedo,
                "setEnabled(boolean)");

        setWindowModified(textEdit.document().isModified());
        actionSave.setEnabled(textEdit.document().isModified());
        actionUndo.setEnabled(textEdit.document().isUndoAvailable());
        actionRedo.setEnabled(textEdit.document().isRedoAvailable());

        actionUndo.triggered.connect(textEdit.document(), "undo()");
        actionRedo.triggered.connect(textEdit.document(), "redo()");

        actionCut.setEnabled(false);
        actionCopy.setEnabled(false);

        actionCut.triggered.connect(textEdit, "cut()");
        actionCopy.triggered.connect(textEdit, "copy()");
        actionPaste.triggered.connect(textEdit, "paste()");

        textEdit.copyAvailable.connect(actionCut, "setEnabled(boolean)");
        textEdit.copyAvailable.connect(actionCopy, "setEnabled(boolean)");

        QApplication.clipboard().dataChanged.connect(this,
                "clipboardDataChanged()");

        String initialFile = "classpath:com/trolltech/demos/example.html";

        if (!load(initialFile))
            fileNew();

        initialized = true;

        resize(640, 800);
    }

    protected void showEvent(QShowEvent e) {
        if (!initialized)
            init();
        super.showEvent(e);
    }

    protected void closeEvent(QCloseEvent e) {
        if (maybeSave())
            e.accept();
        else
            e.ignore();
    }

    void setupFileActions() {
        QToolBar tb = new QToolBar(this);
        tb.setWindowTitle("File Actions");
        addToolBar(tb);

        QMenu menu = new QMenu("&File", this);
        menuBar().addMenu(menu);

        QAction a = new QAction(new QIcon(rsrcPath + "/filenew.png"), "&New",
                this);
        a.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_N.value()));
        a.triggered.connect(this, "fileNew()");
        menu.addAction(a);

        a = new QAction(new QIcon(rsrcPath + "/fileopen.png"), "&Open...", this);
        a.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_O.value()));
        a.triggered.connect(this, "fileOpen()");
        tb.addAction(a);
        menu.addAction(a);

        menu.addSeparator();

        actionSave = a = new QAction(new QIcon(rsrcPath + "/filesave.png"),
                "&Save", this);
        a.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_S.value()));
        a.triggered.connect(this, "fileSave()");
        a.setEnabled(false);
        tb.addAction(a);
        menu.addAction(a);

        a = new QAction("Save &As...", this);
        a.triggered.connect(this, "fileSaveAs()");
        menu.addAction(a);
        menu.addSeparator();

        a = new QAction(new QIcon(rsrcPath + "/fileprint.png"), "&Print...",
                this);
        a.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_P.value()));
        a.triggered.connect(this, "filePrint()");
        tb.addAction(a);
        menu.addAction(a);

        a = new QAction(new QIcon(rsrcPath + "/exportpdf.png"),
                "&Export PDF...", this);
        a.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_D.value()));
        a.triggered.connect(this, "filePrintPdf()");
        tb.addAction(a);
        menu.addAction(a);

        menu.addSeparator();

        a = new QAction("&Quit", this);
        a.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_Q.value()));
        a.triggered.connect(this, "close()");
        menu.addAction(a);
    }

    void setupEditActions() {
        QToolBar tb = new QToolBar(this);
        tb.setWindowTitle("Edit Actions");
        addToolBar(tb);

        QMenu menu = new QMenu("&Edit", this);
        menuBar().addMenu(menu);

        QAction a;
        a = actionUndo = new QAction(new QIcon(rsrcPath + "/editundo.png"),
                "&Undo", this);
        a.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_Z.value()));
        tb.addAction(a);
        menu.addAction(a);
        a = actionRedo = new QAction(new QIcon(rsrcPath + "/editredo.png"),
                "&Redo", this);
        a.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_Y.value()));
        tb.addAction(a);
        menu.addAction(a);
        menu.addSeparator();
        a = actionCut = new QAction(new QIcon(rsrcPath + "/editcut.png"),
                "Cu&t", this);
        a.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_X.value()));
        tb.addAction(a);
        menu.addAction(a);
        a = actionCopy = new QAction(new QIcon(rsrcPath + "/editcopy.png"),
                "&Copy", this);
        a.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_C.value()));
        tb.addAction(a);
        menu.addAction(a);
        a = actionPaste = new QAction(new QIcon(rsrcPath + "/editpaste.png"),
                "&Paste", this);
        a.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_V.value()));
        tb.addAction(a);
        menu.addAction(a);
        actionPaste.setEnabled(QApplication.clipboard().text().length() > 0);
    }

    void setupTextActions() {
        QToolBar tb = new QToolBar(this);
        tb.setWindowTitle("Format Actions");
        addToolBar(tb);

        QMenu menu = new QMenu("F&ormat", this);
        menuBar().addMenu(menu);

        actionTextBold = new QAction(new QIcon(rsrcPath + "/textbold.png"),
                "&Bold", this);
        actionTextBold.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_B.value()));
        QFont bold = new QFont();
        bold.setBold(true);
        actionTextBold.setFont(bold);
        actionTextBold.triggered.connect(this, "textBold()");
        tb.addAction(actionTextBold);
        menu.addAction(actionTextBold);
        actionTextBold.setCheckable(true);

        actionTextItalic = new QAction(new QIcon(rsrcPath + "/textitalic.png"),
                "&Italic", this);
        actionTextItalic.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_I.value()));
        QFont italic = new QFont();
        italic.setItalic(true);
        actionTextItalic.setFont(italic);
        actionTextItalic.triggered.connect(this, "textItalic()");
        tb.addAction(actionTextItalic);
        menu.addAction(actionTextItalic);
        actionTextItalic.setCheckable(true);

        actionTextUnderline = new QAction(
                new QIcon(rsrcPath + "/textunder.png"), "&Underline", this);
        actionTextUnderline.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_U.value()));
        QFont underline = new QFont();
        underline.setUnderline(true);
        actionTextUnderline.setFont(underline);
        actionTextUnderline.triggered.connect(this, "textUnderline()");
        tb.addAction(actionTextUnderline);
        menu.addAction(actionTextUnderline);
        actionTextUnderline.setCheckable(true);

        menu.addSeparator();

        QActionGroup grp = new QActionGroup(this);
        grp.triggered.connect(this, "textAlign(QAction)");

        actionAlignLeft = new QAction(new QIcon(rsrcPath + "/textleft.png"),
                "&Left", grp);
        actionAlignLeft.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_L.value()));
        actionAlignLeft.setCheckable(true);
        actionAlignCenter = new QAction(
                new QIcon(rsrcPath + "/textcenter.png"), "C&enter", grp);
        actionAlignCenter.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_E.value()));
        actionAlignCenter.setCheckable(true);
        actionAlignRight = new QAction(new QIcon(rsrcPath + "/textright.png"),
                "&Right", grp);
        actionAlignRight.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_R.value()));
        actionAlignRight.setCheckable(true);
        actionAlignJustify = new QAction(new QIcon(rsrcPath
                + "/textjustify.png"), "&Justify", grp);
        actionAlignJustify.setShortcut(new QKeySequence(Qt.Modifier.CTRL.value(), Qt.Key.Key_J.value()));
        actionAlignJustify.setCheckable(true);

        tb.addActions(grp.actions());
        menu.addActions(grp.actions());

        menu.addSeparator();

        QPixmap pix = new QPixmap(16, 16);
        pix.fill(QColor.black);
        actionTextColor = new QAction(new QIcon(pix), "&Color...", this);
        actionTextColor.triggered.connect(this, "textColor()");
        tb.addAction(actionTextColor);
        menu.addAction(actionTextColor);

        tb = new QToolBar(this);
        tb.setAllowedAreas(new Qt.ToolBarAreas(Qt.ToolBarArea.TopToolBarArea, Qt.ToolBarArea.BottomToolBarArea));
        tb.setWindowTitle("Format Actions");
        addToolBarBreak(Qt.ToolBarArea.TopToolBarArea);
        addToolBar(tb);

        comboStyle = new QComboBox(tb);
        tb.addWidget(comboStyle);
        comboStyle.addItem("Standard", null);
        comboStyle.addItem("Bullet List (Disc)", null);
        comboStyle.addItem("Bullet List (Circle)", null);
        comboStyle.addItem("Bullet List (Square)", null);
        comboStyle.addItem("Ordered List (Decimal)", null);
        comboStyle.addItem("Ordered List (Alpha lower)", null);
        comboStyle.addItem("Ordered List (Alpha upper)", null);
        comboStyle.activatedIndex.connect(this, "textStyle(int)");

        comboFont = new QComboBox(tb);
        tb.addWidget(comboFont);
        comboFont.setEditable(true);
        QFontDatabase db = new QFontDatabase();
        comboFont.addItems(db.families());
        comboFont.activated.connect(this, "textFamily(String)");
        comboFont.setCurrentIndex(comboFont.findText(QApplication.font()
                .family(), new Qt.MatchFlags(Qt.MatchFlag.MatchExactly, Qt.MatchFlag.MatchCaseSensitive)));

        comboSize = new QComboBox(tb);
        comboSize.setObjectName("comboSize");
        tb.addWidget(comboSize);
        comboSize.setEditable(true);

        for (int i = 0; i < QFontDatabase.standardSizes().size(); ++i) {
            int size = QFontDatabase.standardSizes().get(i);
            comboSize.addItem("" + size, null);
        }

        comboSize.activated.connect(this, "textSize(String)");
        comboSize.setCurrentIndex(comboSize.findText(""
                + QApplication.font().pointSize(),
                new Qt.MatchFlags(Qt.MatchFlag.MatchExactly, Qt.MatchFlag.MatchCaseSensitive)));
    }

    boolean load(String f) {
        if (!QFile.exists(f))
            return false;
        QFile file = new QFile(f);
        if (!file.open(new QFile.OpenMode(QFile.OpenModeFlag.ReadOnly)))
            return false;

        QByteArray data = file.readAll();
        QTextCodec codec = QTextCodec.codecForHtml(data);
        String str = codec.toUnicode(data);

        if (str.toLowerCase().indexOf("<html") >= 0)
            textEdit.setHtml(str);
        else
            textEdit.setPlainText(str);

        setCurrentFileName(f);
        return true;
    }

    boolean maybeSave() {
        if (!textEdit.document().isModified())
            return true;
        QMessageBox.StandardButton ret = QMessageBox.warning(this,
                                      "Application",
                                      "The document has been modified.\n"
                                      + "Save your changes?",
                                      new QMessageBox.StandardButtons(QMessageBox.StandardButton.Ok, QMessageBox.StandardButton.Discard, QMessageBox.StandardButton.Cancel));
        if (ret == QMessageBox.StandardButton.Yes)
            return fileSave();
        else if (ret == QMessageBox.StandardButton.Cancel)
            return false;
        return true;
    }

    void setCurrentFileName(String fileName) {
        this.fileName = fileName;
        textEdit.document().setModified(false);

        String shownName;
        if (fileName.length() == 0)
            shownName = "untitled.txt";
        else
            shownName = new QFileInfo(fileName).fileName();

        setWindowTitle(shownName + "[*] - Rich Text");
        setWindowModified(false);
    }

    public void fileNew() {
        if (maybeSave()) {
            textEdit.clear();
            setCurrentFileName("");
        }
    }

    public void fileOpen() {
        String fn = QFileDialog.getOpenFileName(this, "Open File...", "",
                new QFileDialog.Filter("HTML-Files (*.htm *.html);;All Files (*)"));
        if (fn.length() != 0)
            load(fn);
    }

    public boolean fileSave() {
        if (fileName.length() == 0)
            return fileSaveAs();

        QFile file = new QFile(fileName);
        if (!file.open(new QFile.OpenMode(QFile.OpenModeFlag.WriteOnly)))
            return false;
        QTextStream ts = new QTextStream(file);
        ts.setCodec(QTextCodec.codecForName(new QByteArray("UTF-8")));
        ts.operator_shift_left(textEdit.document().toHtml(
                new QByteArray("UTF-8")));
        textEdit.document().setModified(false);

        file.close();

        return true;
    }

    public boolean fileSaveAs() {
        String fn = QFileDialog.getSaveFileName(this, "Save as...", "",
                new QFileDialog.Filter("HTML-Files (*.htm *.html);;All Files (*)"));
        if (fn.length() == 0)
            return false;
        setCurrentFileName(fn);
        return fileSave();
    }

    public void filePrint() {
        QPrinter printer = new QPrinter(QPrinter.PrinterMode.HighResolution);
        printer.setFullPage(true);
        QPrintDialog dlg = new QPrintDialog(printer, this);
        if (dlg.exec() == QDialog.DialogCode.Accepted.value()) {
            textEdit.document().print(printer);
        }
    }

    public void filePrintPdf() {
        String fileName = QFileDialog.getSaveFileName(this, "Export PDF", "",
                new QFileDialog.Filter("*.pdf"));
        if (fileName.length() == 0)
            return;
        QPrinter printer = new QPrinter(QPrinter.PrinterMode.HighResolution);
        printer.setOutputFormat(QPrinter.OutputFormat.PdfFormat);
        printer.setOutputFileName(fileName);
        textEdit.document().print(printer);
    }

    public void textBold() {
        textEdit.setFontWeight(actionTextBold.isChecked()
                ? QFont.Weight.Bold.value()
                : QFont.Weight.Normal.value());
    }

    public void textUnderline() {
        textEdit.setFontUnderline(actionTextUnderline.isChecked());
    }

    public void textItalic() {
        textEdit.setFontItalic(actionTextItalic.isChecked());
    }

    public void textFamily(String f) {
        textEdit.setFontFamily(f);
    }

    public void textSize(String p) {
        textEdit.setFontPointSize(new Float(p));
    }

    public void textStyle(int styleIndex) {
        QTextCursor cursor = textEdit.textCursor();

        if (styleIndex != 0) {
            QTextListFormat.Style style = QTextListFormat.Style.ListDisc;

            switch (styleIndex) {
            default:
            case 1:
                style = QTextListFormat.Style.ListDisc;
                break;
            case 2:
                style = QTextListFormat.Style.ListCircle;
                break;
            case 3:
                style = QTextListFormat.Style.ListSquare;
                break;
            case 4:
                style = QTextListFormat.Style.ListDecimal;
                break;
            case 5:
                style = QTextListFormat.Style.ListLowerAlpha;
                break;
            case 6:
                style = QTextListFormat.Style.ListUpperAlpha;
                break;
            }

            cursor.beginEditBlock();

            QTextBlockFormat blockFmt = cursor.blockFormat();

            QTextListFormat listFmt = new QTextListFormat();

            if (cursor.currentList() != null) {
                listFmt = cursor.currentList().format();
            } else {
                listFmt.setIndent(blockFmt.indent() + 1);
                blockFmt.setIndent(0);
                cursor.setBlockFormat(blockFmt);
            }

            listFmt.setStyle(style);

            cursor.createList(listFmt);

            cursor.endEditBlock();
        } else {
            QTextBlockFormat bfmt = new QTextBlockFormat();
            bfmt.setObjectIndex(-1);
            cursor.mergeBlockFormat(bfmt);
        }
    }

    public void textColor() {
        QColor col = QColorDialog.getColor(textEdit.textColor(), this);
        if (!col.isValid())
            return;
        textEdit.setTextColor(col);
        colorChanged(col);
    }

    public void textAlign(QAction a) {
        if (a == actionAlignLeft)
            textEdit.setAlignment(new Qt.Alignment(Qt.AlignmentFlag.AlignLeft));
        else if (a == actionAlignCenter)
            textEdit.setAlignment(new Qt.Alignment(Qt.AlignmentFlag.AlignHCenter));
        else if (a == actionAlignRight)
            textEdit.setAlignment(new Qt.Alignment(Qt.AlignmentFlag.AlignRight));
        else if (a == actionAlignJustify)
            textEdit.setAlignment(new Qt.Alignment(Qt.AlignmentFlag.AlignJustify));
    }

    public void currentCharFormatChanged(QTextCharFormat format) {
        fontChanged(format.font());
        colorChanged(format.foreground().color());
        alignmentChanged(textEdit.alignment());
    }

    public void clipboardDataChanged() {
        actionPaste.setEnabled(QApplication.clipboard().text().length() > 0);
    }

    public void fontChanged(QFont f) {
        comboFont.setCurrentIndex(comboFont.findText(f.family(),
                new Qt.MatchFlags(Qt.MatchFlag.MatchExactly, Qt.MatchFlag.MatchCaseSensitive)));
        comboSize.setCurrentIndex(comboSize.findText(new Integer(f.pointSize())
                .toString(), new Qt.MatchFlags(Qt.MatchFlag.MatchExactly, Qt.MatchFlag.MatchCaseSensitive)));
        actionTextBold.setChecked(f.bold());
        actionTextItalic.setChecked(f.italic());
        actionTextUnderline.setChecked(f.underline());
    }

    public void colorChanged(QColor c) {
        QPixmap pix = new QPixmap(16, 16);
        pix.fill(c);
        actionTextColor.setIcon(new QIcon(pix));
    }

    public void alignmentChanged(Qt.Alignment a) {
        if (a.isSet(Qt.AlignmentFlag.AlignLeft))
            actionAlignLeft.setChecked(true);
        else if (a.isSet(Qt.AlignmentFlag.AlignHCenter))
            actionAlignCenter.setChecked(true);
        else if (a.isSet(Qt.AlignmentFlag.AlignRight))
            actionAlignRight.setChecked(true);
        else if (a.isSet(Qt.AlignmentFlag.AlignJustify))
            actionAlignJustify.setChecked(true);
    }

    static public void main(String args[]) {
        QApplication.initialize(args);

        TextEdit mw = new TextEdit();
        if (args.length > 0) {
            mw.init();
            mw.load(args[0]);
        }
        mw.show();


        QApplication.exec();
    }

}
