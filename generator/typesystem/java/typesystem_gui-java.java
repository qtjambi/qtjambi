/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
**
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
**
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
**
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
**
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package generator;

import com.trolltech.qt.QNativePointer;
import com.trolltech.qt.QtBlockedSlot;
import com.trolltech.qt.Utilities;
import com.trolltech.qt.QtJambiObject.QPrivateConstructor;
import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.gui.*;

class QTransform___ extends QTransform {

        public final QTransform multiply(double d) {
            operator_multiply_assign(d);
            return this;
        }

        public final QTransform multiply(QTransform matrix) {
            operator_multiply_assign(matrix);
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
         * Creates a transformation that maps a quad to a unit square.
         *
         * @return The transformation.
         * @throws IllegalArgumentException If this transformation is not possible.
         */
        public static final QTransform quadToSquare(QPolygonF quad) {
            QTransform res = new QTransform();
            QNativePointer resPointer = res.nativePointer();
            if (quadToSquarePrivate(quad, resPointer)) {
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
            return fromData(size, com.trolltech.qt.internal.QtJambiInternal.byteArrayToNativePointer(bits), monoFormat);
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
            this(com.trolltech.qt.internal.QtJambiInternal.byteArrayToNativePointer(data), width, height, format);
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
}// class

class QColor___ extends QColor {
        /**
         * Please refactor your code from using these variables into initializing directly with:
		 *
         *     new QColor(com.trolltech.qt.core.Qt.GlobalColor.white)
		 *
         * These variable are scheduled for removal in the Qt Jambi 5 release due to the
         * color values being mutable.
		 *
         * @deprecated
         */
        @Deprecated
        public static final QColor white = new QColor(com.trolltech.qt.core.Qt.GlobalColor.white);
        @Deprecated
        public static final QColor black = new QColor(com.trolltech.qt.core.Qt.GlobalColor.black);
        @Deprecated
        public static final QColor red = new QColor(com.trolltech.qt.core.Qt.GlobalColor.red);
        @Deprecated
        public static final QColor darkRed = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkRed);
        @Deprecated
        public static final QColor green = new QColor(com.trolltech.qt.core.Qt.GlobalColor.green);
        @Deprecated
        public static final QColor darkGreen = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkGreen);
        @Deprecated
        public static final QColor blue = new QColor(com.trolltech.qt.core.Qt.GlobalColor.blue);
        @Deprecated
        public static final QColor darkBlue = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkBlue);
        @Deprecated
        public static final QColor cyan = new QColor(com.trolltech.qt.core.Qt.GlobalColor.cyan);
        @Deprecated
        public static final QColor darkCyan = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkCyan);
        @Deprecated
        public static final QColor magenta = new QColor(com.trolltech.qt.core.Qt.GlobalColor.magenta);
        @Deprecated
        public static final QColor darkMagenta = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkMagenta);
        @Deprecated
        public static final QColor yellow = new QColor(com.trolltech.qt.core.Qt.GlobalColor.yellow);
        @Deprecated
        public static final QColor darkYellow = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkYellow);
        @Deprecated
        public static final QColor gray = new QColor(com.trolltech.qt.core.Qt.GlobalColor.gray);
        @Deprecated
        public static final QColor darkGray = new QColor(com.trolltech.qt.core.Qt.GlobalColor.darkGray);
        @Deprecated
        public static final QColor lightGray = new QColor(com.trolltech.qt.core.Qt.GlobalColor.lightGray);
        @Deprecated
        public static final QColor transparent = new QColor(com.trolltech.qt.core.Qt.GlobalColor.transparent);
        @Deprecated
        public static final QColor color0 = new QColor(com.trolltech.qt.core.Qt.GlobalColor.color0);
        @Deprecated
        public static final QColor color1 = new QColor(com.trolltech.qt.core.Qt.GlobalColor.color1);

}// class

class QTextLine___ extends QTextLine {

        public final void draw(QPainter painter, com.trolltech.qt.core.QPointF position) {
            draw(painter, position, null);
        }

}// class

class QLineF___ extends QLineF {

        public final QLineF.IntersectType intersect(QLineF line, com.trolltech.qt.core.QPointF intersectionPoint) {
            return intersect(line, intersectionPoint != null ? intersectionPoint.nativePointer() : null);
        }

}// class

class QLineEdit___ extends QLineEdit {

        /**
            The TextMargins class represents the margins of
            the text in a QLineEdit.<p>It consists of four
            public integers, giving the top, bottom,
            left, and right margins.
        */
        public static final class TextMargins {
                /** The top margin of the QLineEdit. */
                public final int top;
                /** The bottom margin of the QLineEdit */
                public final int bottom;
                /** The left margin of the QLineEdit. */
                public final int left;
                /** The right margin of the QLineEdit. */
                public final int right;

                private TextMargins(int left, int top, int right, int bottom) {
                    this.left = left;
                    this.top = top;
                    this.right = right;
                    this.bottom = bottom;
                }
        }

        public final TextMargins getTextMargins() {
            QNativePointer left = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer top = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer right = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer bottom = new QNativePointer(QNativePointer.Type.Int);

            getTextMargins_private(left, top, right, bottom);

            return new TextMargins(left.intValue(), top.intValue(), right.intValue(),
                                   bottom.intValue());
        }
}// class

class QKeySequence___ extends QKeySequence {

        @QtBlockedSlot
        public final int toInt() {
            return operator_cast_int();
        }

        @QtBlockedSlot
        public final int at(int i) {
            return operator_subscript(i);
        }

}// class

class QPicture___ extends QPicture {

        public final boolean load(QIODevice dev) {
            return load(dev, (QNativePointer) null);
        }

        public final boolean load(String fileName) {
            return load(fileName, (com.trolltech.qt.QNativePointer) null);
        }

        public final boolean save(QIODevice dev) {
            return save(dev, (com.trolltech.qt.QNativePointer) null);
        }

        public final boolean save(String fileName) {
            return save(fileName, (com.trolltech.qt.QNativePointer) null);
        }

