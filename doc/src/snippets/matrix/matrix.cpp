#include <QtGui>
#include <cmath>

class SimpleTransformation : public QWidget
{
    void paintEvent(QPaintEvent *);
};

//! [0]
void SimpleTransformation::paintEvent(QPaintEvent *)
{
    QPainter painter(this);
    painter.setPen(QPen(Qt::blue, 1, Qt::DashLine));
    painter.drawRect(0, 0, 100, 100);

    painter.rotate(45);

    painter.setFont(QFont("Helvetica", 24));
    painter.setPen(QPen(Qt::black, 1));
    painter.drawText(20, 10, "QMatrix");
}
//! [0]

class CombinedTransformation : public QWidget
{
    void paintEvent(QPaintEvent *);
};

//! [1]
void CombinedTransformation::paintEvent(QPaintEvent *)
{
    QPainter painter(this);
    painter.setPen(QPen(Qt::blue, 1, Qt::DashLine));
    painter.drawRect(0, 0, 100, 100);

    QMatrix matrix;
    matrix.translate(50, 50);
    matrix.rotate(45);
    matrix.scale(0.5, 1.0);
    painter.setMatrix(matrix);

    painter.setFont(QFont("Helvetica", 24));
    painter.setPen(QPen(Qt::black, 1));
    painter.drawText(20, 10, "QMatrix");
}
//! [1]

class BasicOperations : public QWidget
{
    void paintEvent(QPaintEvent *);
};

//! [2]
void BasicOperations::paintEvent(QPaintEvent *)
{
    double pi = 3.14;

    double a    = pi/180 * 45.0;
    double sina = sin(a);
    double cosa = cos(a);

    QMatrix translationMatrix(1, 0, 0, 1, 50.0, 50.0);
    QMatrix rotationMatrix(cosa, sina, -sina, cosa, 0, 0);
    QMatrix scalingMatrix(0.5, 0, 0, 1.0, 0, 0);

    QMatrix matrix;
    matrix =  scalingMatrix * rotationMatrix * translationMatrix;

    QPainter painter(this);
    painter.setPen(QPen(Qt::blue, 1, Qt::DashLine));
    painter.drawRect(0, 0, 100, 100);

    painter.setMatrix(matrix);

    painter.setFont(QFont("Helvetica", 24));
    painter.setPen(QPen(Qt::black, 1));
    painter.drawText(20, 10, "QMatrix");
}
//! [2]

int main(int argc, char **argv)
{
    QApplication app(argc, argv);

    QWidget widget;

    SimpleTransformation *simpleWidget = new SimpleTransformation;
    CombinedTransformation *combinedWidget = new CombinedTransformation;
    BasicOperations *basicWidget = new BasicOperations;

    QVBoxLayout *layout = new QVBoxLayout;
    layout->addWidget(simpleWidget);
    layout->addWidget(combinedWidget);
    layout->addWidget(basicWidget);
    widget.setLayout(layout);

    widget.show();
    widget.resize(130, 350);
    return app.exec();
}
