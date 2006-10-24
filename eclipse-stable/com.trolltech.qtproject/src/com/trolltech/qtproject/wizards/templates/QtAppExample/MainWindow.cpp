#include "MainWindow.h"
#include <QDebug>

MainWindow::MainWindow()
{
    ui.setupUi(this);
    connect(ui.toolBox, SIGNAL(currentChanged(int)),this,SLOT(exampleChanged(int)));

    connect(ui.actionShow_Example_1, SIGNAL(triggered()), this, SLOT(showExample1()));
    connect(ui.actionShow_Example_2, SIGNAL(triggered()), this, SLOT(showExample2()));
    connect(ui.actionShow_Example_3, SIGNAL(triggered()), this, SLOT(showExample3()));

    connect(ui.lineEdit, SIGNAL(selectionChanged()), this, SLOT(updateSelection()));

    exampleChanged(ui.toolBox->currentIndex());
}

MainWindow::~MainWindow()
{
}

void MainWindow::exampleChanged(int index){
    qDebug() << "MainWindow::exampleChanged(int index)";
    switch(index){
        case 0:
            ui.textBrowser->setSource(QUrl("qrc:/Example1.htm"));
            break;
        case 1:
            ui.textBrowser->setSource(QUrl("qrc:/Example2.htm"));
            break;
        case 2:
            ui.textBrowser->setSource(QUrl("qrc:/Example3.htm"));
            break;
    }
}

void MainWindow::updateSelection(){
    ui.label->setText(ui.lineEdit->selectedText());
}
