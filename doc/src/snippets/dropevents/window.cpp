#include <QtGui>

#include "window.h"

//! [0]
Window::Window(QWidget *parent)
    : QWidget(parent)
{
//! [0]
    QLabel *textLabel = new QLabel(tr("Data:"), this);
    textBrowser = new QTextBrowser(this);

    QLabel *mimeTypeLabel = new QLabel(tr("MIME types:"), this);
    mimeTypeCombo = new QComboBox(this);

    QVBoxLayout *layout = new QVBoxLayout(this);
    layout->addWidget(textLabel);
    layout->addWidget(textBrowser);
    layout->addWidget(mimeTypeLabel);
    layout->addWidget(mimeTypeCombo);

//! [1]
    setAcceptDrops(true);
//! [1]
    setWindowTitle(tr("Drop Events"));
//! [2]
}
//! [2]

//! [3]
void Window::dragEnterEvent(QDragEnterEvent *event)
{
    if (event->mimeData()->hasFormat("text/plain"))
        event->acceptProposedAction();
}
//! [3]

//! [4]
void Window::dropEvent(QDropEvent *event)
{
    textBrowser->setPlainText(event->mimeData()->text());
    mimeTypeCombo->clear();
    mimeTypeCombo->addItems(event->mimeData()->formats());

    event->acceptProposedAction();
}
//! [4]
