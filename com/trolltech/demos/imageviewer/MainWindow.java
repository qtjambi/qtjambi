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

package com.trolltech.demos.imageviewer;

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.gui.QAbstractItemView.ScrollHint;

public class MainWindow extends QMainWindow {

    public MainWindow() {
        ui.setupUi(this);

        setupTableView();
        setupView();

        readSettings();
    }

    public void on_dirView_activated(QModelIndex index) {
        QDir dir = new QDir(dirModel.fileInfo(index).absoluteFilePath());
        imageModel.setDirectory(dir);
        ui.tableDock.setWindowTitle("Images in: " + dir.absolutePath());

        statusBar().showMessage("Displaying a list of images in " + dir.absolutePath() + "'");
    }

    public void on_tableView_activated(QModelIndex index) {
        view.setImage(imageModel.imageAt(index.row()));
        statusBar().showMessage("Displaying image");
    }

    public void on_resetColorBalance_clicked() {
        ui.redCyanBalance.setValue(0);
        ui.greenMagentaBalance.setValue(0);
        ui.blueYellowBalance.setValue(0);
        ui.colorBalance.setValue(0);
        ui.inverted.setChecked(false);
    }

    public void on_actionSave_triggered() {
        if (view.modifiedImage() == null) {
            statusBar().showMessage("No image to save");
            return;
        }

        String fileName = QFileDialog.getSaveFileName(this, "File to save", "*.png");
        if (fileName.length() == 0 || !fileName.toLowerCase().endsWith("png")) {
            statusBar().showMessage("Not saving image");
            return;
        }

        view.modifiedImage().save(fileName, "PNG");

        statusBar().showMessage("Image saved as '" + fileName + "'");
    }

    public void on_actionAbout_Qt_triggered() {
        QApplication.aboutQt();
    }

    public void on_actionAbout_Image_Viewer_triggered() {
        QDialog d = new QDialog(this);
        Ui_AboutImageViewer ui = new Ui_AboutImageViewer();
        ui.setupUi(d);
        ui.label.setPixmap(new QPixmap("classpath:com/trolltech/images/qt-logo.png"));

        QPalette pal = ui.textEdit.palette();
        pal.setBrush(QPalette.ColorRole.Base, d.palette().window());
        ui.textEdit.setPalette(pal);

        d.exec();
        d.dispose(); // No strictly needed, but it frees up memory faster.
    }

    public void on_actionAbout_Qt_Jambi_triggered() {
        QApplication.aboutQtJambi();
    }

    public void on_actionExit_triggered() {
        close();
    }

    public void on_actionClose_triggered() {
        view.setImage(null);
    }

    protected void showEvent(QShowEvent e) {
        if (dirModel == null)
            QTimer.singleShot(100, this, "setupDirModel()");
    }

    private void setupTableView() {
        imageModel = new ImageTableModel(this);
        ui.tableView.setModel(imageModel);
        ui.tableView.setIconSize(LazyPixmap.SMALL_SIZE);

        ui.tableView.clicked.connect(this, "on_resetColorBalance_clicked()");
    }

    public void setupDirModel() {
        if (dirModel != null)
            return;
        dirModel = new QDirModel(this);
        dirModel.setLazyChildCount(true);
        dirModel.setFilter(new QDir.Filters(QDir.Filter.Dirs, QDir.Filter.Drives, QDir.Filter.NoDotAndDotDot));

        ui.dirView.setModel(dirModel);

        for (int i=1; i<ui.dirView.header().count(); ++i)
            ui.dirView.hideColumn(i);
        ui.dirView.header().hide();

        ui.dirView.header().setStretchLastSection(false);
        ui.dirView.header().setResizeMode(QHeaderView.ResizeMode.ResizeToContents);

        QFileInfo info = new QFileInfo("com/trolltech/images");
        QModelIndex initial = dirModel.index(info.absoluteFilePath());
        if (initial != null) {
            ui.dirView.setCurrentIndex(initial);
            ui.dirView.activated.emit(initial);
        }

        ui.dirView.scrollTo(ui.dirView.currentIndex(),ScrollHint.PositionAtCenter);
    }

    private void setupView() {
        view = new View(this);
        setCentralWidget(view);

        ui.redCyanBalance.valueChanged.connect(view, "setRedCyan(int)");
        ui.greenMagentaBalance.valueChanged.connect(view, "setGreenMagenta(int)");
        ui.blueYellowBalance.valueChanged.connect(view, "setBlueYellow(int)");
        ui.colorBalance.valueChanged.connect(view, "setColorBalance(int)");
        ui.inverted.toggled.connect(view, "setInvert(boolean)");

        view.valid.connect(ui.actionClose, "setEnabled(boolean)");
        view.valid.connect(ui.actionSave, "setEnabled(boolean)");
        view.valid.connect(ui.groupBox, "setEnabled(boolean)");
    }

    public void closeEvent(QCloseEvent event) {
        writeSettings();
    }

    public void readSettings(){
        QSettings settings = new QSettings("Trolltech", "ImageViewer Example");
        resize((QSize)settings.value("size", new QSize(1000, 500)));
        QPoint point = (QPoint)settings.value("pos", null);
        if(point!=null)
            move(point);
        restoreState((QByteArray)settings.value("state", null));
        settings.sync();
        settings.dispose();
    }

    public void writeSettings(){
        QSettings settings = new QSettings("Trolltech", "ImageViewer Example");
        settings.setValue("pos", pos());
        settings.setValue("size", size());
        settings.setValue("state", saveState());
        settings.sync();
        settings.dispose();
    }

    private Ui_MainWindow ui = new Ui_MainWindow();
    private QDirModel dirModel;
    private ImageTableModel imageModel;
    private View view;
}
