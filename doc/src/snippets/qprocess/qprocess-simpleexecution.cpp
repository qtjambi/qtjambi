#include <QApplication>
#include <QtGui>

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
//! [0]
    QObject *parent;
//! [0]
    parent = &app;

//! [1]
    QString program = "./path/to/Qt/examples/widgets/analogclock";
//! [1]
    program = "./../../../../examples/widgets/analogclock/analogclock";

//! [2]
    QStringList arguments;
    arguments << "-style" << "motif";

    QProcess *myProcess = new QProcess(parent);
    myProcess->start(program, arguments);
//! [2]

    return app.exec();
}
