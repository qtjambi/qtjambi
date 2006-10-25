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

import java.util.Vector;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class ItemviewChart extends QMainWindow {

    private QAbstractItemModel model;
    private QAbstractItemView pieChart;

    public static void main(String args[]) {
        QApplication.initialize(args);

        ItemviewChart mainW = new ItemviewChart();
        mainW.show();

        QApplication.exec();
    }

    public ItemviewChart() {
        QMenu fileMenu = new QMenu(tr("&File"), this);

        QAction openAction = new QAction(tr("&Open..."), this);
        openAction.setShortcut(new QKeySequence(tr("Ctrl+O")));
        openAction.triggered.connect(this, "openFile()");
        fileMenu.addAction(openAction);

        QAction saveAction = new QAction(tr("&Save As..."), this);
        saveAction.setShortcut(new QKeySequence(tr("Ctrl+S")));
        saveAction.triggered.connect(this, "saveFile()");
        fileMenu.addAction(saveAction);

        QAction quitAction = new QAction(tr("&Quit"), this);
        quitAction.setShortcut(new QKeySequence(tr("Ctrl+Q")));
        quitAction.triggered.connect(this, "close()");
        fileMenu.addAction(quitAction);

        setupModel();
        setupViews();

        menuBar().addMenu(fileMenu);
        statusBar();

        openFile("classpath:com/trolltech/examples/qtdata.cht");

        setWindowTitle(tr("Chart"));
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
        resize(750, 500);
    }

    private void setupModel() {
        model = new QStandardItemModel(8, 2, this);
        model.setHeaderData(0, Qt.Orientation.Horizontal, tr("Label"));
        model.setHeaderData(1, Qt.Orientation.Horizontal, tr("Quantity"));
    }

    private void setupViews() {
        QSplitter splitter = new QSplitter();
        QTableView table = new QTableView();
        pieChart = new PieView(this);
        splitter.addWidget(table);
        splitter.addWidget(pieChart);
        splitter.setStretchFactor(0, 0);
        splitter.setStretchFactor(1, 1);

        table.setModel(model);
        pieChart.setModel(model);

        QItemSelectionModel selectionModel = new QItemSelectionModel(model);
        table.setSelectionModel(selectionModel);
        pieChart.setSelectionModel(selectionModel);

        setCentralWidget(splitter);
    }

    @SuppressWarnings("unused")
    private void openFile() {
        openFile("");
    }

    private void openFile(String path) {
        String fileName;
        if (path.equals(""))
            fileName = QFileDialog.getOpenFileName(this, tr("Choose a data file"), "", "*.cht");
        else
            fileName = path;

        if (!fileName.equals("")) {
            QFile file = new QFile(fileName);

            if (file.open(new QFile.OpenMode(QFile.OpenModeFlag.ReadOnly, QFile.OpenModeFlag.Text))) {
                QTextStream stream = new QTextStream(file);
                String line;

                model.removeRows(0, model.rowCount(new QModelIndex()), new QModelIndex());

                int row = 0;
                do {
                    line = stream.readLine();
                    if (!line.equals("")) {

                        model.insertRows(row, 1, new QModelIndex());

                        String[] pieces = line.split(",");

                        model.setData(model.index(row, 0, new QModelIndex()), pieces[0].trim());
                        model.setData(model.index(row, 1, new QModelIndex()), pieces[1].trim());
                        model.setData(model.index(row, 0, new QModelIndex()), new QColor(pieces[2].trim()), Qt.ItemDataRole.DecorationRole);
                        row++;

                    }
                } while (!line.equals(""));

                file.close();
                statusBar().showMessage(tr("Loaded " + fileName), 2000);
            }
        }
    }

    @SuppressWarnings("unused")
    private void saveFile() {
        String fileName = QFileDialog.getSaveFileName(this, tr("Save file as"), "", "*.cht");

        if (!fileName.equals("")) {
            QFile file = new QFile(fileName);
            QTextStream stream = new QTextStream(file);

            if (file.open(new QFile.OpenMode(QFile.OpenModeFlag.WriteOnly, QFile.OpenModeFlag.Text))) {
                for (int row = 0; row < model.rowCount(new QModelIndex()); ++row) {

                    stream.operator_shift_left(model.data(model.index(row, 0, new QModelIndex()), Qt.ItemDataRole.DisplayRole).toString() + ",");
                    stream.operator_shift_left(model.data(model.index(row, 1, new QModelIndex()), Qt.ItemDataRole.DisplayRole).toString() + ",");
                    stream.operator_shift_left(((QColor) model.data(model.index(row, 0, new QModelIndex()), Qt.ItemDataRole.DecorationRole)).name());
                    stream.operator_shift_left("\n");
                }
            }

            file.close();
            statusBar().showMessage(tr("Saved " + fileName), 2000);
        }
    }

    private class PieView extends QAbstractItemView {

        private int margin;
        private int totalSize;
        private int pieSize;
        private int validItems;
        private double totalValue;
        private QPoint origin;
        private QRubberBand rubberBand;

        public PieView(QWidget parent) {
            super(parent);
            horizontalScrollBar().setRange(0, 0);
            verticalScrollBar().setRange(0, 0);

            margin = 8;
            totalSize = 300;
            pieSize = totalSize - 2 * margin;
            validItems = 0;
            totalValue = 0.0;
        }

        protected void dataChanged(final QModelIndex topLeft, final QModelIndex bottomRight) {
            super.dataChanged(topLeft, bottomRight);

            validItems = 0;
            totalValue = 0.0;

            for (int row = 0; row < model().rowCount(rootIndex()); ++row) {

                QModelIndex index = model().index(row, 1, rootIndex());
                double value = toDouble(model().data(index));

                if (value > 0.0) {
                    totalValue += value;
                    validItems++;
                }
            }
            viewport().update();
        }

        protected boolean edit(final QModelIndex index, EditTrigger trigger, QEvent event) {
            return false;
        }

        public QModelIndex indexAt(final QPoint point) {
            if (validItems == 0)
                return new QModelIndex();

            int wx = point.x() + horizontalScrollBar().value();
            int wy = point.y() + verticalScrollBar().value();

            if (wx < totalSize) {
                double cx = wx - totalSize / 2;
                double cy = totalSize / 2 - wy;
                double d = Math.pow(Math.pow(cx, 2) + Math.pow(cy, 2), 0.5);

                if (d == 0 || d > pieSize / 2)
                    return new QModelIndex();

                double angle = (180 / Math.PI) * Math.acos(cx / d);
                if (cy < 0)
                    angle = 360 - angle;

                double startAngle = 0.0;

                for (int row = 0; row < model().rowCount(rootIndex()); ++row) {

                    QModelIndex index = model().index(row, 1, rootIndex());
                    double value = toDouble(model().data(index));

                    if (value > 0.0) {
                        double sliceAngle = 360 * value / totalValue;

                        if (angle >= startAngle && angle < (startAngle + sliceAngle))
                            return model().index(row, 1, rootIndex());

                        startAngle += sliceAngle;
                    }
                }
            }

            return new QModelIndex();
        }

        protected boolean isIndexHidden(final QModelIndex index) {
            return false;
        }

        QRect itemRect(final QModelIndex index) {
            if (!index.isValid())
                return new QRect();

            if (index.column() != 1)
                return new QRect();

            if (toDouble(model().data(index)) > 0.0) {

                int listItem = 0;
                for (int row = index.row() - 1; row >= 0; --row) {
                    if (toDouble(model().data(model().index(row, 1, rootIndex()))) > 0.0)
                        listItem++;
                }

                return new QRect(margin, margin, pieSize, pieSize);
            }
            return new QRect();
        }

        QRegion itemRegion(final QModelIndex index) {
            if (!index.isValid())
                return new QRegion();

            if (index.column() != 1)
                return new QRegion();

            if (toDouble(model().data(index)) <= 0.0)
                return new QRegion();

            double startAngle = 0.0;
            for (int row = 0; row < model().rowCount(rootIndex()); ++row) {

                QModelIndex sliceIndex = model().index(row, 1, rootIndex());
                double value = toDouble(model().data(sliceIndex));

                if (value > 0.0) {
                    double angle = 360 * value / totalValue;

                    if (sliceIndex.equals(index)) {
                        QPainterPath slicePath = new QPainterPath();
                        slicePath.moveTo(totalSize / 2, totalSize / 2);
                        slicePath.arcTo(margin, margin, margin + pieSize, margin + pieSize, startAngle, angle);
                        slicePath.closeSubpath();

                        return new QRegion(slicePath.toFillPolygon().toPolygon());
                    }
                    startAngle += angle;
                }
            }

            return new QRegion();
        }

        protected int horizontalOffset() {
            return horizontalScrollBar().value();
        }

        protected void mousePressEvent(QMouseEvent event) {
            super.mousePressEvent(event);
            origin = event.pos();
            if (rubberBand == null)
                rubberBand = new QRubberBand(QRubberBand.Shape.Rectangle, this);
            rubberBand.setGeometry(new QRect(origin, new QSize()));
            rubberBand.show();
        }

        protected void mouseMoveEvent(QMouseEvent event) {
            QRect rect = new QRect(origin, event.pos()).normalized();
            rubberBand.setGeometry(rect);
            super.mouseMoveEvent(event);

            QModelIndex underMouseIndex = indexAt(event.pos());
            if (!underMouseIndex.isValid())
                setSelection(rect, selectionCommand(underMouseIndex, event));
            viewport().update();
        }

        protected void mouseReleaseEvent(QMouseEvent event) {
            super.mouseReleaseEvent(event);
            rubberBand.hide();
            viewport().update();
        }

        protected QModelIndex moveCursor(QAbstractItemView.CursorAction cursorAction, Qt.KeyboardModifiers modifiers) {
            QModelIndex current = currentIndex();

            switch (cursorAction) {
            case MoveLeft:
            case MoveUp:
                if (current.row() > 0)
                    current = model().index(current.row() - 1, current.column(), rootIndex());
                else
                    current = model().index(0, current.column(), rootIndex());
                break;
            case MoveRight:
            case MoveDown:
                if (current.row() < rows(current) - 1)
                    current = model().index(current.row() + 1, current.column(), rootIndex());
                else
                    current = model().index(rows(current) - 1, current.column(), rootIndex());
                break;
            default:
                break;
            }

            viewport().update();
            return current;
        }

        protected void paintEvent(QPaintEvent event) {
            QItemSelectionModel selections = selectionModel();
            QStyleOptionViewItem option = viewOptions();

            QBrush background = option.palette().base();
            QPen foreground = new QPen(option.palette().color(QPalette.ColorRole.WindowText));

            QPainter painter = new QPainter();
            painter.begin(viewport());
            painter.setRenderHint(QPainter.RenderHint.Antialiasing);

            painter.fillRect(event.rect(), background);
            painter.setPen(foreground);

            QRect pieRect = new QRect(margin, margin, pieSize, pieSize);

            if (validItems > 0) {

                painter.save();
                painter.translate(pieRect.x() - horizontalScrollBar().value(), pieRect.y() - verticalScrollBar().value());
                painter.drawEllipse(0, 0, pieSize, pieSize);
                double startAngle = 0.0;
                int row;

                for (row = 0; row < model().rowCount(rootIndex()); ++row) {

                    QModelIndex index = model().index(row, 1, rootIndex());
                    double value = toDouble(model().data(index));

                    if (value > 0.0) {
                        double angle = 360 * value / totalValue;

                        QModelIndex colorIndex = model().index(row, 0, rootIndex());
                        QColor color = (QColor) model().data(colorIndex, Qt.ItemDataRole.DecorationRole);

                        if (currentIndex() != null && currentIndex().equals(index) && selections.isSelected(index))
                            painter.setBrush(new QBrush(color, Qt.BrushStyle.Dense4Pattern));
                        else if (selections.isSelected(index))
                            painter.setBrush(new QBrush(color, Qt.BrushStyle.Dense3Pattern));
                        else
                            painter.setBrush(new QBrush(color));

                        painter.drawPie(0, 0, pieSize, pieSize, (int) (startAngle * 16), (int) (angle * 16));

                        startAngle += angle;
                    }
                }
                painter.restore();
            }
            painter.end();
        }

        protected void resizeEvent(QResizeEvent event) {
            updateGeometries();
        }

        int rows(final QModelIndex index) {
            return model().rowCount(model().parent(index));
        }

        protected void rowsInserted(final QModelIndex parent, int start, int end) {
            for (int row = start; row <= end; ++row) {

                QModelIndex index = model().index(row, 1, rootIndex());
                double value = toDouble(model().data(index));

                if (value > 0.0) {
                    totalValue += value;
                    validItems++;
                }
            }

            super.rowsInserted(parent, start, end);
        }

        protected void rowsAboutToBeRemoved(final QModelIndex parent, int start, int end) {
            for (int row = start; row <= end; ++row) {

                QModelIndex index = model().index(row, 1, rootIndex());
                double value = toDouble(model().data(index));
                if (value > 0.0) {
                    totalValue -= value;
                    validItems--;
                }
            }

            super.rowsAboutToBeRemoved(parent, start, end);
        }

        public void scrollTo(final QModelIndex index, ScrollHint hint) {
            QRect area = viewport().rect();
            QRect rect = visualRect(index);

            if (rect.left() < area.left())
                horizontalScrollBar().setValue(
                    horizontalScrollBar().value() + rect.left() - area.left());
            else if (rect.right() > area.right())
                horizontalScrollBar().setValue(
                    horizontalScrollBar().value() + Math.min(
                        rect.right() - area.right(), rect.left() - area.left()));

            if (rect.top() < area.top())
                verticalScrollBar().setValue(
                    verticalScrollBar().value() + rect.top() - area.top());
            else if (rect.bottom() > area.bottom())
                verticalScrollBar().setValue(
                    verticalScrollBar().value() + Math.min(
                        rect.bottom() - area.bottom(), rect.top() - area.top()));

            update();
        }

        protected void setSelection(final QRect rect, QItemSelectionModel.SelectionFlags command) {
            QRect contentsRect = rect.translated(horizontalScrollBar().value(), verticalScrollBar().value()).normalized();

            int rows = model().rowCount(rootIndex());
            int columns = model().columnCount(rootIndex());
            Vector<QModelIndex> indexes = new Vector<QModelIndex>();

            for (int row = 0; row < rows; ++row) {
                for (int column = 0; column < columns; ++column) {
                    QModelIndex index = model().index(row, column, rootIndex());
                    QRegion region = itemRegion(index);

                    if (!region.intersect(new QRegion(contentsRect)).isEmpty())
                        indexes.add(index);
                }
            }

            if (indexes.size() > 0) {
                int firstRow = indexes.elementAt(0).row();
                int lastRow = indexes.elementAt(0).row();
                int firstColumn = indexes.elementAt(0).column();
                int lastColumn = indexes.elementAt(0).column();

                for (int i = 1; i < indexes.size(); ++i) {
                    firstRow = Math.min(firstRow, indexes.elementAt(i).row());
                    lastRow = Math.max(lastRow, indexes.elementAt(i).row());
                    firstColumn = Math.min(firstColumn, indexes.elementAt(i).column());
                    lastColumn = Math.max(lastColumn, indexes.elementAt(i).column());
                }

                QItemSelection selection = new QItemSelection(
                    model().index(firstRow, firstColumn, rootIndex()),
                    model().index(lastRow, lastColumn, rootIndex()));
                selectionModel().select(selection, command);
            } else {
                QModelIndex noIndex = new QModelIndex();
                QItemSelection selection = new QItemSelection(noIndex, noIndex);
                selectionModel().select(selection, command);
            }

            update();
        }

        protected void updateGeometries() {
            horizontalScrollBar().setPageStep(viewport().width());
            horizontalScrollBar().setRange(0, Math.max(0, totalSize - viewport().width()));
            verticalScrollBar().setPageStep(viewport().height());
            verticalScrollBar().setRange(0, Math.max(0, totalSize - viewport().height()));
        }

        protected int verticalOffset() {
            return verticalScrollBar().value();
        }

        public QRect visualRect(final QModelIndex index) {
            QRect rect = itemRect(index);
            if (rect.isValid())
                return new QRect(rect.left() - horizontalScrollBar().value(), rect.top() - verticalScrollBar().value(), rect.width(), rect.height());
            else
                return rect;
        }

        protected QRegion visualRegionForSelection(final QItemSelection selection) {
            int ranges = selection.size();

            if (ranges == 0)
                return new QRegion(new QRect());

            QRegion region = new QRegion();
            for (int i = 0; i < ranges; ++i) {
                QItemSelectionRange range = selection.at(i);
                for (int row = range.top(); row <= range.bottom(); ++row) {
                    for (int col = range.left(); col <= range.right(); ++col) {
                        QModelIndex index = model().index(row, col, rootIndex());
                        region.operator_add_assign(new QRegion(visualRect(index)));
                    }
                }
            }
            return region;
        }
    }

    private double toDouble(Object o) {

        if (o instanceof String) {
            try {
                return Double.parseDouble((String) o);
            } catch (NumberFormatException e) {

            }
        }
        return 0;
    }

    // REMOVE-START

    public static String exampleName() {
        return "ItemView With Chart";
    }

    public static boolean canInstantiate() {
        return true;
    }

    // REMOVE-END
}
