/****************************************************************************
 **
 **  (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
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

import com.trolltech.qt.core.Qt.AlignmentFlag;
import com.trolltech.qt.gui.*;

@QtJambiExample(name = "Menus")
public class Menus extends QMainWindow {

    public static void main(String args[]) {
        QApplication.initialize(args);
        Menus menus = new Menus();
        menus.show();
        QApplication.exec();
    }

    QMenu fileMenu;
    QMenu editMenu;
    QMenu formatMenu;
    QMenu helpMenu;
    QActionGroup alignmentGroup;
    QAction newAct;
    QAction openAct;
    QAction saveAct;
    QAction printAct;
    QAction exitAct;
    QAction undoAct;
    QAction redoAct;
    QAction cutAct;
    QAction copyAct;
    QAction pasteAct;
    QAction boldAct;
    QAction italicAct;
    QAction leftAlignAct;
    QAction rightAlignAct;
    QAction justifyAct;
    QAction centerAct;
    QAction setLineSpacingAct;
    QAction setParagraphSpacingAct;
    QAction aboutAct;
    QAction aboutQtAct;
    QLabel infoLabel;

    public Menus() {
        QWidget widget = new QWidget();
        setCentralWidget(widget);

        QWidget topFiller = new QWidget();
        topFiller.setSizePolicy(QSizePolicy.Policy.Expanding, 
                                QSizePolicy.Policy.Expanding);

        infoLabel = new QLabel(tr("<i>Choose a menu option, " 
                                  + "or right-click to invoke "
                                  + "a context menu</i>"));
        infoLabel.setFrameStyle(QFrame.Shape.StyledPanel.value() 
                                | QFrame.Shadow.Sunken.value());
        infoLabel.setAlignment(AlignmentFlag.AlignCenter);

        QWidget bottomFiller = new QWidget();
        bottomFiller.setSizePolicy(QSizePolicy.Policy.Expanding, 
                                   QSizePolicy.Policy.Expanding);

        QVBoxLayout layout = new QVBoxLayout();
        layout.setMargin(5);
        layout.addWidget(topFiller);
        layout.addWidget(infoLabel);
        layout.addWidget(bottomFiller);
        widget.setLayout(layout);

        createActions();
        createMenus();

        statusBar().showMessage(tr("A context menu is available by "
                                   + "right-clicking"));

        setWindowTitle(tr("Menus"));
        setMinimumSize(160, 160);
        resize(480, 320);
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
    }

    public void contextMenuEvent(QContextMenuEvent event) {
        QMenu menu = new QMenu(this);
        menu.addAction(cutAct);
        menu.addAction(copyAct);
        menu.addAction(pasteAct);
        menu.exec(event.globalPos());
    }

    void newFile() {
        infoLabel.setText(tr("Invoked <b>File|New</b>"));
    }

    void open() {
        infoLabel.setText(tr("Invoked <b>File|Open</b>"));
    }

    void save() {
        infoLabel.setText(tr("Invoked <b>File|Save</b>"));
    }

    void print() {
        infoLabel.setText(tr("Invoked <b>File|Print</b>"));
    }

    void undo() {
        infoLabel.setText(tr("Invoked <b>Edit|Undo</b>"));
    }

    void redo() {
        infoLabel.setText(tr("Invoked <b>Edit|Redo</b>"));
    }

    void cut() {
        infoLabel.setText(tr("Invoked <b>Edit|Cut</b>"));
    }

    void copy() {
        infoLabel.setText(tr("Invoked <b>Edit|Copy</b>"));
    }

    void paste() {
        infoLabel.setText(tr("Invoked <b>Edit|Paste</b>"));
    }

    void bold() {
        infoLabel.setText(tr("Invoked <b>Edit|Format|Bold</b>"));
    }

    void italic() {
        infoLabel.setText(tr("Invoked <b>Edit|Format|Italic</b>"));
    }

    void leftAlign() {
        infoLabel.setText(tr("Invoked <b>Edit|Format|Left Align</b>"));
    }

    void rightAlign() {
        infoLabel.setText(tr("Invoked <b>Edit|Format|Right Align</b>"));
    }

    void justify() {
        infoLabel.setText(tr("Invoked <b>Edit|Format|Justify</b>"));
    }

    void center() {
        infoLabel.setText(tr("Invoked <b>Edit|Format|Center</b>"));
    }

    void setLineSpacing() {
        infoLabel.setText(tr("Invoked <b>Edit|Format|Set Line Spacing</b>"));
    }

    void setParagraphSpacing() {
        infoLabel.setText(tr("Invoked <b>Edit|Format|"
                             + "Set Paragraph Spacing</b>"));
    }

    void about() {
        infoLabel.setText(tr("Invoked <b>Help|About</b>"));
        QMessageBox.about(this, tr("About Menu"),
                          tr("The <b>Menu</b> example shows how to create "
                             + "menu-bar menus and context menus."));
    }

    void aboutQt() {
        infoLabel.setText(tr("Invoked <b>Help|About Qt</b>"));
    }

    void createActions() {
        String tip;
        newAct = new QAction(tr("&New"), this);
        newAct.setShortcut(tr("Ctrl+N"));
        tip = tr("Create a new file");
        newAct.setStatusTip(tip);
        newAct.triggered.connect(this, "newFile()");

        openAct = new QAction(tr("&Open..."), this);
        openAct.setShortcut(tr("Ctrl+O"));
        tip = tr("Open an existing file");
        openAct.setStatusTip(tip);
        openAct.triggered.connect(this, "open()");

        saveAct = new QAction(tr("&Save"), this);
        saveAct.setShortcut(tr("Ctrl+S"));
        tip = tr("Save the document to disk");
        saveAct.setStatusTip(tip);
        saveAct.triggered.connect(this, "save()");

        printAct = new QAction(tr("&Print..."), this);
        printAct.setShortcut(tr("Ctrl+P"));
        tip = tr("Print the document");
        printAct.setStatusTip(tip);
        printAct.triggered.connect(this, "print()");

        exitAct = new QAction(tr("E&xit"), this);
        exitAct.setShortcut(tr("Ctrl+Q"));
        tip = tr("Exit the application");
        exitAct.setStatusTip(tip);
        exitAct.triggered.connect(this, "close()");

        undoAct = new QAction(tr("&Undo"), this);
        undoAct.setShortcut(tr("Ctrl+Z"));
        tip = tr("Undo the last operation");
        undoAct.setStatusTip(tip);
        undoAct.triggered.connect(this, "undo()");

        redoAct = new QAction(tr("&Redo"), this);
        redoAct.setShortcut(tr("Ctrl+Y"));
        tip = tr("Redo the last operation");
        redoAct.setStatusTip(tip);
        redoAct.triggered.connect(this, "redo()");

        cutAct = new QAction(tr("Cu&t"), this);
        cutAct.setShortcut(tr("Ctrl+X"));
        tip = tr("Cut the current selection's contents to the clipboard");
        cutAct.setStatusTip(tip);
        cutAct.triggered.connect(this, "cut()");

        copyAct = new QAction(tr("&Copy"), this);
        copyAct.setShortcut(tr("Ctrl+C"));
        tip = tr("Copy the current selection's contents to the clipboard");
        copyAct.setStatusTip(tip);
        copyAct.triggered.connect(this, "copy()");

        pasteAct = new QAction(tr("&Paste"), this);
        pasteAct.setShortcut(tr("Ctrl+V"));
        tip = tr("Paste the clipboard's contents into the current selection");
        pasteAct.setStatusTip(tip);
        pasteAct.triggered.connect(this, "paste()");

        boldAct = new QAction(tr("&Bold"), this);
        boldAct.setCheckable(true);
        boldAct.setShortcut(tr("Ctrl+B"));
        tip = tr("Make the text bold");
        boldAct.setStatusTip(tip);
        boldAct.triggered.connect(this, "bold()");

        QFont boldFont = boldAct.font();
        boldFont.setBold(true);
        boldAct.setFont(boldFont);

        italicAct = new QAction(tr("&Italic"), this);
        italicAct.setCheckable(true);
        italicAct.setShortcut(tr("Ctrl+I"));
        tip = tr("Make the text italic");
        italicAct.setStatusTip(tip);
        italicAct.triggered.connect(this, "italic()");

        QFont italicFont = italicAct.font();
        italicFont.setItalic(true);
        italicAct.setFont(italicFont);

        setLineSpacingAct = new QAction(tr("Set &Line Spacing..."), this);
        tip = tr("Change the gap between the lines of a paragraph");
        setLineSpacingAct.setStatusTip(tip);
        setLineSpacingAct.triggered.connect(this, "setLineSpacing()");

        setParagraphSpacingAct = new QAction(tr("Set &Paragraph Spacing..."), 
                                             this);
        tip = tr("Change the gap between paragraphs");
        setLineSpacingAct.setStatusTip(tip);
        setParagraphSpacingAct.triggered.connect(this, "setParagraphSpacing()");

        aboutAct = new QAction(tr("&About"), this);
        tip = tr("Show the application's About box");
        aboutAct.setStatusTip(tip);
        aboutAct.triggered.connect(this, "about()");

        aboutQtAct = new QAction(tr("About &Qt"), this);
        tip = tr("Show the Qt library's About box");
        aboutQtAct.setStatusTip(tip);
        aboutQtAct.triggered.connect(QApplication.instance(), "aboutQt()");
        aboutQtAct.triggered.connect(this, "aboutQt()");

        leftAlignAct = new QAction(tr("&Left Align"), this);
        leftAlignAct.setCheckable(true);
        leftAlignAct.setShortcut(tr("Ctrl+L"));
        tip = tr("Left align the selected text");
        leftAlignAct.setStatusTip(tip);
        leftAlignAct.triggered.connect(this, "leftAlign()");

        rightAlignAct = new QAction(tr("&Right Align"), this);
        rightAlignAct.setCheckable(true);
        rightAlignAct.setShortcut(tr("Ctrl+R"));
        tip = tr("Right align the selected text");
        rightAlignAct.setStatusTip(tip);
        rightAlignAct.triggered.connect(this, "rightAlign()");

        justifyAct = new QAction(tr("&Justify"), this);
        justifyAct.setCheckable(true);
        justifyAct.setShortcut(tr("Ctrl+J"));
        tip = tr("Justify the selected text");
        justifyAct.setStatusTip(tip);
        justifyAct.triggered.connect(this, "justify()");

        centerAct = new QAction(tr("&Center"), this);
        centerAct.setCheckable(true);
        centerAct.setShortcut(tr("Ctrl+E"));
        tip = tr("Center the selected text");
        centerAct.setStatusTip(tip);
        centerAct.triggered.connect(this, "center()");

        alignmentGroup = new QActionGroup(this);
        alignmentGroup.addAction(leftAlignAct);
        alignmentGroup.addAction(rightAlignAct);
        alignmentGroup.addAction(justifyAct);
        alignmentGroup.addAction(centerAct);
        leftAlignAct.setChecked(true);
    }

    void createMenus() {
        fileMenu = menuBar().addMenu(tr("&File"));
        fileMenu.addAction(newAct);
        fileMenu.addAction(openAct);
        fileMenu.addAction(saveAct);
        fileMenu.addAction(printAct);
        fileMenu.addSeparator();
        fileMenu.addAction(exitAct);

        editMenu = menuBar().addMenu(tr("&Edit"));
        editMenu.addAction(undoAct);
        editMenu.addAction(redoAct);
        editMenu.addSeparator();
        editMenu.addAction(cutAct);
        editMenu.addAction(copyAct);
        editMenu.addAction(pasteAct);
        editMenu.addSeparator();

        helpMenu = menuBar().addMenu(tr("&Help"));
        helpMenu.addAction(aboutAct);
        helpMenu.addAction(aboutQtAct);

        formatMenu = editMenu.addMenu(tr("&Format"));
        formatMenu.addAction(boldAct);
        formatMenu.addAction(italicAct);
        formatMenu.addSeparator().setText(tr("Alignment"));
        formatMenu.addAction(leftAlignAct);
        formatMenu.addAction(rightAlignAct);
        formatMenu.addAction(justifyAct);
        formatMenu.addAction(centerAct);
        formatMenu.addSeparator();
        formatMenu.addAction(setLineSpacingAct);
        formatMenu.addAction(setParagraphSpacingAct);
    }
}
