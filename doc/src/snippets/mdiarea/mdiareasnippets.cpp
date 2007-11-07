#include <QtGui>

int main(int argv, char **args)
{
    QApplication app(argv, args);

    QMdiArea *area = new QMdiArea;

    QMdiSubWindow *myWindow = new QMdiSubWindow(area);
    myWindow->setAttribute(Qt::WA_DeleteOnClose);
    myWindow->setWidget(new QPushButton("My button!"));
    myWindow->show();

    QMdiSubWindow *yourWindow = new QMdiSubWindow(area);
    yourWindow->setWidget(new QPushButton("Your Button"));
    yourWindow->show();

    area->show();

    return app.exec();
}
