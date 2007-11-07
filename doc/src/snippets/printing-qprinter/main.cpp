#include <QtGui>
#include "object.h"

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    Object object;
    QTimer timer;
    timer.setSingleShot(true);
    timer.connect(&timer, SIGNAL(timeout()), &object, SLOT(print()));
    timer.start(0);
    return app.exec();
}
