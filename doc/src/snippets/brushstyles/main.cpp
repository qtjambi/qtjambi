#include <QApplication>

#include "stylewidget.h"

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    StyleWidget widget;
    widget.show();
    return app.exec();
}
