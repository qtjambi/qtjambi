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

public class Delegate extends QItemDelegate {

    public static final int MARGE_HOR = 20; // Extra pixels on each side of text...
    public static final int MARGE_VER = 6;

    public Delegate(LaunchableListModel model) {
	m_model = model;
    }

    private static String stripName(String s) {
	String[] tmp = s.split("\\.");
	return tmp[tmp.length - 1];
    }

    private final boolean isFirst(QModelIndex index) {
	return index.row() == 0;
    }

    private final boolean isLast(QModelIndex index) {
        return index.row() == m_model.size() - 1;
    }

    public void paint(QPainter p, QStyleOptionViewItem option, QModelIndex index) {
        Launchable l = m_model.at(index);
        String text = stripName(l.name());
        boolean selected = (option.state().isSet(QStyle.StateFlag.State_Selected));
        QRectF rect = new QRectF(option.rect());

        drawBackground(p, rect, index);

        if (selected) {
            Style.drawShadeButton(p, rect, new QStyle.State(QStyle.StateFlag.State_Enabled));
            Style.drawButtonOutline(p, rect, new QStyle.State(Style.StateFlag.State_Enabled));
        }

        Style.drawShadowText(p, rect.translated(0, -1), text, 2, 2);
    }


    public QSize sizeHint(QStyleOptionViewItem option, QModelIndex index) {
	Launchable l = m_model.at(index);

	String text = stripName(l.name());

	QRect rect = option.fontMetrics().boundingRect(text);

	return new QSize(rect.width() + MARGE_HOR * 2,
			 rect.height() + MARGE_VER * 2);
    }

    /**
     * Draws the rounded edges of the background based on the index of the
     * item..
     */
    private final void drawBackground(QPainter p, QRectF rect, QModelIndex index) {
	QPainterPath fill = null;
	QPainterPath outline = null;
	if (isFirst(index)) {
	    double dim = Style.ROUND;

	    outline = new QPainterPath();
	    outline.moveTo(rect.left()+1, rect.bottom());
	    outline.arcTo(rect.left()+1, rect.top()+1, dim, dim, 180, -90);
	    outline.arcTo(rect.right() - dim-1, rect.top()+1, dim, dim, 90, -90);
	    outline.lineTo(rect.right()-1, rect.bottom());

	    fill = outline;

	} else if (isLast(index)) {
	    double dim = Style.ROUND;

	    outline = new QPainterPath();
	    outline.moveTo(rect.left()+1, rect.top());
	    outline.arcTo(rect.left()+1, rect.bottom() - dim-1, dim, dim, 180, 90);
	    outline.arcTo(rect.right() - dim-1, rect.bottom() - dim-1, dim, dim, 270, 90);
	    outline.lineTo(rect.right()-1, rect.top());

	    fill = outline;
	} else {
	    outline = new QPainterPath();
	    outline.moveTo(rect.left()+1, rect.top());
	    outline.lineTo(rect.left()+1, rect.bottom());
	    outline.moveTo(rect.right()-1, rect.top());
	    outline.lineTo(rect.right()-1, rect.bottom());

	    fill = new QPainterPath();
	    fill.addRect(rect);
	}
	p.setRenderHint(QPainter.RenderHint.Antialiasing);

	p.fillPath(fill, new QBrush(Style.TT_BG_GREEN_LIGHT));
	p.strokePath(outline, Style.PEN_THICK_GREEN);
    }

    // Member variables...
    private LaunchableListModel m_model;
}
