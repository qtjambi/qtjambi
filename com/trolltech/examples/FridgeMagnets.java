/****************************************************************************
 **
 **  (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
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

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

@QtJambiExample(name = "Drag and Drop")
public class FridgeMagnets extends QWidget {

    public static void main(String args[]) {
        QApplication.initialize(args);
        FridgeMagnets fridgeMagnets = new FridgeMagnets(null);
        fridgeMagnets.show();
        QApplication.exec();
    }

    public FridgeMagnets(QWidget parent) {
        super(parent);
        QFile dictionaryFile;
        dictionaryFile = new QFile("classpath:com/trolltech/examples/words.txt");
        dictionaryFile.open(QIODevice.OpenModeFlag.ReadOnly);
        QTextStream inputStream = new QTextStream(dictionaryFile);

        int x = 5;
        int y = 5;

        while (!inputStream.atEnd()) {
            String word = "";
            word = inputStream.readLine();
            if (!word.equals("")) {
                DragLabel wordLabel = new DragLabel(word, this);
                wordLabel.move(x, y);
                wordLabel.show();
                x += wordLabel.width() + 2;
                if (x >= 245) {
                    x = 5;
                    y += wordLabel.height() + 2;
                }
            }
        }

        QPalette newPalette = palette();
        newPalette.setColor(QPalette.ColorRole.Window, QColor.white);
        setPalette(newPalette);

        setMinimumSize(400, Math.max(200, y));
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
        setWindowTitle(tr("Fridge Magnets"));

        setAcceptDrops(true);
    }

    public void dragEnterEvent(QDragEnterEvent event) {
        if (event.mimeData().hasFormat("application/x-fridgemagnet")) {
            if (children().contains(event.source())) {
                event.setDropAction(Qt.DropAction.MoveAction);
                event.accept();
            } else {
                event.acceptProposedAction();
            }
        } else if (event.mimeData().hasText()) {
            event.acceptProposedAction();
        } else {
            event.ignore();
        }
    }

    public void dragMoveEvent(QDragMoveEvent event) {
        if (event.mimeData().hasFormat("application/x-fridgemagnet")) {
            if (children().contains(event.source())) {
                event.setDropAction(Qt.DropAction.MoveAction);
                event.accept();
            } else {
                event.acceptProposedAction();
            }
        } else if (event.mimeData().hasText()) {
            event.acceptProposedAction();
        } else {
            event.ignore();
        }
    }

    public void dropEvent(QDropEvent event) {
        if (event.mimeData().hasFormat("application/x-fridgemagnet")) {
            com.trolltech.qt.core.QMimeData mime = event.mimeData();
            QByteArray itemData = mime.data("application/x-fridgemagnet");
            QDataStream dataStream = new QDataStream(itemData,
                   new QIODevice.OpenMode(QIODevice.OpenModeFlag.ReadOnly));

            String text = dataStream.readString();
            QPoint offset = new QPoint();
            offset.readFrom(dataStream);

            DragLabel newLabel = new DragLabel(text, this);
            newLabel.move(new QPoint(event.pos().x() - offset.x(),
                                     event.pos().y() - offset.y()));
            newLabel.show();

            if (children().contains(event.source())) {
                event.setDropAction(Qt.DropAction.MoveAction);
                event.accept();
            } else {
                event.acceptProposedAction();
            }
        } else if (event.mimeData().hasText()) {
            String[] pieces = event.mimeData().text().split("\\s+");
            QPoint position = event.pos();

            for (String piece : pieces) {
                if (piece.equals(""))
                    continue;

                DragLabel newLabel = new DragLabel(piece, this);
                newLabel.move(position);
                newLabel.show();

                position.add(new QPoint(newLabel.width(), 0));
            }

            event.acceptProposedAction();
        } else {
            event.ignore();
        }
    }

    class DragLabel extends QLabel {
        private String labelText;

        public DragLabel(final String text, QWidget parent) {
            super(parent);

            QFontMetrics metrics = new QFontMetrics(font());
            QSize size = metrics.size(12, text);
            QImage image = new QImage(size.width() + 12, size.height() + 12,
                    QImage.Format.Format_ARGB32_Premultiplied);
            image.fill(0);

            QFont font = new QFont();
            font.setStyleStrategy(QFont.StyleStrategy.ForceOutline);

            QPainter painter = new QPainter();
            painter.begin(image);
            painter.setRenderHint(QPainter.RenderHint.Antialiasing);
            painter.setBrush(QColor.white);
            QRectF frame = new QRectF(0.5, 0.5, image.width() - 1,
                                      image.height() - 1);
            painter.drawRoundRect(frame, 25, 25);

            painter.setFont(font);
            painter.setBrush(QColor.black);

            QRect rectangle = new QRect(new QPoint(6, 6), size);
            painter.drawText(rectangle, Qt.AlignmentFlag.AlignCenter.value(),
                             text);
            painter.end();

            setPixmap(QPixmap.fromImage(image));
            labelText = text;
        }
        
        public void mousePressEvent(QMouseEvent event) {
            QByteArray itemData = new QByteArray();
            QDataStream dataStream;
            dataStream = new QDataStream(itemData,
                    new QIODevice.OpenMode(QIODevice.OpenModeFlag.WriteOnly));

            dataStream.writeString(labelText);
            QPoint position = new QPoint(event.pos().x() - rect().topLeft().x(),
                                         event.pos().y() - rect().topLeft().y());
            position.writeTo(dataStream);

            QMimeData mimeData = new MyMimeData();
            mimeData.setData("application/x-fridgemagnet", itemData);
            mimeData.setText(labelText);

            QDrag drag = new QDrag(this);
            drag.setMimeData(mimeData);

            drag.setHotSpot(new QPoint(event.pos().x() - rect().topLeft().x(),
                                       event.pos().y() - rect().topLeft().y()));
            drag.setPixmap(pixmap());

            hide();

            if (drag.exec(Qt.DropAction.MoveAction) == Qt.DropAction.MoveAction)
                close();
            else
                show();
        }
    }
}