        public final byte[] data() {
            QNativePointer npData = data_private();
            if (npData == null)
                return null;
            byte returned[] = new byte[size()];
            for (int i = 0; i < returned.length; ++i)
                returned[i] = npData.byteAt(i);
            return returned;
        }

}// class

class QRegion___ extends QRegion {

        public void setRects(com.trolltech.qt.core.QRect[] rects) {
            setRects(com.trolltech.qt.core.QRect.nativePointerArray(rects), rects.length);
        }

}// class

class QPolygon___ extends QPolygon {

}// class

class QPolygonF___ extends QPolygonF {

}// class

class QTextFrame_iterator___ extends QTextFrame_iterator {

        @QtBlockedSlot
        public final void next() {
            operator_increment();
        }

        @QtBlockedSlot
        public final void previous() {
            operator_decrement();
        }

}// class

class QTextBlock_iterator___ extends QTextBlock_iterator {

        @QtBlockedSlot
        public final void next() {
            operator_increment();
        }

        @QtBlockedSlot
        public final void previous() {
            operator_decrement();
        }

}// class

class QPixmap___ extends QPixmap {

        public QPixmap(String xpm[]) {
            this(com.trolltech.qt.QNativePointer.createCharPointerPointer(xpm));
        }

}// class

class QPixmapFilter___ extends QPixmapFilter {
        public final void setConvolutionKernel(double[] kernel, int rows, int columns) {
            int length = rows * columns;

            QNativePointer kernelPtr = new QNativePointer(QNativePointer.Type.Double, length);
            for (int i = 0; i < length; ++i)
                kernelPtr.setDoubleAt(i, kernel[i]);

            setConvolutionKernel_private(kernelPtr, rows, columns);
        }
}// class

class QItemSelection___ extends QItemSelection {

        public static void split(QItemSelectionRange range, QItemSelectionRange other, QItemSelection result) {
            com.trolltech.qt.QNativePointer np = result.nativePointer();
            split(range, other, np);
        }

}// class

class QPainterPath_Element___ extends QPainterPath_Element {

        @QtBlockedSlot
        public final com.trolltech.qt.core.QPointF toPoint() {
            return operator_cast_QPointF();
        }

}// class

class QBrush___ extends QBrush {

        public static final QBrush NoBrush = new QBrush(com.trolltech.qt.core.Qt.BrushStyle.NoBrush);

}// class

class QAbstractItemView___ extends QAbstractItemView {

        private java.util.Hashtable<Integer, QAbstractItemDelegate> __rcDelegatesForColumns = new java.util.Hashtable<Integer, QAbstractItemDelegate>();
        private java.util.Hashtable<Integer, QAbstractItemDelegate> __rcDelegatesForRows = new java.util.Hashtable<Integer, QAbstractItemDelegate>();

}// class

class QAccessibleTableInterface___ extends QAccessibleTableInterface {

        public static class CellAtIndex extends QTableArea {
                public CellAtIndex(int row, int column, int rowSpan, int columnSpan, boolean isSelected) {
                    super(row, column, rowSpan, columnSpan);
                    this.isSelected = isSelected;
                }

                public boolean isSelected;
        }

}// class

class QAccessibleInterface___ extends QAccessibleInterface {

        public static class Target {
                public Target(int childIndex, QAccessibleInterface target) {
                    this.childIndex = childIndex;
                    this.target = target;
                }

                public QAccessibleInterface target;
                public int childIndex;
        }

}// class

class QDesktopServices___ extends QDesktopServices {

        private static java.util.Hashtable<String, InternalUrlHandler> __rcUrlHandlers = new java.util.Hashtable<String, InternalUrlHandler>();

        public static interface UrlHandler {
            public void handleUrl(com.trolltech.qt.core.QUrl url);
        }

        private static class InternalUrlHandler extends com.trolltech.qt.core.QObject {

                private UrlHandler urlHandler;

                private InternalUrlHandler(UrlHandler urlHandler) {
                    this.urlHandler = urlHandler;
                }

                private void handleUrl(com.trolltech.qt.core.QUrl url) {
                    urlHandler.handleUrl(url);
                }

        }

        public static void setUrlHandler(String scheme, UrlHandler urlHandler) {
            InternalUrlHandler receiver = urlHandler != null ? new InternalUrlHandler(urlHandler) : null;

            if (receiver == null)
                __rcUrlHandlers.remove(scheme);
            else
                __rcUrlHandlers.put(scheme, receiver);
            setUrlHandler(scheme, receiver, QNativePointer.createCharPointer("handleUrl"));
        }

}// class

class QWizardPage___ extends QWizardPage {

        protected final void registerField(String name, QWidget widget) {
            registerField(name, widget, (com.trolltech.qt.QNativePointer) null, (com.trolltech.qt.QNativePointer) null);
        }

        protected final void registerField(String name, QWidget widget, String property) {
            registerField(name, widget, QNativePointer.createCharPointer(property), null);
        }

