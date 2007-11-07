#include <QtGui>
#include "paintwidget.h"

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    PaintWidget window;
    window.show();
    return app.exec();
}
