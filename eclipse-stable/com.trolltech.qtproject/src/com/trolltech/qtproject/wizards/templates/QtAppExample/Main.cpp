#include <QDebug>
#include <QApplication>

#include "MainWindow.h"

int main(int argc, char **argv)
{
    QApplication app(argc, argv);
   
    MainWindow *mainw = new MainWindow();
    mainw->show();
    
    return app.exec();
}