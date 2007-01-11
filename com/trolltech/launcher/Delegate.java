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
        super(model);
        m_model = model;
    }

    private static String stripName(String s) {
        String[] tmp = s.split("\\.");
        return tmp[tmp.length - 1];
    }

    public void paint(QPainter p, QStyleOptionViewItem option, QModelIndex index) {
        Launchable l = m_model.at(index);
        String text = stripName(l.name());
        boolean selected = (option.state().isSet(QStyle.StateFlag.State_Selected));
        QRectF rect = new QRectF(option.rect());
        rect.adjust(3, 1, -3, -1);
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

        return new QSize(rect.width() + MARGE_HOR * 2, rect.height() + MARGE_VER * 2);
    }

    // Member variables...
    private LaunchableListModel m_model;
}
