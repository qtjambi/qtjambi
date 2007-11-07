/****************************************************************************
**
** Copyright (C) 2004-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

/*
finalwidget.cpp

A widget to display an image and a label containing a description.
*/

#include <QtGui>
#include "finalwidget.h"

FinalWidget::FinalWidget(QWidget *parent, const QString &name,
                         const QSize &labelSize)
    : QFrame(parent)
{
    hasImage = false;
    imageLabel = new QLabel;
    imageLabel->setFrameShadow(QFrame::Sunken);
    imageLabel->setFrameShape(QFrame::StyledPanel);
    imageLabel->setMinimumSize(labelSize);
    nameLabel = new QLabel(name);

    QVBoxLayout *layout = new QVBoxLayout;
    layout->addWidget(imageLabel, 1);
    layout->addWidget(nameLabel, 0);
    setLayout(layout);
}

/*!
    If the mouse moves far enough when the left mouse button is held down,
    start a drag and drop operation.
*/

void FinalWidget::mouseMoveEvent(QMouseEvent *event)
{
    if (!(event->buttons() & Qt::LeftButton))
        return;
    if ((event->pos() - dragStartPosition).manhattanLength()
         < QApplication::startDragDistance())
        return;
    if (!hasImage)
        return;

    QDrag *drag = new QDrag(this);
    QMimeData *mimeData = new QMimeData;

//! [0]
    QByteArray output;
    QBuffer outputBuffer(&output);
    outputBuffer.open(QIODevice::WriteOnly);
    imageLabel->pixmap()->toImage().save(&outputBuffer, "PNG");
    mimeData->setData("image/png", output);
//! [0]
/*
//! [1]
    mimeData->setImageData(QVariant(*imageLabel->pixmap()));
//! [1]
*/
    drag->setMimeData(mimeData);
    drag->setPixmap(imageLabel->pixmap()->scaled(64, 64, Qt::KeepAspectRatio));
//! [2]
    drag->setHotSpot(QPoint(drag->pixmap().width()/2,
                            drag->pixmap().height()));
//! [2]

    drag->start();
}

/*!
    Check for left mouse button presses in order to enable drag and drop.
*/

void FinalWidget::mousePressEvent(QMouseEvent *event)
{
    if (event->button() == Qt::LeftButton)
        dragStartPosition = event->pos();
}

const QPixmap* FinalWidget::pixmap() const
{
    return imageLabel->pixmap();
}

void FinalWidget::setPixmap(const QPixmap &pixmap)
{
    imageLabel->setPixmap(pixmap);
    hasImage = true;
}
