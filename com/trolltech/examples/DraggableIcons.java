
/****************************************************************************
**
** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of $PRODUCT$.
**
** $JAVA_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.examples;


import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

@QtJambiExample(name = "Draggable Icons")
public class DraggableIcons extends QFrame
{

//![0]
    public DraggableIcons()
    {
        setMinimumSize(200, 200);
        setFrameStyle(QFrame.Shadow.Sunken.value() | QFrame.Shape.StyledPanel.value());
        setAcceptDrops(true);

        QLabel boatIcon = new QLabel(this);
        boatIcon.setPixmap(new QPixmap("classpath:com/trolltech/examples/images/boat.png"));
        boatIcon.move(20, 20);

        QLabel carIcon = new QLabel(this);
        carIcon.setPixmap(new QPixmap("classpath:com/trolltech/examples/images/car.png"));
        carIcon.move(120, 20);

        QLabel houseIcon = new QLabel(this);
        houseIcon.setPixmap(new QPixmap("classpath:com/trolltech/examples/images/house.png"));
        houseIcon.move(20, 120);
    }
//![0]

    protected void dragEnterEvent(QDragEnterEvent event)
    {
        if (event.mimeData().hasFormat("application/x-dnditemdata")) {
            if (event.source().equals(this)) {
                event.setDropAction(Qt.DropAction.MoveAction);
                event.accept();
            } else {
                event.acceptProposedAction();
            }
        } else {
            event.ignore();
        }
    }

    protected void dragMoveEvent(QDragMoveEvent event)
    {
        if (event.mimeData().hasFormat("application/x-dnditemdata")) {
            if (event.source().equals(this)) {
                event.setDropAction(Qt.DropAction.MoveAction);
                event.accept();
            } else {
                event.acceptProposedAction();
            }
        } else {
            event.ignore();
        }
    }

    protected void dropEvent(QDropEvent event)
    {
        if (event.mimeData().hasFormat("application/x-dnditemdata")) {
            QByteArray itemData = event.mimeData().data("application/x-dnditemdata");
            QDataStream dataStream = new QDataStream(itemData, QIODevice.OpenModeFlag.ReadOnly);
        
            QPixmap pixmap = new QPixmap();
            QPoint offset = new QPoint();
            pixmap.readFrom(dataStream);
            offset.readFrom(dataStream);

            QLabel newIcon = new QLabel(this);
            newIcon.setPixmap(pixmap);
            newIcon.move(event.pos().subtract(offset));
            newIcon.show();
            newIcon.setAttribute(Qt.WidgetAttribute.WA_DeleteOnClose);

            if (event.source().equals(this)) {
                event.setDropAction(Qt.DropAction.MoveAction);
                event.accept();
            } else {
                event.acceptProposedAction();
            }
        } else {
            event.ignore();
        }
    }

//![1]
    protected void mousePressEvent(QMouseEvent event)
    {
        QLabel child = (QLabel) childAt(event.pos());
        if (child == null)
            return;

        QPixmap pixmap = child.pixmap();

        QByteArray itemData = new QByteArray();
        QDataStream dataStream = new QDataStream(itemData, QIODevice.OpenModeFlag.WriteOnly);
        pixmap.writeTo(dataStream);
        event.pos().subtract(child.pos()).writeTo(dataStream);

//! [1]

//! [2]
        com.trolltech.qt.core.QMimeData mimeData = new com.trolltech.qt.core.QMimeData();
        mimeData.setData("application/x-dnditemdata", itemData);
//! [2]
        
//! [3]
        QDrag drag = new QDrag(this);
        drag.setMimeData(mimeData);
        drag.setPixmap(pixmap);
        drag.setHotSpot(event.pos().subtract(child.pos()));
//! [3]

        QPixmap tempPixmap = new QPixmap(pixmap);
        QPainter painter = new QPainter();
        painter.begin(tempPixmap);
        painter.fillRect(pixmap.rect(), new QBrush(new QColor(127, 127, 127, 127)));
        painter.end();

        child.setPixmap(tempPixmap);

        if (drag.exec(new Qt.DropActions(Qt.DropAction.CopyAction,
            Qt.DropAction.MoveAction, Qt.DropAction.CopyAction)) == Qt.DropAction.MoveAction)
            child.close();
        else {
            child.show();
            child.setPixmap(pixmap);
        }
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        QWidget mainWidget = new QWidget();
        QHBoxLayout horizontalLayout = new QHBoxLayout();
        horizontalLayout.addWidget(new DraggableIcons());
        horizontalLayout.addWidget(new DraggableIcons());

        mainWidget.setLayout(horizontalLayout);
        mainWidget.setWindowTitle("Draggable Icons");
        mainWidget.show();

        QApplication.exec();
    }
}
