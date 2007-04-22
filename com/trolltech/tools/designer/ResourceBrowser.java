package com.trolltech.tools.designer;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.*;

import java.util.*;

public class ResourceBrowser extends JambiResourceBrowser {

    private static final boolean UNFILTERED = Utilities.matchProperty("unfiltered");

    protected void disposed() {
        if (walker != null) {
            walker.kill();
            try {
                walker.thread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
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
            view.doubleClicked.connect(((QDialog) window()), "accept()");
        }

        view.expandAll();
        
        selection.selectionChanged.connect(this, "selectionChanged()");

        setContextMenuPolicy(Qt.ContextMenuPolicy.ActionsContextMenu);
        addAction("Reset resourcelist", "reindex()");
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

    protected void showEvent(QShowEvent arg) {
        if (!shown)
            reindex();
        shown = true;
        filterEdit.setFocus();
    }

    @Override
    public void updateRootDirs(String paths) {
        String rootArray[] = paths.split(System.getProperty("path.separator"));
        
        
        List<String> roots = ClassPathWalker.roots();
        if (roots != null) {
            for (String root : roots) 
                QtJambiUtils.removeSearchPathForResourceEngine(root);
        }
                
        roots = Arrays.asList(rootArray);
        for (String root : roots)
            QtJambiUtils.addSearchPathForResourceEngine(root);
        
        ClassPathWalker.setRoots(roots);
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

    @SuppressWarnings("unused")
    private void selectionChanged() {
        QModelIndex index = selection.currentIndex();
        if (!UNFILTERED)
            index = filterModel.mapToSource(index);
        setPreviewForIndex(index);
        currentPath = null;
    }

    @SuppressWarnings("unused")
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

    private void setupSearchConnections() {
        walker.beginSearching.connect(hourGlass, "start()");
        walker.doneSearching.connect(hourGlass, "stop()");
        walker.resourceFound.connect(hourGlass, "start()");

        browserModel.rowsAdded.connect(view, "expandAll()");
        browserModel.rowsAdded.connect(this, "reselectCurrent()");
    }

    private void reindex() {
        filterModel.setSourceModel(null);
        browserModel = null;
        if (walker != null) {
            walker.kill();
            walker = null;
        }
        searchClassPath();
        filterModel.setSourceModel(browserModel);
        setupSearchConnections();
    }

    @SuppressWarnings("unused")
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
            			QtJambiUtils.removeSearchPathForResourceEngine(root);
            	}
            }
            
            // Add new roots to ClassPathFileEngine
            {
            	for (String path : newPaths) {
            		if (!oldRoots.contains(path))
            			QtJambiUtils.addSearchPathForResourceEngine(path);
            	}
            }
            
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
    private boolean shown;
}
