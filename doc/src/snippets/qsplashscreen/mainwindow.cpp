#include <QtGui>

#include "mainwindow.h"

MainWindow::MainWindow()
{
    QLabel *label = new QLabel(tr("This is the main window."));
    label->setAlignment(Qt::AlignCenter);
    setCentralWidget(label);
}
