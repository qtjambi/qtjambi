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

package com.trolltech.launcher;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class ScrollingHTMLView extends QWidget {

    private int m_y_offset = 0;
    private QTextDocument m_document = new QTextDocument();
    private QPoint m_mouse_pos;

    private QPixmap topFade = new QPixmap(1, 32);
    private QPixmap bottomFade = new QPixmap(1, 32);
    private QPixmap rightFade = new QPixmap(32, 1);

    private QPixmap background;
    
    private boolean wrap = true;
    private int margine = 200;

    public ScrollingHTMLView() {
        this(null);
    }

    public ScrollingHTMLView(QWidget parent) {
        super(parent);
        setAutoFillBackground(false);

        {
            topFade.fill(new QColor(0, 0, 0, 0));
            QPainter p = new QPainter();
            p.begin(topFade);
            QLinearGradient lg = new QLinearGradient(0, 0, 0, topFade.height());
            lg.setColorAt(0, new QColor(255, 255, 255));
            lg.setColorAt(1, new QColor(0, 0, 0, 0));
            p.fillRect(topFade.rect(), new QBrush(lg));
            p.end();
        }
        {
            bottomFade.fill(new QColor(0, 0, 0, 0));
            QPainter p = new QPainter();
            p.begin(bottomFade);
            QLinearGradient lg = new QLinearGradient(0, bottomFade.height(), 0, 0);
            lg.setColorAt(0, new QColor(255, 255, 255));
            lg.setColorAt(1, new QColor(0, 0, 0, 0));
            p.fillRect(bottomFade.rect(), new QBrush(lg));
            p.end();
        }
        {
            rightFade.fill(new QColor(0, 0, 0, 0));
            QPainter p = new QPainter();
            p.begin(rightFade);
            QLinearGradient lg = new QLinearGradient(rightFade.width(), 0, 0, 0);
            lg.setColorAt(0, new QColor(255, 255, 255));
            lg.setColorAt(1, new QColor(0, 0, 0, 0));
            p.fillRect(rightFade.rect(), new QBrush(lg));
            p.end();
        }
    }


    public void setHtml(String html) {
        if (html == null)
            return;
        m_document.setHtml(html);
        resetYOffset();
        update();
    }

    public void setWordWrap(boolean wrap) {
        this.wrap = wrap;
        m_document.setTextWidth(wrap ? width() - margine : -1);
    }

    @Override
    protected void paintEvent(QPaintEvent e) {
        int w = width(), h = height();
        double margin = 0.1;

        QPainter p = new QPainter();
        p.begin(this);

        if (w > background.width() || h > background.height() || background == null)
            p.fillRect(rect(), new QBrush(QColor.white));
        p.drawPixmap(w / 2 - background.width() / 2, h / 2 - background.height() / 2, background);

        QRectF textRect = new QRectF(w * margin, 0, w * (1 - margin), h);
        p.setPen(QPen.NoPen);

        int ypos = m_y_offset;

        p.translate(textRect.x(), ypos);

        QAbstractTextDocumentLayout_PaintContext ctx = new QAbstractTextDocumentLayout_PaintContext();
        ctx.setPalette(palette());

        ctx.setClip(new QRectF(0, -ypos, w - 200, h));
        m_document.documentLayout().draw(p, ctx);

        p.resetMatrix();
        p.drawTiledPixmap(0, 0, width(), topFade.height(), topFade);
        p.drawTiledPixmap(0, height() - bottomFade.height(), width(), bottomFade.height(),
                          bottomFade);
        p.drawTiledPixmap(width() - rightFade.width(), 0, rightFade.width(), height(), rightFade);  
        p.end();
    }

    @Override
    protected void wheelEvent(QWheelEvent e) {
        setYOffset(m_y_offset + e.delta() / 3);
    }

    @Override
    protected void mousePressEvent(QMouseEvent e) {
        m_mouse_pos = e.pos();
    }

    @Override
    protected void mouseMoveEvent(QMouseEvent e) {
        setYOffset(m_y_offset + e.y() - m_mouse_pos.y());
        m_mouse_pos = e.pos();
    }

    @Override
    public QSize sizeHint() {
        return new QSize(500, 400);
    }

    public QPixmap getBackground() {
        return background;
    }

    public void setBackground(QPixmap background) {
        this.background = background;
    }

    @Override
    protected void resizeEvent(QResizeEvent e) {
        m_document.setTextWidth(wrap ? e.size().width() - margine : -1);
    }

    private static final int OFFSET_BASE = 32;

    private void resetYOffset() {
        setYOffset(OFFSET_BASE);
    }

    private void setYOffset(int pos) {
        int offset_limit = (int)(-m_document.documentLayout().documentSize().height() + height()) -  OFFSET_BASE;
        pos = Math.min(OFFSET_BASE, Math.max(offset_limit, pos));

        m_y_offset = pos;
        update();
    }

    public static void main(String args[]) {
        QApplication.initialize(args);

        ScrollingHTMLView view = new ScrollingHTMLView(null);
        view.show();

        QApplication.exec();
    }
}
