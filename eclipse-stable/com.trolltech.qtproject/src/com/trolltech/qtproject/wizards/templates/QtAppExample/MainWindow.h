#ifndef MAIN_WINDOW_h
#define MAIN_WINDOW_h

#include "ui_MainWindow.h"

#include <QMainWindow>

class MainWindow : public QMainWindow
{
Q_OBJECT

public:
    MainWindow();
    virtual ~MainWindow();

public slots:
    void exampleChanged(int index);

    void showExample1(){exampleChanged(1);}
    void showExample2(){exampleChanged(2);}
    void showExample3(){exampleChanged(3);}

    void updateSelection();

private:
    Ui_MainWindow ui;

};

#endif
