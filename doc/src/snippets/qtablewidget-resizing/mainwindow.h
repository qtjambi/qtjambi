#ifndef WINDOW_H
#define WINDOW_H

#include <QMainWindow>

class QAction;
class QTableWidget;
class QTableWidgetItem;

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    MainWindow();

public slots:
    void changeHeight();
    void changeWidth();

private:
    void setupTableItems();

    QTableWidget *tableWidget;
};

#endif