        /**
         * Creates a field called <code>name</code> associated with the given
         * property of the given widget. From then on, that property becomes
         * accessible using <code>field()</code> and <code>setField()</code>.
         * Fields are global to the entire wizard and make it easy for any single
         * page to access information stored by another page, without having to put
         * all the logic in <code>QWizard</code> or having the pages know
         * explicitly about each other. If name ends with an asterisk (*), the field
         * is a mandatory field. When a page has mandatory fields, the Next and/or
         * Finish buttons are enabled only when all mandatory fields are filled.
         * This requires a <code>changedSignal</code> to be specified, to tell
         * QWizard to recheck the value stored by the mandatory field.
         *
         * QWizard knows the most common Qt widgets. For these (or their
         * subclasses), you don't need to specify a <code>property</code> or a
         * <code>changedSignal</code>. The table below lists these widgets:
         *
         * <code>QAbstractButton</code> (for which the relevant property is the
         * <code>checked</code> property), <code>QAbstractSlider</code> (the
         * <code>value</code> property), <code>QComboBox</code> (<code>currentIndex</code>
         * property), <code>QDateTimeEdit</code>(<code>dateTime</code>
         * property), <code>QLineEdit</code>(<code>text</code> property),
         * <code>QListWidget</code>(<code>currentRow</code> property), or
         * <code>QSpinBox</code>(<code>value</code> property).
         *
         * @param name
         *            The name which will be used to access the field. Names ending
         *            with an asterisk signify mandatory fields.
         * @param widget
         *            The widget whose property will be accessed using this field.
         * @param property
         *            The name of the property associated with the field.
         * @param changedSignal
         *            The name of a signal which is emitted when the associated
         *            property's value changes.
         *
         * @see com.trolltech.qt.gui.QWizardPage#field
         * @see com.trolltech.qt.gui.QWizardPage#setField
         */
        protected final void registerField(String name, QWidget widget, String property, String changedSignal) {
            String signalSignature = com.trolltech.qt.internal.MetaObjectTools.cppSignalSignature(widget, changedSignal);
            if (signalSignature.length() == 0)
                throw new QNoSuchSignalException("Signal '" + changedSignal
                                                 + "' does not exist or has argument types that cannot be converted to Qt Jambi or java.lang types.");
            registerField(name, widget, com.trolltech.qt.QNativePointer.createCharPointer(property), com.trolltech.qt.QNativePointer
                          .createCharPointer(com.trolltech.qt.internal.QtJambiInternal.SignalPrefix + signalSignature));
        }

}// class

class QFontDialog___ extends QFontDialog {

        public static final class Result {
                public Result(QFont font, boolean ok) {
                    this.font = font;
                    this.ok = ok;
                }

                public QFont font;
                public boolean ok;
        }

        public static Result getFont(QFont initial, QWidget parent, String title, FontDialogOptions options) {
            QNativePointer np = new QNativePointer(QNativePointer.Type.Boolean);
            QFont returned = getFont(np, initial, parent, title, options);
            return new Result(returned, np.booleanValue());
        }

        public static Result getFont(QFont initial, QWidget parent, String caption) {
            QNativePointer np = new QNativePointer(QNativePointer.Type.Boolean);
            QFont returned = getFont(np, initial, parent, caption);
            return new Result(returned, np.booleanValue());
        }

        public static Result getFont(QWidget parent) {
            QNativePointer np = new QNativePointer(QNativePointer.Type.Boolean);
            QFont returned = getFont(np, parent);
            return new Result(returned, np.booleanValue());
        }

        public static Result getFont() {
            return getFont((QWidget) null);
        }

        public static Result getFont(QFont initial, QWidget parent) {
            QNativePointer np = new QNativePointer(QNativePointer.Type.Boolean);
            QFont returned = getFont(np, initial, parent);
            return new Result(returned, np.booleanValue());
        }

        public static Result getFont(QFont initial) {
            return getFont(initial , (QWidget) null);
        }

}// class

class QMenu___ extends QMenu {

        protected final void initStyleOption(com.trolltech.qt.gui.QStyleOptionMenuItem option, QAction action) {
            initStyleOption(option.nativePointer(), action);
        }

}// class

class QMenuBar___ extends QMenuBar {

        protected final void initStyleOption(com.trolltech.qt.gui.QStyleOptionMenuItem option, QAction action) {
            initStyleOption(option.nativePointer(), action);
        }

}// class

class QPixmapCache___ extends QPixmapCache {

        public static boolean find(String key, QPixmap pm) {
            return find(key, pm.nativePointer());
        }

}// class

class QShortcut___ extends QShortcut {

        public QShortcut(QKeySequence key, QWidget parent) {
            this(key, parent, null, null, com.trolltech.qt.core.Qt.ShortcutContext.WindowShortcut);
        }

        public QShortcut(QKeySequence key, QWidget parent, com.trolltech.qt.core.Qt.ShortcutContext context) {
            this(key, parent, null, null, context);
        }

}// class

class QValidator___ extends QValidator {

        public static class QValidationData {
                public QValidationData(String input, int pos) {
                    string = input;
                    position = pos;
                }

                public String string;
                public int position;
        }

}// class

class QAbstractButton___ extends QAbstractButton {

        /**
         * Sets the shortcut to the key sequence for the given key string. For
         * example "Ctrl+O" gives CTRL+'O'. The strings "Ctrl", "Shift", "Alt" and
         * "Meta" are recognized, as well as their translated equivalents in the
         * "QShortcut" context (using QObject::tr()). Up to four key codes may be
         * entered by separating them with commas, e.g. "Alt+X,Ctrl+S,Q".
         *
         * @param key
         *            The description of the key sequence. Typically used with tr()
         *            so key sequences can be locale aware.
         */
        public final void setShortcut(String key) {
            setShortcut(new QKeySequence(key));
        }

        /**
         * Sets the shortcut to the key sequence for the given key. The result will
         * depend on the currently running platform. The key sequence will be based
         * on the first element in the list of key bindings for the key.
         *
         * @param key
         *            The key for which to select a key sequence
         */
        public final void setShortcut(QKeySequence.StandardKey key) {
            setShortcut(new QKeySequence(key));
        }

}// class

class QStyle___ extends QStyle {

        @QtBlockedSlot
        public final int combinedLayoutSpacing(QSizePolicy.ControlTypes controls1, QSizePolicy.ControlTypes controls2,
                                               com.trolltech.qt.core.Qt.Orientation orientation, QStyleOption option, QWidget widget) {
            return combinedLayoutSpacing(controls1, controls2, orientation, option == null ? null : option.nativePointer(), widget);
        }

        @QtBlockedSlot
        public final int combinedLayoutSpacing(QSizePolicy.ControlTypes controls1, QSizePolicy.ControlTypes controls2,
                                               com.trolltech.qt.core.Qt.Orientation orientation, QStyleOption option) {
            return combinedLayoutSpacing(controls1, controls2, orientation, option, null);
        }

