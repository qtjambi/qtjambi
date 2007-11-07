#include <QtGui>

class MyWidget : public QWidget
{
public:
    MyWidget();

protected:
    void paintEvent(QPaintEvent *);
};

MyWidget::MyWidget()
{
    QPalette palette(MyWidget::palette());
    palette.setColor(backgroundRole(), Qt::white);
    setPalette(palette);
}

void MyWidget::paintEvent(QPaintEvent *)
{
    QPainter painter(this);
    painter.setRenderHint(QPainter::Antialiasing);
    painter.setPen(Qt::darkGreen);
    painter.drawRect(1, 2, 6, 4);

    //painter.setPen(Qt::darkGray);
    //painter.drawLine(2, 8, 6, 2);
}

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    MyWidget widget;
    widget.show();
    return app.exec();
}
