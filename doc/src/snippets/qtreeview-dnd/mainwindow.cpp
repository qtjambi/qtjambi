#include <QtGui>

#include "mainwindow.h"
#include "dragdropmodel.h"

MainWindow::MainWindow()
{
    QMenu *fileMenu = new QMenu(tr("&File"));

    QAction *quitAction = fileMenu->addAction(tr("E&xit"));
    quitAction->setShortcut(tr("Ctrl+Q"));

    menuBar()->addMenu(fileMenu);

//  For convenient quoting:
    QTreeView *treeView = new QTreeView(this);
    treeView->setSelectionMode(QAbstractItemView::ExtendedSelection);
    treeView->setDragEnabled(true);
    treeView->setAcceptDrops(true);
    treeView->setDropIndicatorShown(true);

    this->treeView = treeView;

    connect(quitAction, SIGNAL(triggered()), this, SLOT(close()));

    setupItems();

    setCentralWidget(treeView);
    setWindowTitle(tr("Tree View"));
}

void MainWindow::setupItems()
{
    QStringList items;
    items << tr("Widgets\tUser interface objects used to create GUI applications.")
          << tr("  QWidget\tThe basic building block for all other widgets.")
          << tr("  QDialog\tThe base class for dialog windows.")
          << tr("Tools\tUtilities and applications for Qt developers.")
          << tr("  Qt Designer\tA GUI form designer for Qt applications.")
          << tr("  Qt Assistant\tA documentation browser for Qt documentation.");

    DragDropModel *model = new DragDropModel(items, this);
    QModelIndex index = model->index(0, 0, QModelIndex());
    model->insertRows(2, 3, index);
    index = model->index(0, 0, QModelIndex());
    index = model->index(2, 0, index);
    model->setData(index, QVariant("QFrame"));
    model->removeRows(3, 2, index.parent());
    treeView->setModel(model);
}
