#include <QtGui>

#include "mainwindow.h"
#include "model.h"

MainWindow::MainWindow()
{
    QMenu *fileMenu = new QMenu(tr("&File"));

    QAction *quitAction = fileMenu->addAction(tr("E&xit"));
    quitAction->setShortcut(tr("Ctrl+Q"));

    QMenu *itemsMenu = new QMenu(tr("&Items"));

    insertAction = itemsMenu->addAction(tr("&Insert Item"));
    removeAction = itemsMenu->addAction(tr("&Remove Item"));
    QAction *ascendingAction = itemsMenu->addAction(tr("Sort in &Ascending Order"));
    QAction *descendingAction = itemsMenu->addAction(tr("Sort in &Descending Order"));

    menuBar()->addMenu(fileMenu);
    menuBar()->addMenu(itemsMenu);

    QStringList strings;
    strings << tr("Oak") << tr("Fir") << tr("Pine") << tr("Birch")
            << tr("Hazel") << tr("Redwood") << tr("Sycamore") << tr("Chestnut");
    model = new StringListModel(strings, this);
/*  For convenient quoting:
    QListView *listView = new QListView(this);
*/
    listView = new QListView(this);
    listView->setModel(model);
    listView->setSelectionMode(QAbstractItemView::SingleSelection);

    connect(quitAction, SIGNAL(triggered()), this, SLOT(close()));
    connect(ascendingAction, SIGNAL(triggered()), this, SLOT(sortAscending()));
    connect(descendingAction, SIGNAL(triggered()), this, SLOT(sortDescending()));
    connect(insertAction, SIGNAL(triggered()), this, SLOT(insertItem()));
    connect(removeAction, SIGNAL(triggered()), this, SLOT(removeItem()));
    connect(listView->selectionModel(),
            SIGNAL(currentChanged(const QModelIndex &, const QModelIndex &)),
            this, SLOT(updateMenus(const QModelIndex &)));

    updateMenus(listView->selectionModel()->currentIndex());

    setCentralWidget(listView);
    setWindowTitle(tr("List View"));
}

void MainWindow::sortAscending()
{
    model->sort(0, Qt::AscendingOrder);
}

void MainWindow::sortDescending()
{
    model->sort(0, Qt::DescendingOrder);
}

void MainWindow::insertItem()
{
    QModelIndex currentIndex = listView->currentIndex();
    if (!currentIndex.isValid())
        return;

    QString itemText = QInputDialog::getText(this, tr("Insert Item"),
        tr("Input text for the new item:"));

    if (itemText.isNull())
        return;

    if (model->insertRow(currentIndex.row(), QModelIndex())) {
        QModelIndex newIndex = model->index(currentIndex.row(), 0, QModelIndex());
        model->setData(newIndex, itemText, Qt::EditRole);

        QString toolTipText = tr("Tooltip:") + itemText;
        QString statusTipText = tr("Status tip:") + itemText;
        QString whatsThisText = tr("What's This?:") + itemText;
        model->setData(newIndex, toolTipText, Qt::ToolTipRole);
        model->setData(newIndex, toolTipText, Qt::StatusTipRole);
        model->setData(newIndex, whatsThisText, Qt::WhatsThisRole);
    }
}

void MainWindow::removeItem()
{
    QModelIndex currentIndex = listView->currentIndex();
    if (!currentIndex.isValid())
        return;

    model->removeRow(currentIndex.row(), QModelIndex());
}

void MainWindow::updateMenus(const QModelIndex &current)
{
    insertAction->setEnabled(current.isValid());
    removeAction->setEnabled(current.isValid());
}
