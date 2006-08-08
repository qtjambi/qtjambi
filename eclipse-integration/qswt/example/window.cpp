#include <QtGui>
#include <QMessageBox>

#include "window.h"
#include "qswt.h"

QtWindow::QtWindow(QWidget *parent) : QWidget(parent)
{
    QMenuBar *menuBar = new QMenuBar(this);

    QMenu* fileMenu = new QMenu(tr("&Message"), this);
    QAction *quitAction = fileMenu->addAction(tr("&Show"));
    menuBar->addMenu(fileMenu);

    connect(quitAction, SIGNAL(triggered()), this, SLOT(showMessageBox()));

    QGroupBox *horizontalGroup = new QGroupBox(tr("Horizontal layout"), this);

    QBoxLayout *horizontalLayout = new QHBoxLayout(horizontalGroup);
    for (int i = 1; i <= 4; ++i) {
	QPushButton *button = new QPushButton(horizontalGroup);
	button->setText(tr("Button %1").arg(i));
	horizontalLayout->addWidget(button, 10);
    }

    QGroupBox *gridGroup = new QGroupBox(tr("Grid layout"), this);
    QGridLayout *gridLayout = new QGridLayout(gridGroup);

    for (int row = 0; row < 3; ++row) {
	QLabel *label = new QLabel(tr("Line %1").arg(row+1), gridGroup);
	QLineEdit *lineEdit = new QLineEdit(gridGroup);
	gridLayout->addWidget(label, row, 0);
	gridLayout->addWidget(lineEdit, row, 1);	
    }

    QTextEdit *gridEditor = new QTextEdit(gridGroup);
    gridEditor->setPlainText(tr("This widget will take up three rows in "
                                "the grid layout."));
    gridLayout->addWidget(gridEditor, 0, 2, 3, 1);
    gridLayout->setColumnStretch(1, 10);
    gridLayout->setColumnStretch(2, 20);

    QTextEdit *bigEditor = new QTextEdit(this);
    bigEditor->setPlainText(tr("This widget will take up all the remaining "
                               "space in the top-level layout."));

    QPushButton *okButton = new QPushButton(tr("OK"), this);
    QPushButton *cancelButton = new QPushButton(tr("Cancel"), this );
    okButton->setDefault(true);

    QHBoxLayout *paddedLayout = new QHBoxLayout;
    paddedLayout->addStretch(1);
    paddedLayout->addWidget(okButton);
    paddedLayout->addWidget(cancelButton);

    QBoxLayout *mainLayout = new QVBoxLayout(this);
    mainLayout->setMenuBar(menuBar);
    mainLayout->addWidget(horizontalGroup);
    mainLayout->addWidget(gridGroup);
    mainLayout->addWidget(bigEditor);
    mainLayout->addLayout(paddedLayout);

    setWindowTitle(tr("Basic layouts"));
}

void QtWindow::showMessageBox()
{
    QMessageBox::information(this, "Hello SWT", "This is a Qt MessageBox");
    emit messageBoxClosed();
}

QSWT_MAIN_BEGIN("qtexample", "", 
    "{0EBD4B7A-7A8B-4DED-8111-CCC9284E8506}", "{70B132BA-16EA-4F39-8970-5025785BEEFE}")
    QSWT_CLASS(QtWindow, "window.h", "window.cpp")
QSWT_MAIN_END()
