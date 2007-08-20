package generator;

import com.trolltech.qt.QNativePointer;
import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.gui.*;

class QTransform___ extends QTransform {

    public final QTransform multiply(double d) {
        operator_multiply_assign(d);
        return this;
    }

    public final QTransform add(double d) {
        operator_add_assign(d);
        return this;
    }

    public final QTransform divide(double d) {
        operator_divide_assign(d);
        return this;
    }

    public final QTransform subtract(double d) {
        operator_subtract_assign(d);
        return this;
    }

    /**
     * Returns an inverted copy of this transformation.
     * 
     * @return The inverse of the transformation.
     * @throws IllegalArgumentException
     *             If this transformation is not invertible.
     */
    public final QTransform inverted() {
        QNativePointer ok = new QNativePointer(QNativePointer.Type.Boolean);
        QTransform returned = inverted(ok);
        if (!ok.booleanValue())
            throw new IllegalArgumentException("Transformation is not invertible");
        return returned;
    }

    /**
     * Creates a transformation mapping one arbitrary quad into another.
     * 
     * @return The transformation.
     * @throws IllegalArgumentException
     *             If this transformation is not possible.
     */
    public static final QTransform quadToQuad(QPolygonF from, QPolygonF to) {
        QTransform res = new QTransform();
        QNativePointer resPointer = res.nativePointer();
        if (quadToQuadPrivate(from, to, resPointer)) {
            return res;
        } else
            throw new IllegalArgumentException("Transformation is not possible");
    }

    /**
     * Creates a transformation that maps a unit square to a the given quad.
     * 
     * @return The transformation.
     * @throws IllegalArgumentException
     *             If this transformation is not possible.
     */
    public static final QTransform squareToQuad(QPolygonF quad) {
        QTransform res = new QTransform();
        QNativePointer resPointer = res.nativePointer();
        if (squareToQuadPrivate(quad, resPointer)) {
            return res;
        } else
            throw new IllegalArgumentException("Transformation is not possible");
    }
}// class

class QBitmap___ extends QBitmap {

    public QBitmap(String fileName, String format) {
        this(fileName, format == null ? null : com.trolltech.qt.QNativePointer.createCharPointer(format));
    }

    public QBitmap(String fileName) {
        this(fileName, (String) null);
    }

    public static QBitmap fromData(com.trolltech.qt.core.QSize size, byte bits[], QImage.Format monoFormat) {
        return fromData(size, QtJambiInternal.byteArrayToNativePointer(bits), monoFormat);
    }

    public static QBitmap fromData(com.trolltech.qt.core.QSize size, byte bits[]) {
        return fromData(size, bits, QImage.Format.Format_MonoLSB);
    }
}// class

class QPolygon___ extends QPolygon {

    private native void add_private(long nid, int x, int y);

    @QtBlockedSlot
    public final QPolygon add(int x, int y) {
        add_private(nativeId(), x, y);
        return this;
    }

    @QtBlockedSlot
    public final QPolygon add(QPoint pt) {
        add_private(nativeId(), pt.x(), pt.y());
        return this;
    }

    @QtBlockedSlot
    public final QPolygon add(QPolygon p) {
        int size = p.size();
        long nid = nativeId();
        for (int i = 0; i < size; ++i) {
            QPoint pt = p.at(i);
            add_private(nid, pt.x(), pt.y());
        }
        return this;
    }
}// class

class QPolygonF___ extends QPolygonF {
    private native void add_private(long nid, double x, double y);

    @QtBlockedSlot
    public final QPolygonF add(double x, double y) {
        add_private(nativeId(), x, y);
        return this;
    }

    @QtBlockedSlot
    public final QPolygonF add(QPointF pt) {
        add_private(nativeId(), pt.x(), pt.y());
        return this;
    }

    @QtBlockedSlot
    public final QPolygonF add(QPolygonF p) {
        int size = p.size();
        long nid = nativeId();
        for (int i = 0; i < size; ++i) {
            QPointF pt = p.at(i);
            add_private(nid, pt.x(), pt.y());
        }
        return this;
    }
}// class

class QTreeWidgetItemIterator___ extends QTreeWidgetItemIterator {
    @QtBlockedSlot
    public final void next(int i) {
        operator_add_assign(i);
    }

    @QtBlockedSlot
    public final void previous(int i) {
        operator_subtract_assign(i);
    }

    @QtBlockedSlot
    public final void next() {
        operator_increment();
    }

    @QtBlockedSlot
    public final void previous() {
        operator_decrement();
    }

    @QtBlockedSlot
    public final QTreeWidgetItem current() {
        return operator_multiply();
    }
}// class

