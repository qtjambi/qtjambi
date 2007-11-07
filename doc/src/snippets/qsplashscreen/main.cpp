#include <QtGui>
#include <stdlib.h>

#include "mainwindow.h"

//! [0]
int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    QPixmap pixmap(":/splash.png");
    QSplashScreen splash(pixmap);
    splash.show();
    app.processEvents();
//! [0]

    sleep(5);
//! [1]
    QMainWindow window;
    window.show();
    splash.finish(&window);
    return app.exec();
}
//! [1]