        @QtBlockedSlot
        public final int combinedLayoutSpacing(QSizePolicy.ControlTypes controls1, QSizePolicy.ControlTypes controls2,
                                               com.trolltech.qt.core.Qt.Orientation orientation) {
            return combinedLayoutSpacing(controls1, controls2, orientation, null);
        }

        @QtBlockedSlot
        public final int layoutSpacing(QSizePolicy.ControlType control1, QSizePolicy.ControlType control2, com.trolltech.qt.core.Qt.Orientation orientation,
                                       QStyleOption option, QWidget widget) {
            return layoutSpacing(control1, control2, orientation, option == null ? null : option.nativePointer(), widget);
        }

        @QtBlockedSlot
        public final int layoutSpacing(QSizePolicy.ControlType control1, QSizePolicy.ControlType control2, com.trolltech.qt.core.Qt.Orientation orientation,
                                       QStyleOption option) {
            return layoutSpacing(control1, control2, orientation, option, null);
        }

        @QtBlockedSlot
        public final int layoutSpacing(QSizePolicy.ControlType control1, QSizePolicy.ControlType control2, com.trolltech.qt.core.Qt.Orientation orientation) {
            return layoutSpacing(control1, control2, orientation, null);
        }

}// class

class QLayout___ extends QLayout {

        @QtBlockedSlot
        public final QContentsMargins getContentsMargins() {
            QNativePointer left = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer top = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer right = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer bottom = new QNativePointer(QNativePointer.Type.Int);

            getContentsMargins(left, top, right, bottom);
            return new QContentsMargins(left.intValue(), top.intValue(), right.intValue(), bottom.intValue());
        }

        @QtBlockedSlot
        public final void setContentsMargins(QContentsMargins margins) {
            setContentsMargins(margins.left, margins.top, margins.right, margins.bottom);
        }

}// class

class QGridLayout___ extends QGridLayout {

        public final QTableArea getItemPosition(int index) {
            QNativePointer row = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer column = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer rowSpan = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer columnSpan = new QNativePointer(QNativePointer.Type.Int);

            getItemPosition(index, row, column, rowSpan, columnSpan);

            return new QTableArea(row.intValue(), column.intValue(), rowSpan.intValue(), columnSpan.intValue());
        }

}// class

class QWidget___ extends QWidget {

        private native static void __qt_QMessageBox_setWindowTitle(long native_id, String windowTitle);

        private native static void __qt_QMessageBox_setWindowModality(long native_id, int modality);

        @QtBlockedSlot
        public final QContentsMargins getContentsMargins() {
            QNativePointer left = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer top = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer right = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer bottom = new QNativePointer(QNativePointer.Type.Int);

            getContentsMargins(left, top, right, bottom);
            return new QContentsMargins(left.intValue(), top.intValue(), right.intValue(), bottom.intValue());
        }

        @QtBlockedSlot
        public final void setContentsMargins(QContentsMargins margins) {
            setContentsMargins(margins.left, margins.top, margins.right, margins.bottom);
        }

}// class

class QFileDialog___ extends QFileDialog {

        public static class Filter {
                public Filter(String filter) {
                    this.filter = filter;
                }

                public String filter;
                public String selectedFilter = "";
        };

}// class

class QTabBar___ extends QTabBar {

        public final void initStyleOption(QStyleOptionTab option, int tabIndex) {
            initStyleOption(option.nativePointer(), tabIndex);
        }

}// class

class QClipboard___ extends QClipboard {

        public static class Text {
                public String text;
                public String subtype;
        }

        public final Text text(String subtype, Mode mode) {
            QNativePointer np = new QNativePointer(QNativePointer.Type.String);
            np.setStringValue(subtype != null ? subtype : "");

            Text returned = new Text();
            returned.text = text(np, mode);
            returned.subtype = np.stringValue();
            return returned;
        }

        public final Text text(String subtype) {
            return text(subtype, Mode.Clipboard);
        }

}// class

class QAbstractScrollArea___ extends QAbstractScrollArea {

        public QPaintEngine paintEngine() {
            throw new RuntimeException("Cannot open a painter directly on a QAbstractScrollArea, open QPainter on its viewport instead...");
        }

}// class

class QTextDocument___ extends QTextDocument {

        public final void redo(QTextCursor cursor) {
            redo(cursor.nativePointer());
        }

        public final void undo(QTextCursor cursor) {
            undo(cursor.nativePointer());
        }

}// class

class QSplitter___ extends QSplitter {

        public static class Range {
                public Range(int min, int max) {
                    minimum = min;
                    maximum = max;
                }

                public int minimum;
                public int maximum;
        }

        public final Range getRange(int index) {
            QNativePointer min = new QNativePointer(QNativePointer.Type.Int);
            QNativePointer max = new QNativePointer(QNativePointer.Type.Int);

            getRange(index, min, max);

            return new Range(min.intValue(), max.intValue());
        }

}// class

class QAction___ extends QAction {

        private QActionGroup __rcActionGroup = null;

        /**
         * Sets the shortcut to the key sequence for the given key string. For
         * example "Ctrl+O" gives CTRL+'O'. The strings "Ctrl", "Shift", "Alt" and
         * "Meta" are recognized, as well as their translated equivalents in the
         * "QShortcut" context (using QObject::tr()). Up to four key codes may be
         * entered by separating them with commas, e.g. "Alt+X,Ctrl+S,Q".
         *
         * @param key
         *            The description of the key sequence. Typically used with tr()
         *            so key sequences can be locale aware.
         */
        public final void setShortcut(String key) {
            setShortcut(new QKeySequence(key));
        }

        /**
         * Sets the shortcut to the key sequence for the given key. The result will
         * depend on the currently running platform. The key sequence will be based
         * on the first element in the list of key bindings for the key.
         *
         * @param key
         *            The key for which to select a key sequence
         */
        public final void setShortcut(QKeySequence.StandardKey key) {
            setShortcut(new QKeySequence(key));
        }

