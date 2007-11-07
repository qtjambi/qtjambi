#include <QtGui>

#include "window.h"

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);

    MyWidget window;
    window.show();
    return app.exec();
}
