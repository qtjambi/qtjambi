#include <QApplication>

#include "clipwindow.h"

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    ClipWindow *window = new ClipWindow;
    window->resize(640, 480);
    window->show();
    return app.exec();
}