        public final void setIcon(QPixmap pm) {
            setIcon(new QIcon(pm));
        }

}// class

class QPainter___ extends QPainter {

        public final void setBrush(QColor color) {
            setBrush(new QBrush(color));
        }

        public final void setBrush(QGradient gradient) {
            setBrush(new QBrush(gradient));
        }

        public final void setBrush(QPixmap pm) {
            setBrush(new QBrush(pm));
        }

        public static QPaintDeviceInterface redirected(QPaintDeviceInterface device, com.trolltech.qt.core.QPoint offset) {
            return redirected(device, offset == null ? null : offset.nativePointer());
        }

        public QPainter(QWidget widget) {
            this();
            begin(widget);
        }

        public boolean begin(QWidget widget) { // can't be final because of QStylePainter
            return com.trolltech.qt.QtJambiGuiInternal.beginPaint(widget, this);
        }

        private static java.util.Stack<QPaintDeviceInterface> __rcRedirections = new java.util.Stack<QPaintDeviceInterface>();

}// class

class QApplication___ extends QApplication {

        public static void initialize(String args[]) {
            com.trolltech.qt.internal.HelperFunctions.setAsMainThread();

            if (m_instance != null)
                throw new RuntimeException("QApplication can only be initialized once");

            String path = Utilities.unpackPlugins();
            if (path != null)
                addLibraryPath(path);
            else
                com.trolltech.qt.internal.QtJambiInternal.setupDefaultPluginPath();
            m_instance = new QApplication(args);
            m_instance.aboutToQuit.connect(m_instance, "disposeOfMyself()");
        }

        public static void initialize(String applicationName, String args[]) {
            com.trolltech.qt.internal.HelperFunctions.setAsMainThread();

            if (m_instance != null)
                throw new RuntimeException("QApplication can only be initialized once");

            String path = Utilities.unpackPlugins();
            if (path != null)
                addLibraryPath(path);
            else
                com.trolltech.qt.internal.QtJambiInternal.setupDefaultPluginPath();
            m_instance = new QApplication(applicationName, args);
            m_instance.aboutToQuit.connect(m_instance, "disposeOfMyself()");
        }

        public static void aboutQtJambi() {
            com.trolltech.qt.QtJambiGuiInternal.aboutQtJambi();
        }

        public static QApplication instance() {
            if (type() != Type.Tty)
                return (QApplication) com.trolltech.qt.core.QCoreApplication.instance();
            return null;
        }

        public QApplication(String args[]) {
            this(argc(args), argv(args));
        }

        public QApplication(String applicationName, String args[]) {
            this(argc(args), argv(applicationName, args));
        }

        public static void setFont(QFont font) {
            setFont(font, null);
        }

        public static void setPalette(QPalette palette) {
            setPalette(palette, null);
        }

        public static QCursor overrideCursor() {
            QNativePointer np = overrideCursor_private();
            return np == null ? null : QCursor.fromNativePointer(np);
        }

}// class

class QFormLayout___ extends QFormLayout {
        /**
         * Retrieves the row and role (column) of the item at the specified index.
         * If index is out of bounds, -1 is returned in place of the row.
         *
         * @param index The index of the item for which to retrieve the position.
         * @return A pair of an int (row of item) and an ItemRole (role of the item.)
         **/
        @QtBlockedSlot
        public final com.trolltech.qt.QPair<Integer, ItemRole> getItemPosition(int index) {
            com.trolltech.qt.QNativePointer row = new com.trolltech.qt.QNativePointer(com.trolltech.qt.QNativePointer.Type.Int);
            com.trolltech.qt.QNativePointer role = new com.trolltech.qt.QNativePointer(com.trolltech.qt.QNativePointer.Type.Int);

            getItemPosition(index, row, role);

            return new com.trolltech.qt.QPair<Integer, ItemRole>(row.intValue(), ItemRole.resolve(role.intValue()));
        }

        /**
         * Retrieves the row and role (column) of the specified child layout.
         * If the layout is not in the form layout, -1 is returned in place of the row.
         *
         * @param layout The layout for which to retrieve the position.
         * @return A pair of an int (row of item) and an ItemRole (role of the item.)
         */
        @QtBlockedSlot
        public final com.trolltech.qt.QPair<Integer, ItemRole> getLayoutPosition(QLayout layout) {
            com.trolltech.qt.QNativePointer row = new com.trolltech.qt.QNativePointer(com.trolltech.qt.QNativePointer.Type.Int);
            com.trolltech.qt.QNativePointer role = new com.trolltech.qt.QNativePointer(com.trolltech.qt.QNativePointer.Type.Int);

            getLayoutPosition(layout, row, role);

            return new com.trolltech.qt.QPair<Integer, ItemRole>(row.intValue(), ItemRole.resolve(role.intValue()));
        }

        /**
         * Retrieves the row and role (column) of the specified widget in the layout.
         * If the widget is not in the form layout, -1 is returned in place of the row.
         *
         * @param widget The widget for which to retrieve the position.
         * @return A pair of an int (row of item) and an ItemRole (role of the item.)
         */
        @QtBlockedSlot
        public final com.trolltech.qt.QPair<Integer, ItemRole> getWidgetPosition(QWidget widget) {
            com.trolltech.qt.QNativePointer row = new com.trolltech.qt.QNativePointer(com.trolltech.qt.QNativePointer.Type.Int);
            com.trolltech.qt.QNativePointer role = new com.trolltech.qt.QNativePointer(com.trolltech.qt.QNativePointer.Type.Int);

            getWidgetPosition(widget, row, role);

            return new com.trolltech.qt.QPair<Integer, ItemRole>(row.intValue(), ItemRole.resolve(role.intValue()));
        }

        /**
         * Sets the item in the given row for the given role to item, extending the layout with empty rows if necessary.
         * If the cell is already occupied, the item is not inserted and a IllegalArgumentException is thrown.
         */
        @QtBlockedSlot
        public final void setItem(int row, ItemRole role, QLayoutItem item) {
            if (itemAt(row, role) == null)
                setItem_private(row, role, item);
            else
                throw new IllegalArgumentException("Cell in form layout is already occupied");
        }

}// class