class QTextCursor___ extends QTextCursor {
    public final QTableArea selectedTableCells() {
        QNativePointer firstRow = new QNativePointer(QNativePointer.Type.Int);
        QNativePointer numRows = new QNativePointer(QNativePointer.Type.Int);
        QNativePointer firstColumn = new QNativePointer(QNativePointer.Type.Int);
        QNativePointer numColumns = new QNativePointer(QNativePointer.Type.Int);

        selectedTableCells(firstRow, numRows, firstColumn, numColumns);

        return new QTableArea(firstRow.intValue(), firstColumn.intValue(), numRows.intValue(), numColumns.intValue());
    }
}// class

class QMatrix___ extends QMatrix {
    /**
     * Returns an inverted copy of this matrix.
     * 
     * @return The inverse of the matrix.
     * @throws IllegalArgumentException
     *             If this matrix is not invertible.
     */
    public final QMatrix inverted() {
        QNativePointer ok = new QNativePointer(QNativePointer.Type.Boolean);
        QMatrix returned = inverted(ok);
        if (!ok.booleanValue())
            throw new IllegalArgumentException("Matrix is not invertible");
        return returned;
    }

    @QtBlockedSlot
    public final QMatrix multiply(QMatrix other) {
        operator_multiply_assign(other);
        return this;
    }

    @QtBlockedSlot
    public final QMatrix multiplied(QMatrix other) {
        return operator_multiply(other);
    }
}// class

class QImage___ extends QImage {
    public QImage(String xpm[]) {
        this(com.trolltech.qt.QNativePointer.createCharPointerPointer(xpm));
    }

    public final byte[] copyOfBytes() {
        QNativePointer bits = bits();
        byte bytes[] = new byte[numBytes()];
        for (int i = 0; i < bytes.length; ++i)
            bytes[i] = bits.byteAt(i);
        return bytes;
    }

    public QImage(byte data[], int width, int height, Format format) {
        this(com.trolltech.qt.QtJambiInternal.byteArrayToNativePointer(data), width, height, format);
    }

    public QImage(String fileName, String format) {
        this(fileName, format == null ? null : QNativePointer.createCharPointer(format));
    }

    public QImage(String fileName) {
        this(fileName, (String) null);
    }
}// class

class QPen___ extends QPen {
    public QPen(QColor color, double width, com.trolltech.qt.core.Qt.PenStyle s, com.trolltech.qt.core.Qt.PenCapStyle c, com.trolltech.qt.core.Qt.PenJoinStyle j) {
        this(new QBrush(color), width, s, c, j);
    }

    public QPen(QColor color, double width, com.trolltech.qt.core.Qt.PenStyle s, com.trolltech.qt.core.Qt.PenCapStyle c) {
        this(new QBrush(color), width, s, c);
    }

    public QPen(QColor color, double width, com.trolltech.qt.core.Qt.PenStyle s) {
        this(new QBrush(color), width, s);
    }

    public QPen(QColor color, double width) {
        this(new QBrush(color), width);
    }

    public static final QPen NoPen = new QPen(com.trolltech.qt.core.Qt.PenStyle.NoPen);
}// Class

class QColor___ extends QColor {
    public static final QColor white = new QColor(com.trolltech.qt.core.Qt.GlobalColor.white);
    public static final QColor black = new QColor(com.trolltech.qt.core.Qt.GlobalColor.black);
    public static final QColor red = new QColor(com.trolltech.qt.core.Qt.GlobalColor.red);
    public static final QColor darkRed = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkRed);
    public static final QColor green = new QColor(com.trolltech.qt.core.Qt.GlobalColor.green);
    public static final QColor darkGreen = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkGreen);
    public static final QColor blue = new QColor(com.trolltech.qt.core.Qt.GlobalColor.blue);
    public static final QColor darkBlue = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkBlue);
    public static final QColor cyan = new QColor(com.trolltech.qt.core.Qt.GlobalColor.cyan);
    public static final QColor darkCyan = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkCyan);
    public static final QColor magenta = new QColor(com.trolltech.qt.core.Qt.GlobalColor.magenta);
    public static final QColor darkMagenta = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkMagenta);
    public static final QColor yellow = new QColor(com.trolltech.qt.core.Qt.GlobalColor.yellow);
    public static final QColor darkYellow = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkYellow);
    public static final QColor gray = new QColor(com.trolltech.qt.core.Qt.GlobalColor.gray);
    public static final QColor darkGray = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkGray);
    public static final QColor lightGray = new QColor(com.trolltech.qt.core.Qt.GlobalColor.lightGray);
    public static final QColor transparent = new QColor(com.trolltech.qt.core.Qt.GlobalColor.transparent);
    public static final QColor color0 = new QColor(com.trolltech.qt.core.Qt.GlobalColor.color0);
    public static final QColor color1 = new QColor(com.trolltech.qt.core.Qt.GlobalColor.color1);

}// class
