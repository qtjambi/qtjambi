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

public class MainWindow extends QMainWindow {

    public MainWindow() {
        ui.setupUi(this);

        patchPixmaps();
        setupTableView();
        setupView();
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
        imageModel = new ImageTableModel();
        ui.tableView.setModel(imageModel);
        ui.tableView.setIconSize(LazyPixmap.SMALL_SIZE);

        ui.tableView.clicked.connect(this, "on_resetColorBalance_clicked()");
    }

    public void setupDirModel() {
        if (dirModel != null)
            return;
        dirModel = new QDirModel();
        dirModel.setLazyChildCount(true);
        dirModel.setFilter(new QDir.Filters(QDir.Filter.Dirs, QDir.Filter.Drives, QDir.Filter.NoDotAndDotDot));
        ui.dirView.setModel(dirModel);
        for (int i=1; i<ui.dirView.header().count(); ++i)
            ui.dirView.header().hideSection(i);
        ui.dirView.header().hide();

        QFileInfo info = new QFileInfo("com/trolltech/images");
        QModelIndex initial = dirModel.index(info.absoluteFilePath());
        if (initial != null && initial.isValid()) {
            ui.dirView.setCurrentIndex(initial);
            ui.dirView.activated.emit(initial);
        }
    }

    private void setupView() {
        view = new View(this);
        setCentralWidget(view);

        ui.redCyanBalance.valueChanged.connect(view, "setRedCyan(int)");
        ui.greenMagentaBalance.valueChanged.connect(view, "setGreenMagenta(int)");
        ui.blueYellowBalance.valueChanged.connect(view, "setBlueYellow(int)");
        ui.colorBalance.valueChanged.connect(view, "setColorBalance(int)");
        ui.actionZoom_In.triggered.connect(view, "increaseZoom()");
        ui.actionZoom_Out.triggered.connect(view, "decreaseZoom()");

        QtJambiUtils.connect(view.valid, "setEnabled(boolean)",
                            ui.actionClose,
                            ui.actionSave,
                            ui.actionZoom_In,
                            ui.actionZoom_Out,
                            ui.groupBox);
    }

    // We need to manually locate the pixmaps until Juic generates the right
    // code for this
    private void patchPixmaps() {
        String trolltechPrefix = "classpath:com/trolltech/";
        String prefix = trolltechPrefix + "demos/imageviewer/";

        QPixmap pixmapBlack = new QPixmap(prefix + "circle_black_16.png");
        QPixmap pixmapWhite= new QPixmap(prefix + "circle_white_16.png");
        QPixmap pixmapRed = new QPixmap(prefix + "circle_red_16.png");
        QPixmap pixmapBlue= new QPixmap(prefix + "circle_blue_16.png");
        QPixmap pixmapGreen= new QPixmap(prefix + "circle_green_16.png");
        QPixmap pixmapCyan = new QPixmap(prefix + "circle_cyan_16.png");
        QPixmap pixmapYellow = new QPixmap(prefix + "circle_yellow_16.png");
        QPixmap pixmapMagenta = new QPixmap(prefix + "circle_magenta_16.png");
        QPixmap pixmapZoomIn = new QPixmap(prefix + "zoomin.png");
        QPixmap pixmapZoomOut = new QPixmap(prefix + "zoomout.png");
        QPixmap pixmapSave = new QPixmap(trolltechPrefix + "images/save.png");
        QPixmap pixmapClose = new QPixmap(trolltechPrefix + "images/close.png");

        ui.actionZoom_In.setIcon(pixmapZoomIn);
        ui.actionZoom_Out.setIcon(pixmapZoomOut);
        ui.actionSave.setIcon(pixmapSave);
        ui.actionClose.setIcon(pixmapClose);

        ui.labelBlack.setPixmap(pixmapBlack);
        ui.labelWhite.setPixmap(pixmapWhite);
        ui.labelRed.setPixmap(pixmapRed);
        ui.labelBlue.setPixmap(pixmapBlue);
        ui.labelGreen.setPixmap(pixmapGreen);
        ui.labelCyan.setPixmap(pixmapCyan);
        ui.labelYellow.setPixmap(pixmapYellow);
        ui.labelMagenta.setPixmap(pixmapMagenta);
    }

    private Ui_MainWindow ui = new Ui_MainWindow();
    private QDirModel dirModel;
    private ImageTableModel imageModel;
    private View view;
}
