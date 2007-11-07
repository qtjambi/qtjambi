#include <QtGui>

#include "window.h"

Window::Window(QWidget *parent)
    : QWidget(parent)
{
    QLabel *textLabel = new QLabel(tr("Data:"), this);
    textBrowser = new QTextBrowser(this);

    QLabel *mimeTypeLabel = new QLabel(tr("MIME types:"), this);
    mimeTypeCombo = new QComboBox(this);

    QVBoxLayout *layout = new QVBoxLayout(this);
    layout->addWidget(textLabel);
    layout->addWidget(textBrowser);
    layout->addWidget(mimeTypeLabel);
    layout->addWidget(mimeTypeCombo);
/*
    ...
    setAcceptDrops(true);
*/
    setAcceptDrops(true);
    setWindowTitle(tr("Drop Actions"));
}

void Window::dragEnterEvent(QDragEnterEvent *event)
{
    if (event->mimeData()->hasFormat("text/plain"))
        event->acceptProposedAction();
}

void Window::dropEvent(QDropEvent *event)
{
    QMenu actionMenu(this);
    QAction *copyAction = 0;
    QAction *moveAction = 0;
    QAction *linkAction = 0;
    QAction *ignoreAction = 0;
    if (event->possibleActions() & Qt::CopyAction)
        copyAction = actionMenu.addAction(tr("Copy"));
    if (event->possibleActions() & Qt::MoveAction)
        moveAction = actionMenu.addAction(tr("Move"));
    if (event->possibleActions() & Qt::LinkAction)
        linkAction = actionMenu.addAction(tr("Link"));
    if (event->possibleActions() & Qt::IgnoreAction)
        ignoreAction = actionMenu.addAction(tr("Ignore"));

    QAction *result = actionMenu.exec(QCursor::pos());

    if (copyAction && result == copyAction)
        event->setDropAction(Qt::CopyAction);
    else if (moveAction && result == moveAction)
        event->setDropAction(Qt::MoveAction);
    else if (linkAction && result == linkAction)
        event->setDropAction(Qt::LinkAction);
    else {
        event->setDropAction(Qt::IgnoreAction);
        return;
    }

    textBrowser->setPlainText(event->mimeData()->text());
    mimeTypeCombo->clear();
    mimeTypeCombo->addItems(event->mimeData()->formats());
}
