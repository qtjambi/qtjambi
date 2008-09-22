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

@QtJambiExample(name = "Draggable Text")
public class DraggableText extends QWidget
{
    public DraggableText()
    {
        QFile dictionaryFile = new QFile("classpath:com/trolltech/examples/dictionary/words.txt");
        dictionaryFile.open(QIODevice.OpenModeFlag.ReadOnly);
        QTextStream inputStream = new QTextStream(dictionaryFile);

        int x = 5;
        int y = 5;

        while (!inputStream.atEnd()) {
            String word = inputStream.readString();
            if (!word.equals("")) {
                DragLabel wordLabel = new DragLabel(word, this);
                wordLabel.move(x, y);
                wordLabel.show();
                x += wordLabel.width() + 2;
                if (x >= 195) {
                    x = 5;
                    y += wordLabel.height() + 2;
                }
            }
        }

        QPalette newPalette = palette();
        newPalette.setColor(QPalette.ColorRole.Window, QColor.white);
        setPalette(newPalette);

        setAcceptDrops(true);
        setMinimumSize(400, Math.max(200, y));
        setWindowTitle(tr("Draggable Text"));
}
    protected void dragEnterEvent(QDragEnterEvent event)
    {
        if (event.mimeData().hasText()) {
            if (children().contains(event.source())) {
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
        if (event.mimeData().hasText()) {
            com.trolltech.qt.core.QMimeData mime = event.mimeData();
            String pieces[] = mime.text().split("\\s+");
            QPoint position = event.pos();
            QPoint hotSpot = new QPoint();

            String hotSpotPos[] = mime.data("application/x-hotspot").toString().split("\\s");
            if (hotSpotPos.length == 2) {
                hotSpot.setX(Integer.parseInt(hotSpotPos[0]));
                hotSpot.setY(Integer.parseInt(hotSpotPos[1]));
            }

            for (String piece : pieces) {
                DragLabel newLabel = new DragLabel(piece, this);
                newLabel.move(position.subtract(hotSpot));
                newLabel.show();

                position.add(new QPoint(newLabel.width(), 0));
            }

            if (children().contains(event.source())) {
                event.setDropAction(Qt.DropAction.MoveAction);
                event.accept();
            } else {
                event.acceptProposedAction();
            }
        } else {
            event.ignore();
        }
    }

    class DragLabel extends QLabel
    {
        public DragLabel(String text, QWidget parent)
        {
            super(parent);

            setText(text);
            setAutoFillBackground(true);
            setFrameShape(QFrame.Shape.Panel);
            setFrameShadow(QFrame.Shadow.Raised);
        }

        protected void mousePressEvent(QMouseEvent event)
        {
            QPoint hotSpot = event.pos();

            com.trolltech.qt.core.QMimeData mimeData = new com.trolltech.qt.core.QMimeData();
            mimeData.setText(text());
            mimeData.setData("application/x-hotspot",
                              new QByteArray(String.valueOf(hotSpot.x())
                              + " " + String.valueOf(hotSpot.y())));

            QPixmap pixmap = new QPixmap(size());
            render(pixmap);

            QDrag drag = new QDrag(this);
            drag.setMimeData(mimeData);
            drag.setPixmap(pixmap);
            drag.setHotSpot(hotSpot);

            Qt.DropAction dropAction = drag.exec(
                new Qt.DropActions(Qt.DropAction.CopyAction,
                                 Qt.DropAction.MoveAction,
                                 Qt.DropAction.CopyAction));

            if (dropAction == Qt.DropAction.MoveAction) {
                close();
                update();
            }
        }
    }

    public static void main(String args[])
    {
        QApplication.initialize(args);

        new DraggableText().show();

        QApplication.exec();
    }

}
