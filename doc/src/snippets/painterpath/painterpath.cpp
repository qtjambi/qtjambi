#include <QtGui>



int main(int argc, char **argv)
{
    QApplication app(argc, argv);

    QImage image(100, 100, QImage::Format_RGB32);

    QPainterPath path;
    path.addRect(20, 20, 60, 60);

    path.moveTo(0, 0);
    path.cubicTo(99, 0,  50, 50,  99, 99);
    path.cubicTo(0, 99,  50, 50,  0, 0);

    QPainter painter(&image);
    painter.fillRect(0, 0, 100, 100, Qt::white);

    painter.save();
    painter.translate(0.5, 0.5);
    painter.setPen(QPen(QColor(79, 106, 25), 1, Qt::SolidLine, Qt::FlatCap, Qt::MiterJoin));
    painter.setBrush(QColor(122, 163, 39));
    painter.setRenderHint(QPainter::Antialiasing);

    painter.drawPath(path);

    painter.restore();
    painter.end();

    QLabel lab;
    lab.setPixmap(QPixmap::fromImage(image));
    lab.show();
    return app.exec();
}
