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

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class Application extends QMainWindow {

    private String curFile;
    private QTextEdit textEdit;
    private QMenu fileMenu;
    private QMenu editMenu;
    private QMenu helpMenu;

    private QToolBar fileToolBar;
    private QToolBar editToolBar;

    private QAction newAct;
    private QAction openAct;
    private QAction saveAct;
    private QAction saveAsAct;
    private QAction exitAct;
    private QAction cutAct;
    private QAction copyAct;
    private QAction pasteAct;
    private QAction aboutAct;
    private QAction aboutQtAct;

    private String rsrcPath = "classpath:com/trolltech/images";

    public Application()
    {
        QMenuBar menuBar = new QMenuBar();
        setMenuBar(menuBar);

        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));

        textEdit = new QTextEdit();
        setCentralWidget(textEdit);

        try {
            createActions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        createMenus();
        createToolBars();
        createStatusBar();

        readSettings();

        textEdit.document().contentsChanged.connect(this, "documentWasModified()");

        setCurrentFile("");
    }

    public void closeEvent(QCloseEvent event)
    {
        if (maybeSave()) {
            writeSettings();
            event.accept();
        } else {
            event.ignore();
        }
    }

    public void newFile()
    {
        if (maybeSave()) {
            textEdit.clear();
            setCurrentFile("");
        }
    }

    public void open()
    {
        if (maybeSave()) {
            String fileName = QFileDialog.getOpenFileName(this);
            if (fileName.length() != 0)
                loadFile(fileName);
        }
    }

    public boolean save()
    {
        if (curFile.length() == 0) {
            return saveAs();
        } else {
            return saveFile(curFile);
        }
    }

    public boolean saveAs()
    {
        String fileName = QFileDialog.getSaveFileName(this);
        if (fileName.length() == 0)
            return false;

        return saveFile(fileName);
    }

    public void about()
    {
        QMessageBox.about(this,
                         tr("About Application"),
                         tr("The <b>Application</b> example demonstrates how to " +
                            "write modern GUI applications using Qt, with a menu bar, " +
                            "toolbars, and a status bar."));
    }

    public void documentWasModified()
    {
        setWindowModified(textEdit.document().isModified());
    }

    private void createActions()
    {
        newAct = new QAction(new QIcon(rsrcPath + "/new.png"), tr("&New"), this);
        newAct.setShortcut(new QKeySequence(tr("Ctrl+N")));
        newAct.setStatusTip(tr("Create a new file"));
        newAct.triggered.connect(this, "newFile()");

        openAct = new QAction(new QIcon(rsrcPath + "/open.png"), tr("&Open..."), this);
        openAct.setShortcut(tr("Ctrl+O"));
        openAct.setStatusTip(tr("Open an existing file"));
        openAct.triggered.connect(this, "open()");

        saveAct = new QAction(new QIcon(rsrcPath + "/save.png"), tr("&Save"), this);
        saveAct.setShortcut(tr("Ctrl+S"));
        saveAct.setStatusTip(tr("Save the document to disk"));
        saveAct.triggered.connect(this, "save()");

        saveAsAct = new QAction(tr("Save &As..."), this);
        saveAsAct.setStatusTip(tr("Save the document under a new name"));
        saveAsAct.triggered.connect(this, "saveAs()");

        exitAct = new QAction(tr("E&xit"), this);
        exitAct.setShortcut(tr("Ctrl+Q"));
        exitAct.setStatusTip(tr("Exit the application"));
        exitAct.triggered.connect(this, "close()");

        cutAct = new QAction(new QIcon(rsrcPath + "/cut.png"), tr("Cu&t"), this);
        cutAct.setShortcut(tr("Ctrl+X"));
        cutAct.setStatusTip(tr("Cut the current selection's contents to the clipboard"));
        cutAct.triggered.connect(textEdit, "cut()");

        copyAct = new QAction(new QIcon(rsrcPath + "/copy.png"), tr("&Copy"), this);
        copyAct.setShortcut(tr("Ctrl+C"));
        copyAct.setStatusTip(tr("Copy the current selection's contents to the clipboard"));
        copyAct.triggered.connect(textEdit, "copy()");

        pasteAct = new QAction(new QIcon(rsrcPath + "/paste.png"), tr("&Paste"), this);
        pasteAct.setShortcut(tr("Ctrl+V"));
        pasteAct.setStatusTip(tr("Paste the clipboard's contents into the current selection"));
        pasteAct.triggered.connect(textEdit, "paste()");

        aboutAct = new QAction(tr("&About"), this);
        aboutAct.setStatusTip(tr("Show the application's About box"));
        aboutAct.triggered.connect(this, "about()");

        aboutQtAct = new QAction(tr("About &Qt"), this);
        aboutQtAct.setStatusTip(tr("Show the Qt library's About box"));
        aboutQtAct.triggered.connect(QApplication.instance(), "aboutQt()");

        cutAct.setEnabled(false);
        copyAct.setEnabled(false);
        textEdit.copyAvailable.connect(cutAct, "setEnabled(boolean)");
        textEdit.copyAvailable.connect(copyAct, "setEnabled(boolean)");
    }

    private void createMenus()
    {
        fileMenu = menuBar().addMenu(tr("&File"));
        fileMenu.addAction(newAct);
        fileMenu.addAction(openAct);
        fileMenu.addAction(saveAct);
        fileMenu.addAction(saveAsAct);
        fileMenu.addSeparator();
        fileMenu.addAction(exitAct);

        editMenu = menuBar().addMenu(tr("&Edit"));
        editMenu.addAction(cutAct);
        editMenu.addAction(copyAct);
        editMenu.addAction(pasteAct);

        menuBar().addSeparator();

        helpMenu = menuBar().addMenu(tr("&Help"));
        helpMenu.addAction(aboutAct);
        helpMenu.addAction(aboutQtAct);
    }

    private void createToolBars()
    {
        fileToolBar = addToolBar(tr("File"));
        fileToolBar.addAction(newAct);
        fileToolBar.addAction(openAct);
        fileToolBar.addAction(saveAct);

        editToolBar = addToolBar(tr("Edit"));
        editToolBar.addAction(cutAct);
        editToolBar.addAction(copyAct);
        editToolBar.addAction(pasteAct);
    }

    private void createStatusBar()
    {
        statusBar().showMessage(tr("Ready"));
    }

    private void readSettings()
    {
//        QSettings settings("Trolltech", "Application Example");
//        QPoint pos = settings.value("pos", QPoint(200, 200)).toPoint();
//        QSize size = settings.value("size", QSize(400, 400)).toSize();
//        resize(size);
//        move(pos);
    }

    private void writeSettings()
    {
//        QSettings settings("Trolltech", "Application Example");
//        settings.setValue("pos", pos());
//        settings.setValue("size", size());
    }

    private boolean maybeSave()
    {
        if (textEdit.document().isModified()) {
            int ret = QMessageBox.warning(this, tr("Application"),
                                           tr("The document has been modified.\n" +
                                              "Save your changes?"),
                                           QMessageBox.Ok | QMessageBox.Cancel);
            if (ret == QMessageBox.Yes) {
                return save();
            } else if (ret == QMessageBox.Cancel) {
                return false;
            }
        }
        return true;
    }

    public void loadFile(String fileName)
    {
        QFile file = new QFile(fileName);
        if (!file.open(QFile.ReadOnly | QFile.Text)) {
            QMessageBox.warning(this, tr("Application"), String.format(tr("Cannot read file %1$s:\n%2$s."), fileName, file.errorString()));
            return;
        }

        QTextStream in = new QTextStream(file);
        QApplication.setOverrideCursor(new QCursor(Qt.WaitCursor));
        textEdit.setPlainText(in.readAll());
        QApplication.restoreOverrideCursor();

        setCurrentFile(fileName);
        statusBar().showMessage(tr("File loaded"), 2000);
    }

    public boolean saveFile(String fileName)
    {
        QFile file = new QFile(fileName);
        if (!file.open(QFile.WriteOnly | QFile.Text)) {
            QMessageBox.warning(this, tr("Application"), String.format(tr("Cannot write file %1$s:\n%2$s."), fileName, file.errorString()));
            return false;
        }

        QTextStream out = new QTextStream(file);
        QApplication.setOverrideCursor(new QCursor(Qt.WaitCursor));
        out.operator_shift_left(textEdit.toPlainText());
        QApplication.restoreOverrideCursor();

        setCurrentFile(fileName);
        statusBar().showMessage(tr("File saved"), 2000);
        file.close();
        return true;
    }

    public void setCurrentFile(String fileName)
    {
        curFile = fileName;
        textEdit.document().setModified(false);
        setWindowModified(false);

        String shownName;
        if (curFile.length() == 0)
            shownName = "untitled.txt";
        else
            shownName = strippedName(curFile);

        setWindowTitle(String.format(tr("%1$s[*] - %2$s"), shownName, tr("Application")));
    }

    private static String strippedName(String fullFileName)
    {
        return new QFileInfo(fullFileName).fileName();
    }

    public static void main(String[] args) {
        QApplication.initialize(args);

        Application application = new Application();
        application.show();

        QApplication.exec();
    }

}
