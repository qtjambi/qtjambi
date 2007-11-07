#include <QtGui>

#include "mainwindow.h"

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    MainWindow *window = new MainWindow;
    window->resize(640, 480);
    window->show();
    return app.exec();
}
