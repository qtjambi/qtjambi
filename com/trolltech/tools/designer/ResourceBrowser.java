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

package com.trolltech.tools.designer;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.*;

import java.util.*;

public class ResourceBrowser extends JambiResourceBrowser {

    private static final boolean UNFILTERED = Utilities.matchProperty("unfiltered");

    public ResourceBrowser(QWidget parent) {
        super(parent);

        try {

            QAbstractItemModel model;

            if (UNFILTERED) {
                model = browserModel;
            } else {
                filterModel = new ResourceBrowserModel.FilterModel(this);
                filterModel.setSourceModel(browserModel);
                model = filterModel;
            }

            selection = new QItemSelectionModel(model);

            view = new QTreeView(this);
            view.header().hide();
            view.setModel(model);
            view.setSelectionModel(selection);
            view.setRootIsDecorated(false);

            filterEdit = new QLineEdit(this);

            pathText = new QLabel();
            pathText.setMaximumSize(250, pathText.maximumHeight());
            pathText.setTextInteractionFlags(Qt.TextInteractionFlag.TextSelectableByMouse);

            sizeText = new QLabel();
            sizeText.setAlignment(Qt.AlignmentFlag.AlignLeft);
            preview = new QLabel();

            preview.setFixedSize(new QSize(64, 64));
        preview.setAlignment(Qt.AlignmentFlag.AlignHCenter, Qt.AlignmentFlag.AlignVCenter);

            hourGlass = new HourGlass(this);

            QGridLayout layout = new QGridLayout(this);
            QHBoxLayout hbox = new QHBoxLayout();

            hbox.addWidget(new QLabel(tr("Filter:"), this));
            hbox.addWidget(filterEdit);
            hbox.addWidget(hourGlass);

            layout.addItem(hbox, 0, 0, 1, 2);
            layout.addWidget(view, 1, 0, 1, 2);

            layout.addWidget(new QLabel(tr("Size:")), 2, 0);
            layout.addWidget(sizeText, 2, 1);

            layout.addWidget(new QLabel(tr("Path:")), 3, 0);
            layout.addWidget(pathText, 3, 1);

            layout.addWidget(preview, 4, 0, 1, 2);

            layout.setMargin(0);
            hbox.setSpacing(6);
            hbox.setMargin(0);

            QWidget.setTabOrder(filterEdit, view);

        QSizePolicy policy = new QSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Preferred);
            policy.setHorizontalStretch((byte) 1);
            pathText.setSizePolicy(policy);
            sizeText.setSizePolicy(policy);

            if (!UNFILTERED) {
            filterEdit.textChanged.connect(filterModel, "setFilterRegExp(String)");
                filterEdit.textChanged.connect(view, "expandAll()");
                filterEdit.textChanged.connect(this, "checkOnlyOne()");
            }

        if (window() instanceof QDialog) {
            view.doubleClicked.connect((window()), "accept()");
        }

            view.expandAll();

        selection.currentChanged.connect(this, "selectionChanged(QModelIndex, QModelIndex)");

            setContextMenuPolicy(Qt.ContextMenuPolicy.ActionsContextMenu);
            addAction("Refresh", "reindex()");
            addAction("Edit searchpath", "changeSearchPath()");
        } catch (Exception e) { e.printStackTrace(); }
        }

    @Override
    public String currentPath() {
        return path;
    }

    @Override
    public void setCurrentPath(String filePath) {
        currentPath = filePath;
        reselectCurrent();
    }

    private void reselectCurrent() {
        if (currentPath == null)
            return;
        QModelIndex index = null;
        if (browserModel != null)
            index = browserModel.indexForPath(currentPath);
        if (filterModel != null && index != null && !UNFILTERED)
            index = filterModel.mapFromSource(index);
        if (selection != null)
            selection.setCurrentIndex(index, QItemSelectionModel.SelectionFlag.SelectCurrent);
        }

    @Override
    protected void showEvent(QShowEvent arg) {
        if (walker == null)
            reindex();

        filterEdit.setFocus();
    }

    @Override
    public void updateRootDirs(String paths) {
        String rootArray[] = paths.split(System.getProperty("path.separator"));


        List<String> roots = ClassPathWalker.roots();
        if (roots != null) {
            for (String root : roots)
                QAbstractFileEngine.removeSearchPathForResourceEngine(root);
        }

        roots = new ArrayList<String>();
        Collections.addAll(roots, rootArray);
        for (String root : roots)
            QAbstractFileEngine.addSearchPathForResourceEngine(root);

        ClassPathWalker.setRoots(roots);
        ClassPathWalker.addRootsFromSettings();
        reindex();
    }

    /**
     * Sets the specified index in the browser model to the selected
     * element...
     */
    private void setPreviewForIndex(QModelIndex index) {
        path = browserModel.resource(index);
        if (path == null) {
            sizeText.setText("");
            pathText.setText("");
            preview.setPixmap(null);
            return;
        }

        QPixmap pixmap = new QPixmap(path);
        sizeText.setText(pixmap.width() + " x " + pixmap.height());
        if (pixmap.width() > 64 || pixmap.height() > 64)
            pixmap = pixmap.scaled(64, 64,
                    Qt.AspectRatioMode.KeepAspectRatio,
                    Qt.TransformationMode.SmoothTransformation);
        preview.setPixmap(pixmap);
        pathText.setText(path);

        currentPathChanged.emit(path);
    }

    private void selectionChanged(QModelIndex current, QModelIndex old) {
        QModelIndex index = current;
        if (index != null) {
            if (!UNFILTERED)
                index = filterModel.mapToSource(index);
            setPreviewForIndex(index);
            currentPath = null;
        }
    }

    private void checkOnlyOne() {
        if (UNFILTERED || filterModel.rowCount() != 1)
            return;

        QModelIndex i = filterModel.index(0, 0, null);

        if (filterModel.rowCount(i) == 1) {
            QModelIndex select = filterModel.index(0, 0, i);
            selection.setCurrentIndex(select,
                                      QItemSelectionModel.SelectionFlag.SelectCurrent);
        }
    }

    private void addAction(String text, String method) {
        QAction action = new QAction(tr(text), this);
        action.triggered.connect(this, method);
        addAction(action);
    }

    private void expand(QModelIndex parent) {
        if (UNFILTERED) {
            view.expand(parent);
        } else {
            QModelIndex filteredIndex = filterModel.mapFromSource(parent);
            this.view.expand(filteredIndex);
        }
    }

    private void setupSearchConnections() {
        walker.beginSearching.connect(hourGlass, "start()");
        walker.doneSearching.connect(hourGlass, "stop()");
        walker.resourceFound.connect(hourGlass, "start()");

        browserModel.rowsAdded.connect(this, "expand(QModelIndex)");
        // browserModel.rowsAdded.connect(this, "reselectCurrent()");
    }

    @Override
    protected void disposed() {
        if (browserModel != null)
            browserModel.dispose();
        if (walker != null) {
            walker.kill();
            walker.dispose();
        }
    }

    private void reindex() {
        filterModel.setSourceModel(null);
        if (browserModel != null) {
            browserModel.dispose();
            browserModel = null;
        }
        if (walker != null) {
            walker.kill();
            walker.dispose();
            walker = null;
        }
        searchClassPath();
        filterModel.setSourceModel(browserModel);
        setupSearchConnections();
    }

    private void updateSettings(List<String> newPaths, List<String> oldRoots) {
        String newExtraPath = "";
        QSettings settings = new QSettings("Trolltech", "Qt Jambi Resource Browser");
        Object oldExtraPath = settings.value("Extra paths");

        List<String> oldExtraPaths = new ArrayList<String>();
        if (oldExtraPath != null && oldExtraPath instanceof String) {
            Collections.addAll(oldExtraPaths, ((String) oldExtraPath).split(java.io.File.pathSeparator));

        }

        for (String newPath : newPaths) {
            if (!oldRoots.contains(newPath) || oldExtraPaths.contains(newPath)) {
                if (!newExtraPath.equals(""))
                    newExtraPath += java.io.File.pathSeparator;
                newExtraPath += newPath;
            }
        }

        settings.setValue("Extra paths", newExtraPath);
        settings.sync();
    }

    private void changeSearchPath() {
        SearchPathDialog pathDialog = new SearchPathDialog(this);
        pathDialog.setPaths(ClassPathWalker.roots());

        if (pathDialog.exec() == QDialog.DialogCode.Accepted.value()) {
            List<String> newPaths = pathDialog.paths();

            // Remove roots that are no longer wanted from ClassPathFileEngine
            List<String> oldRoots = ClassPathWalker.roots();
            {
                for (String root : oldRoots) {
                    if (!newPaths.contains(root))
                        QAbstractFileEngine.removeSearchPathForResourceEngine(root);
                }
            }

            // Add new roots to ClassPathFileEngine
            {
                for (String path : newPaths) {
                    if (!oldRoots.contains(path)) {
                        QAbstractFileEngine.addSearchPathForResourceEngine(path);
                    }
                }
            }

            updateSettings(newPaths, oldRoots);

            ClassPathWalker.setRoots(newPaths);
            reindex();
        }
    }

    private void searchClassPath() {
        if (walker != null)
            return;

        walker = new ClassPathWalker();
        walker.setPixmapSize(ResourceBrowserModel.PIXMAP_SIZE);

        browserModel = new ResourceBrowserModel();

        walker.resourceFound.connect(browserModel, "addResource(String, QImage)");

        walker.start();
    }



    public static void main(String args[]) {
        QApplication.initialize(args);

        ResourceBrowser rb = new ResourceBrowser(null);
        rb.show();

        rb.setCurrentPath("classpath:com/trolltech/images/copy.png");

        QApplication.exec();
    }


    private QItemSelectionModel selection;
    private QLabel preview;
    private QLabel pathText;
    private QLabel sizeText;
    private ClassPathWalker walker;
    private ResourceBrowserModel browserModel;
    private QSortFilterProxyModel filterModel;
    private QTreeView view;
    private String path;
    private String currentPath;
    private QLineEdit filterEdit;
    private HourGlass hourGlass;
    // private boolean shown;
}