class Subclass_of_QGraphicsLayoutItem___ extends QGraphicsLayoutItem {
        @QtBlockedSlot
        public final QMarginsF getContentsMargins() {
            QNativePointer left = new QNativePointer(QNativePointer.Type.Double);
            QNativePointer top = new QNativePointer(QNativePointer.Type.Double);
            QNativePointer right = new QNativePointer(QNativePointer.Type.Double);
            QNativePointer bottom = new QNativePointer(QNativePointer.Type.Double);

            getContentsMargins(left, top, right, bottom);
            return new QMarginsF(left.doubleValue(), top.doubleValue(), right.doubleValue(), bottom.doubleValue());
        }
}// class

class QGraphicsWidget___ extends QGraphicsWidget {

        /**
         * Gets the widget's window frame margins.
         *
         * @returns An object containing the margins in left, right, top and bottom coordinates.
         */
        @QtBlockedSlot
        public final QMarginsF getWindowFrameMargins() {
            QNativePointer left = new QNativePointer(QNativePointer.Type.Double);
            QNativePointer top = new QNativePointer(QNativePointer.Type.Double);
            QNativePointer right = new QNativePointer(QNativePointer.Type.Double);
            QNativePointer bottom = new QNativePointer(QNativePointer.Type.Double);

            getWindowFrameMargins(left, top, right, bottom);
            return new QMarginsF(left.doubleValue(), top.doubleValue(), right.doubleValue(), bottom.doubleValue());
        }

}// class

class QPrinter___ extends QPrinter {

        /**
         * Returns the page margins for this printer.
         * The unit of the returned margins are specified with the unit parameter.
         * This function was introduced in Qt 4.4.
         *
         * @param unit Specifies the unit of the returned margins.
         * @return The page margins for this printer.
         */
        public final QMarginsF getPageMargins(QPrinter.Unit unit) {
            QNativePointer left = new QNativePointer(QNativePointer.Type.Double);
            QNativePointer top = new QNativePointer(QNativePointer.Type.Double);
            QNativePointer right = new QNativePointer(QNativePointer.Type.Double);
            QNativePointer bottom = new QNativePointer(QNativePointer.Type.Double);

            getPageMargins(left, top, right, bottom, unit);
            return new QMarginsF(left.doubleValue(), top.doubleValue(), right.doubleValue(), bottom.doubleValue());
        }

}// class

class QListWidget___ extends QListWidget {

        /**
         * Inserts an item with the text label at the end of the list widget.
         *
         * @param label The text of the new item.
         */
        public final void addItem(String label) {
            addItem(new QListWidgetItem(label, this));
        }

        /**
         * Inserts items with the text labels at the end of the list widget.
         *
         * @param labels The collection of labels for the new items.
         */
        public final void addItems(java.util.List<String> labels) {
        for (String label : labels)
                addItem(label);
        }

        /**
         * This is an overloaded member function, provided for convenience.
         * Inserts an item with the text label in the list widget at the position given by row.
         *
         * @param row The position in which to insert the new item.
         * @param label The text of the new item.
         */
        public final void insertItem(int row, String label) {
            insertItem(row, new QListWidgetItem(label, this));
        }

        /**
         * Inserts items from the list of labels into the list, starting at the given row.
         *
         * @param row The position in which to insert the new items.
         * @param labels The collection of labels for the new items.
         */
        public final void insertItems(int row, java.util.List<String> labels) {
        for (String label : labels)
                insertItem(row++, label);
        }
}// class


class QGraphicsScene___ extends QGraphicsScene {
        /**
         * Equivalent to addEllipse(rect, null, null)
         */
        public final QGraphicsEllipseItem addEllipse(com.trolltech.qt.core.QRectF rect) {
            return addEllipse(rect, null);
        }

        /**
         * Equivalent to addEllipse(rect, pen, null)
         */
        public final QGraphicsEllipseItem addEllipse(com.trolltech.qt.core.QRectF rect, QPen pen) {
            return addEllipse(rect, pen, null);
        }


        /**
         * Creates and adds an ellipse item to the scene, and returns the item
         * reference. The geometry of the ellipse is defined by rect, and its pen
         * and brush are initialized to pen and brush.
         *
         * Note that the item's geometry is provided in item coordinates, and its
         * position is initialized to (0, 0).
         *
         * If the item is visible (i.e., QGraphicsItem::isVisible() returns true),
         * QGraphicsScene will emit changed() once control goes back to the event
         * loop.
         *
         * @param rect The bounding rectangle of the ellipse.
         * @param pen The pen for the resulting QGraphicsEllipseItem.
         * @param brush The brush for the resulting QGraphicsEllipseItem.
         * @return The resulting item.
         */
        public final QGraphicsEllipseItem addEllipse(com.trolltech.qt.core.QRectF rect, QPen pen, QBrush brush) {
            return addEllipse(rect.x(), rect.y(), rect.width(), rect.height(), pen, brush);
        }

        /**
         * Equivalent to addEllipse(x, y, w, h, null, null)
         */
        public final QGraphicsEllipseItem addEllipse(double x, double y, double w, double h) {
            return addEllipse(x, y, w, h, null);
        }

        /**
         * Equivalent to addEllipse(x, y, w, h, pen, null)
         */
        public final QGraphicsEllipseItem addEllipse(double x, double y, double w, double h, QPen pen) {
            return addEllipse(x, y, w, h, pen, null);
        }

