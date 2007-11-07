#include <QtGui>
#include <QApplication>

class MainWindow : public QMainWindow
{
public:
    MainWindow(QWidget *parent = 0);
};

//! [0] //! [1]
MainWindow::MainWindow(QWidget *parent)
    : QMainWindow(parent)
{
//! [0]
    QWidget *myWidget = new QWidget;
    myWidget->setStatusTip(tr("This is my widget."));

    setCentralWidget(myWidget);
//! [1]

//! [2]
    QMenu *fileMenu = menuBar()->addMenu(tr("File"));

    QAction *newAct = new QAction(tr("&New"), this);
    newAct->setStatusTip(tr("Create a new file."));
    fileMenu->addAction(newAct);
//! [2]

    statusBar()->showMessage(tr("Ready"));
    setWindowTitle(tr("QStatusTipEvent"));
//! [3]
}
//! [3]

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    MainWindow window;
    window.show();
    return app.exec();
}

