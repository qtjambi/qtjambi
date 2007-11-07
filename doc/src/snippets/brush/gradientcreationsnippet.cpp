#include <QtGui>

int main(int argv, char **args)
{
    QApplication app(argv, args);

//! [0]
    QRadialGradient gradient(50, 50, 50, 50, 50);
    gradient.setColorAt(0, QColor::fromRgbF(0, 1, 0, 1));
    gradient.setColorAt(1, QColor::fromRgbF(0, 0, 0, 0));

    QBrush brush(gradient);
//! [0]

    QWidget widget;
    QPalette palette;
    palette.setBrush(widget.backgroundRole(), brush);
    widget.setPalette(palette);
    widget.show();

    return app.exec();
}