        /**
         * Creates and adds an ellipse item to the scene, and returns the item
         * reference. The geometry of the ellipse is defined by x, y, w, h, and its pen
         * and brush are initialized to pen and brush.
         *
         * Note that the item's geometry is provided in item coordinates, and its
         * position is initialized to (0, 0).
         *
         * If the item is visible (i.e., QGraphicsItem.isVisible() returns true),
         * QGraphicsScene will emit changed once control goes back to the event
         * loop.
         *
         * @param x The left horizontal coordinate of the ellipse's bounding rectangle.
         * @param y The top vertical coordinate of the ellipse's bounding rectangle.
         * @param w The width of the ellipse's bounding rectangle.
         * @param h The height of the ellipse's bounding rectangle.
         * @param pen The pen for the resulting QGraphicsEllipseItem.
         * @param brush The brush for the resulting QGraphicsEllipseItem.
         * @return The resulting item.
         */
        public final QGraphicsEllipseItem addEllipse(double x, double y, double w, double h, QPen pen, QBrush brush) {
            QGraphicsEllipseItem item = new QGraphicsEllipseItem(x, y, w, h);
            item.setPen(pen);
            item.setBrush(brush);

            addItem(item);

            return item;
        }

        /**
         * Equivalent to addLine(line, null)
         */
        public final QGraphicsLineItem addLine(QLineF line) {
            return addLine(line, null);
        }

        /**
         * Creates and adds a line item to the scene, and returns the item
         * reference. The geometry of the line is defined by line, and its pen
         * is initialized to pen.
         *
         * Note that the item's geometry is provided in item coordinates, and its
         * position is initialized to (0, 0).
         *
         * If the item is visible (i.e., QGraphicsItem.isVisible() returns true),
         * QGraphicsScene will emit changed once control goes back to the event
         * loop.
         *
         * @param line The definition of the line.
         * @param pen The pen with which to draw the line.
         * @return The resulting item.
         */
        public final QGraphicsLineItem addLine(QLineF line, QPen pen) {
            return addLine(line.x1(), line.y1(), line.x2(), line.y2(), pen);
        }

        /**
         * Equivalent to addLine(x1, y1, x2, y2, null)
         */
        public final QGraphicsLineItem addLine(double x1, double y1, double x2, double y2) {
            return addLine(x1, y1, x2, y2, null);
        }

        /**
         * Creates and adds a line item to the scene, and returns the item
         * reference. The geometry of the line is defined by x1, y1, x2, y2, and its pen
         * is initialized to pen.
         *
         * Note that the item's geometry is provided in item coordinates, and its
         * position is initialized to (0, 0).
         *
         * If the item is visible (i.e., QGraphicsItem.isVisible() returns true),
         * QGraphicsScene will emit changed once control goes back to the event
         * loop.
         *
         * @param x1 The first horizontal coordinate of the line.
         * @param y1 The first vertical coordinate of the line.
         * @param x2 The second horizontal coordinate of the line.
         * @param y2 The second vertical coordinate of the line.
         * @param pen The pen with which to draw the line.
         * @return The resulting item.
         */
        public final QGraphicsLineItem addLine(double x1, double y1, double x2, double y2, QPen pen) {
            QGraphicsLineItem item = new QGraphicsLineItem(x1, y1, x2, y2);
            item.setPen(pen);

            addItem(item);

            return item;
        }


        /**
         * Equivalent to addPath(path, null, null)
         */
        public final QGraphicsPathItem addPath(QPainterPath path) {
            return addPath(path, null);
        }

        /**
         * Equivalent to addPath(path, pen, null)
         */
        public final QGraphicsPathItem addPath(QPainterPath path, QPen pen) {
            return addPath(path, pen, null);
        }
        /**
         * Creates and adds a path item to the scene, and returns the item
         * reference. The geometry of the path is defined by path, and its pen and
         * brush are initialized to pen and brush.
         *
         * Note that the item's geometry is provided in item coordinates, and its
         * position is initialized to (0, 0).
         *
         * If the item is visible (i.e., QGraphicsItem.isVisible() returns true),
         * QGraphicsScene will emit changed once control goes back to the event
         * loop.
         *
         * @param path The definition of the path.
         * @param pen The pen for drawing the path.
         * @param brush The brush for drawing the path.
         * @return The resulting item.
         */
        public final QGraphicsPathItem addPath(QPainterPath path, QPen pen, QBrush brush) {
            QGraphicsPathItem item = new QGraphicsPathItem(path);
            item.setPen(pen);
            item.setBrush(brush);

            addItem(item);

            return item;
        }

        /**
         * Creates and adds a pixmap item to the scene, and returns the item
         * reference. The pixmap is defined by pixmap.
         *
         * Note that the item's geometry is provided in item coordinates, and its
         * position is initialized to (0, 0).
         *
         * If the item is visible (i.e., QGraphicsItem.isVisible() returns true),
         * QGraphicsScene will emit changed once control goes back to the event
         * loop.
         *
         * @param pixmap The pixmap for which to create a graphics item.
         * @return The resulting item.
         */
        public final QGraphicsPixmapItem addPixmap(QPixmap pixmap) {
            QGraphicsPixmapItem item = new QGraphicsPixmapItem(pixmap);

            addItem(item);

            return item;
        }

        /**
         * Equivalent to addPolygon(polygon, null, null)
         */
        public final QGraphicsPolygonItem addPolygon(QPolygonF polygon) {
            return addPolygon(polygon, null);
        }

        /**
         * Equivalent to addPolygon(polygon, pen, null)
         */
        public final QGraphicsPolygonItem addPolygon(QPolygonF polygon, QPen pen) {
            return addPolygon(polygon, pen, null);
        }

        /**
         * Creates and adds a polygon item to the scene, and returns the item
         * reference. The polygon is defined by polygon, and its pen and
         * brush are initialized to pen and brush.
         *
         * Note that the item's geometry is provided in item coordinates, and its
         * position is initialized to (0, 0).
         *
         * If the item is visible (i.e., QGraphicsItem.isVisible() returns true),
         * QGraphicsScene will emit changed once control goes back to the event
         * loop.
         *
         * @param polygon The definition of the polygon.
         * @param pen The pen with which to draw the polygon.
         * @param brush The brush with which to draw the polygon.
         * @return The resulting item.
         */
        public final QGraphicsPolygonItem addPolygon(QPolygonF polygon, QPen pen, QBrush brush) {
            QGraphicsPolygonItem item = new QGraphicsPolygonItem(polygon);
            item.setPen(pen);
            item.setBrush(brush);

            addItem(item);

            return item;
        }

