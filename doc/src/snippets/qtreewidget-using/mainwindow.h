#ifndef WINDOW_H
#define WINDOW_H

#include <QMainWindow>

class QAction;
class QTreeWidget;
class QTreeWidgetItem;

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow();

public slots:
    void findItems();
    void insertItem();
    void removeItem();
    void sortAscending();
    void sortDescending();
    void updateMenus(QTreeWidgetItem *current);
    void updateSortItems();

private:
    void setupTreeItems();

    QAction *ascendingAction;
    QAction *autoSortAction;
    QAction *descendingAction;
    QAction *insertAction;
    QAction *removeAction;
    QTreeWidget *treeWidget;
};

#endif