        /**
         * Equivalent to addRect(rect, null, null)
         */
        public final QGraphicsRectItem addRect(com.trolltech.qt.core.QRectF rect) {
            return addRect(rect, null);
        }

        /**
         * Equivalent to addRect(rect, pen, null)
         */
        public final QGraphicsRectItem addRect(com.trolltech.qt.core.QRectF rect, QPen pen) {
            return addRect(rect, pen, null);
        }

        /**
         * Creates and adds a rectangle item to the scene, and returns the item
         * reference. The geometry of the rectangle is defined by rect, and its pen
         * and brush are initialized to pen and brush.
         *
         * Note that the item's geometry is provided in item coordinates, and its
         * position is initialized to (0, 0). For example, if a QRect(50, 50, 100,
         * 100) is added, its top-left corner will be at (50, 50) relative to the
         * origin in the item's coordinate system.
         *
         * If the item is visible (i.e., QGraphicsItem.isVisible() returns true),
         * QGraphicsScene will emit changed once control goes back to the event
         * loop.
         *
         * @param rect The definition of the rectangle.
         * @param pen The pen with which to draw the rectangle.
         * @param brush The brush with which to draw the rectangle.
         * @return The resulting item.
         */
        public final QGraphicsRectItem addRect(com.trolltech.qt.core.QRectF rect, QPen pen, QBrush brush) {
            return addRect(rect.x(), rect.y(), rect.width(), rect.height(), pen, brush);
        }

        /**
         * Equivalent to addRect(x, y, w, h, null, null)
         */
        public final QGraphicsRectItem addRect(double x, double y, double w, double h) {
            return addRect(x, y, w, h, null);
        }

        /**
         * Equivalent to addRect(x, y, w, h, pen, null)
         */
        public final QGraphicsRectItem addRect(double x, double y, double w, double h, QPen pen) {
            return addRect(x, y, w, h, pen, null);
        }

        /**
         * Creates and adds a rectangle item to the scene, and returns the item
         * reference. The geometry of the rectangle is defined by x, y, w, h, and its pen
         * and brush are initialized to pen and brush.
         *
         * Note that the item's geometry is provided in item coordinates, and its
         * position is initialized to (0, 0).
         *
         * If the item is visible (i.e., QGraphicsItem.isVisible() returns true),
         * QGraphicsScene will emit changed once control goes back to the event
         * loop.
         *
         * @param x The left horizontal coordinate of the rectangle.
         * @param y The top vertical coordinate of the rectangle.
         * @param w The width of the rectangle.
         * @param h The height of the rectangle.
         * @param pen The pen with which to draw the rectangle.
         * @param brush The brush with which to draw the rectangle.
         * @return The resulting item.
         */
        public final QGraphicsRectItem addRect(double x, double y, double w, double h, QPen pen, QBrush brush) {
            QGraphicsRectItem item = new QGraphicsRectItem(x, y, w, h);
            item.setPen(pen);
            item.setBrush(brush);

            addItem(item);

            return item;
        }

        /**
         * Equivalent to addSimpleText(text, null)
         */
        public final QGraphicsSimpleTextItem addSimpleText(String text) {
            return addSimpleText(text, null);
        }

        /**
         * Creates and adds a QGraphicsSimpleTextItem to the scene, and returns the
         * item reference. The text string is initialized to text, and its font is
         * initialized to font.
         *
         * The item's position is initialized to (0, 0).
         *
         * If the item is visible (i.e., QGraphicsItem.isVisible() returns true),
         * QGraphicsScene will emit changed once control goes back to the event
         * loop.
         *
         * @param text The text to add to the scene.
         * @param font The font to use for displaying the text.
         * @return The resulting item.
         */
        public final QGraphicsSimpleTextItem addSimpleText(String text, QFont font) {
            QGraphicsSimpleTextItem item = new QGraphicsSimpleTextItem(text);
            item.setFont(font);

            addItem(item);

            return item;
        }

        /**
         * Equivalent to addText(text, null)
         */
        public final QGraphicsTextItem addText(String text) {
            return addText(text, null);
        }

        /**
         * Creates and adds a text item to the scene, and returns the item
         * reference. The text string is initialized to text, and its font
         * is initialized to font.
         *
         * The item's position is initialized to (0, 0).
         *
         * If the item is visible (i.e., QGraphicsItem.isVisible() returns true),
         * QGraphicsScene will emit changed once control goes back to the event
         * loop.
         *
         * @param text The text to add to the scene.
         * @param font The font to use for displaying the text.
         * @return The resulting item.
         */
        public final QGraphicsTextItem addText(String text, QFont font) {
            QGraphicsTextItem item = new QGraphicsTextItem(text);
            item.setFont(font);

            addItem(item);

            return item;
        }

}// class

class QAbstractTextDocumentLayout___ extends QAbstractTextDocumentLayout {

        // Injected methods
        public final QTextObjectInterface handlerForObject(int objectType) {
            com.trolltech.qt.GeneratorUtilities.threadCheck(this);
            if (nativeId() == 0)
                throw new QNoNativeResourcesException("Function call on incomplete object of type: " + getClass().getName());
            return __qt_handlerForObject(nativeId(), objectType);
        }
        native QTextObjectInterface __qt_handlerForObject(long __this__nativeId, int objectType);

        /**
         * Registers the given component as a handler for items of the given objectType.
         * Note: registerHandler() has to be called once for each object type. This means that there is only one handler for multiple replacement characters of the same object type.
         *
         * @param objectType The type for which to register a handler.
         * @param component The handler for the given type.
         **/
        @SuppressWarnings("deprecated")
        public final void registerHandler(int objectType, QTextObjectInterface component) {
            registerHandler(objectType, (com.trolltech.qt.core.QObject) component);
        }

}// class
