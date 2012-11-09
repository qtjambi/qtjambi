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

package com.trolltech.launcher;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.qreal.QReal;

public class Style extends QWindowsStyle {
    public static QLinearGradient GRADIENT_LIGHT = null;
    public static QLinearGradient GRADIENT_DARK = null;

    public static final QColor TROLLTECH_GREEN = new QColor(165, 205, 57);
    public static final QColor TROLLTECH_LIGHT = new QColor(210, 230, 156);
    public static final QColor COLOR_GRAY = new QColor(127, 127, 127);

    public static final QColor TT_BG_GREEN_DARK = new QColor(210, 230, 156);
    public static final QColor TT_BG_GREEN_LIGHT = new QColor(228, 255, 203);

    public static QBrush BRUSH_GREEN = new QBrush(TROLLTECH_GREEN);
    public static QBrush BRUSH_LIGHT_GREEN = new QBrush(TT_BG_GREEN_LIGHT);

    public static QPen PEN_THICK_GREEN = new QPen(BRUSH_GREEN, 2);
    public static QPen PEN_THICK_LIGHT_GREEN = new QPen(BRUSH_LIGHT_GREEN, 2);
    public static QPen PEN_BLACK = new QPen(new QColor(com.trolltech.qt.core.Qt.GlobalColor.black), 0);
    public static QPen PEN_GRAY = new QPen(COLOR_GRAY, 0);

    public static final double GROUPBOX_LINE_THICKNESS = 2;
    public static final int ROUND = 20;
    public static final int TEXT_PADDING = 5;
    public static final int H_TEXT_PADDING = 20;

    public static final int DOT_TO_TEXT_PADDING = 4;

    public static QPainterPath UP_INDICATOR;
    public static QPainterPath DOWN_INDICATOR;

    static {
        GRADIENT_LIGHT = new QLinearGradient(0, 0, 0, 1);
        GRADIENT_LIGHT.setColorAt(0, TT_BG_GREEN_DARK);
        GRADIENT_LIGHT.setColorAt(QReal.valueOf(0.25).platformValue(), new QColor(com.trolltech.qt.core.Qt.GlobalColor.white));
        GRADIENT_LIGHT.setColorAt(QReal.valueOf(0.5).platformValue(), TT_BG_GREEN_LIGHT);
        GRADIENT_LIGHT.setColorAt(1, TT_BG_GREEN_DARK);

        GRADIENT_DARK = new QLinearGradient(0, 0, 0, 1);
        GRADIENT_DARK.setColorAt(0, new QColor(com.trolltech.qt.core.Qt.GlobalColor.white));
        GRADIENT_DARK.setColorAt(QReal.valueOf(0.25).platformValue(), TT_BG_GREEN_DARK);
        GRADIENT_DARK.setColorAt(QReal.valueOf(0.5).platformValue(), TT_BG_GREEN_DARK);
        GRADIENT_DARK.setColorAt(1, TT_BG_GREEN_LIGHT);
    }

    public Style(QObject parent) {
        setParent(parent);

        UP_INDICATOR = new QPainterPath();
        UP_INDICATOR.moveTo(0, -3);
        UP_INDICATOR.lineTo(QReal.valueOf(4.5).platformValue(), 2);
        UP_INDICATOR.lineTo(QReal.valueOf(-4.5).platformValue(), 2);
        UP_INDICATOR.closeSubpath();

        DOWN_INDICATOR = new QPainterPath();
        DOWN_INDICATOR.moveTo(0, 3);
        DOWN_INDICATOR.lineTo(QReal.valueOf(4.5).platformValue(), -2);
        DOWN_INDICATOR.lineTo(QReal.valueOf(-4.5).platformValue(), -2);
        DOWN_INDICATOR.closeSubpath();
    }

    public static boolean isOn(QStyle.State state) {
        return state.isSet(QStyle.StateFlag.State_On);
    }

    public static boolean isSunken(QStyle.State state) {
        return state.isSet(QStyle.StateFlag.State_Sunken);
    }

    public static boolean isEnabled(QStyle.State state) {
        return state.isSet(QStyle.StateFlag.State_Enabled);
    }

    @Override
    public void drawComplexControl(QStyle.ComplexControl cc, QStyleOptionComplex opt, QPainter p,
            QWidget widget) {

        switch (cc) {
        case CC_GroupBox:
            drawGroupBox((QStyleOptionGroupBox) opt, p, widget);
            break;
        default:
            super.drawComplexControl(cc, opt, p, widget);
            break;
        }
    }

    @Override
    public void drawControl(QStyle.ControlElement ce, QStyleOption opt, QPainter p, QWidget widget) {
        switch (ce) {
        case CE_RadioButton:
            drawRadioButton((QStyleOptionButton) opt, p);
            break;
        case CE_PushButton:
            drawPushButton((QStyleOptionButton) opt, p);
            break;
        case CE_ScrollBarSlider:
            drawScrollBarSlider((QStyleOptionSlider)opt, p);
            break;
        case CE_ScrollBarAddLine :
        case CE_ScrollBarSubLine :
            drawScrollBarLine((QStyleOptionSlider)opt, p, ce);
            break;
        default:
            super.drawControl(ce, opt, p, widget);
            break;
        }
    }


    private QRectF groupBoxLabelRect(QRect rect, QWidget w, String label) {
        if (w == null)
            return new QRectF(rect);

        QFontMetrics metrics = new QFontMetrics(w.font());
        QRectF text_rect = new QRectF(metrics.boundingRect(ROUND, 0, rect.width() - ROUND * 2,
                rect.height(), Qt.AlignmentFlag.AlignTop.value() | Qt.AlignmentFlag.AlignHCenter.value(),
                label));

        text_rect.adjust(QReal.valueOf(-H_TEXT_PADDING).platformValue(), QReal.valueOf(GROUPBOX_LINE_THICKNESS / 2.0).platformValue(), QReal.valueOf(TEXT_PADDING * 2 + H_TEXT_PADDING).platformValue(),
                QReal.valueOf(TEXT_PADDING * 2).platformValue());

        return text_rect;
    }

    @Override
    public QRect subControlRect(ComplexControl cc, QStyleOptionComplex opt, int sc, QWidget w) {

        if (cc == ComplexControl.CC_GroupBox && sc == SubControl.SC_GroupBoxLabel) {
            QStyleOptionGroupBox sogb = (QStyleOptionGroupBox) opt;
            String title = sogb.text();
            QRect rect = opt.rect();
            return groupBoxLabelRect(rect, w, title).toRect();
        } else if (cc == ComplexControl.CC_GroupBox && sc == SubControl.SC_GroupBoxContents) {
            QStyleOptionGroupBox sogb = (QStyleOptionGroupBox) opt;
            String title = sogb.text();
            QRectF label = groupBoxLabelRect(opt.rect(), w, title);
            return opt.rect().adjusted(0, title.length() > 0 ? (int) label.height() : 0, 0, 0);
        }
        return super.subControlRect(cc, opt, sc, w);
    }

    @Override
    public QRect subElementRect(SubElement se, QStyleOption opt, QWidget w) {

        if (se == SubElement.SE_CheckBoxFocusRect
                    || se == SubElement.SE_CheckBoxClickRect
                    || se == SubElement.SE_RadioButtonFocusRect
                    || se == SubElement.SE_RadioButtonClickRect)
                    return opt.rect();
                return super.subElementRect(se, opt, w);
        }

    private QRectF groupBoxContentsRect(QRect rect, QStyleOptionGroupBox opt, QWidget w) {
        double lt2 = GROUPBOX_LINE_THICKNESS / 2.0;
        double yoff = 0;
        if (opt.text().length() > 0 && w != null) {
            QFontMetrics metrics = new QFontMetrics(w.font());
            yoff = metrics.ascent();
        }
        return new QRectF(rect).adjusted(QReal.valueOf(lt2).platformValue(), QReal.valueOf(lt2 + yoff).platformValue(), QReal.valueOf(-lt2).platformValue(), QReal.valueOf(-lt2).platformValue());
    }

    private void drawGroupBox(QStyleOptionGroupBox opt, QPainter p, QWidget widget) {

        p.save();

        p.setRenderHint(QPainter.RenderHint.Antialiasing);

        QStyle.State state = opt.state();
        String title = opt.text();

        QFont font = p.font();
        font.setBold(true);
        p.setFont(font);

        QRect rect = opt.rect();
        QPainterPath clipPath = new QPainterPath();
        clipPath.addRect(new QRectF(rect));

        if(!title.equals("")){

            QRectF text_rect = groupBoxLabelRect(rect, widget, title);

            drawShadeButton(p, text_rect, state);

            p.setPen(new QPen(new QBrush(TROLLTECH_GREEN), QReal.valueOf(GROUPBOX_LINE_THICKNESS).platformValue()));

            p.drawRoundRect(text_rect, (int) (ROUND * 100 / text_rect.width()),
                    (int) (ROUND * 100 / text_rect.height()));

            p.setPen(Style.PEN_BLACK);
            Style.drawShadowText(p, text_rect.translated(-1, -1), title, 2, 2);
            clipPath.addRect(text_rect);

        }

        if(!((QGroupBox)widget).isFlat()){
            p.setPen(new QPen(new QBrush(TROLLTECH_GREEN), QReal.valueOf(GROUPBOX_LINE_THICKNESS).platformValue()));
            p.setClipPath(clipPath);
            QRectF bound_rect = groupBoxContentsRect(rect, opt, widget);
            drawButtonOutline(p, bound_rect, state);
        }
        p.restore();
    }

    @Override
    public int pixelMetric(QStyle.PixelMetric pm, QStyleOption opt, QWidget widget) {
        switch (pm) {
        case PM_ExclusiveIndicatorWidth:
        case PM_ExclusiveIndicatorHeight:
            return opt.fontMetrics().height();
        case PM_ScrollBarExtent:
            return 23;
        default:
            return super.pixelMetric(pm, opt, widget);
        }
    }

    private void drawRadioButton(QStyleOptionButton opt, QPainter p) {
        QRect rect = opt.rect();
        String text = opt.text();

        p.save();
        p.setRenderHint(QPainter.RenderHint.Antialiasing);

        p.setPen(PEN_THICK_GREEN);

        double dim = Math.min(rect.width(), rect.height());
        QRectF circle_rect = new QRectF(0, 0, QReal.valueOf(dim).platformValue(), QReal.valueOf(dim).platformValue());

        p.drawEllipse(circle_rect.adjusted(3, 3, -3, -3));

        if (opt.state().isSet(QStyle.StateFlag.State_On)) {
            p.setBrush(new QBrush(TROLLTECH_GREEN));
            p.setPen(QPen.NoPen);
            p.drawEllipse(circle_rect.adjusted(6, 6, -6, -6));
        }

        QRectF text_rect = new QRectF(QReal.valueOf(dim + DOT_TO_TEXT_PADDING).platformValue(), 2, QReal.valueOf(rect.width() - dim).platformValue()
                - DOT_TO_TEXT_PADDING, rect.height());
        p.setPen(PEN_BLACK);
        drawShadowText(p, text_rect.translated(-1, -2), text, 2, 2);

        p.restore();
    }

    private void drawPushButton(QStyleOptionButton opt, QPainter p) {
        QRectF rect = new QRectF(opt.rect());
        QStyle.State state = opt.state();
        boolean button_down = state.isSet(QStyle.StateFlag.State_Sunken);
        boolean enabled = state.isSet(QStyle.StateFlag.State_Enabled);

        p.setRenderHint(QPainter.RenderHint.Antialiasing);
        rect.adjust(3, 1, -3, -1);
        drawShadeButton(p, rect, state);
        drawButtonOutline(p, rect, state);

        double translation = 0;
        double shadow_offset = 2;

        if (button_down) {
            translation = 1;
            shadow_offset = 0;
        }

        if (enabled)
            p.setPen(PEN_BLACK);
        else
            p.setPen(PEN_GRAY);

        drawShadowText(p, rect.translated(QReal.valueOf(translation).platformValue(), QReal.valueOf(translation).platformValue()), opt.text(), shadow_offset,
                shadow_offset);

    }

    public void drawScrollBarSlider(QStyleOptionSlider opt, QPainter p) {
        QRectF rect = new QRectF(opt.rect());
        p.save();
        p.fillRect(rect, new QBrush(new QColor(com.trolltech.qt.core.Qt.GlobalColor.white)));
        QStyle.State state = opt.state();
        state.clear(QStyle.StateFlag.State_Sunken);
        drawButtonOutline(p, rect, state);
        p.restore();
    }

    public void drawScrollBarLine(QStyleOptionSlider opt, QPainter p, QStyle.ControlElement ce) {
        QRectF rect = new QRectF(opt.rect());
        p.save();
        p.fillRect(rect, new QBrush(new QColor(com.trolltech.qt.core.Qt.GlobalColor.white)));
        drawButtonOutline(p, rect, opt.state());

        p.translate(new QRectF(opt.rect()).center());
        p.setRenderHint(QPainter.RenderHint.Antialiasing);
        if (isSunken(opt.state())) {
            p.setPen(PEN_THICK_GREEN);
            p.setBrush(BRUSH_LIGHT_GREEN);
        } else {
            p.setBrush(BRUSH_GREEN);
            p.setPen(QPen.NoPen);
        }
        if (ce == QStyle.ControlElement.CE_ScrollBarAddLine)
            p.drawPath(DOWN_INDICATOR);
        else if (ce == QStyle.ControlElement.CE_ScrollBarSubLine)
            p.drawPath(UP_INDICATOR);
        p.restore();
    }

    public static void drawButtonOutline(QPainter p, QRectF rect, QStyle.State state) {
        p.save();
        p.setRenderHint(QPainter.RenderHint.Antialiasing);
        if (isEnabled(state))
            p.setPen(PEN_THICK_GREEN);
        else
            p.setPen(PEN_THICK_LIGHT_GREEN);
        p.setBrush(QBrush.NoBrush);
        p.drawRoundRect(rect.adjusted(1, 1, -1, -1), (int) (ROUND * 100 / rect.width()),
                (int) (ROUND * 100 / rect.height()));
        p.restore();
    }

    public static void drawShadeButton(QPainter p, QRectF rect, QStyle.State state) {
        p.save();
        p.translate(rect.topLeft());
        p.scale(rect.width(), rect.height());
        p.setBrush(new QBrush(isSunken(state) ? GRADIENT_DARK : GRADIENT_LIGHT));
        p.setPen(QPen.NoPen);
        p.drawRoundRect(0, 0, 1, 1, (int) (ROUND * 100 / rect.width()), (int) (ROUND * 100 / rect
                .height()));

        p.restore();
    }

    public static void drawShadowText(QPainter p, QRectF rect, String text, double dx, double dy) {

        int flags = Qt.AlignmentFlag.AlignCenter.value() | Qt.TextFlag.TextShowMnemonic.value();

        p.save();
        p.setPen(new QPen(new QColor(0, 0, 0, 20)));
        p.drawText(rect.translated(QReal.valueOf(dx).platformValue(), QReal.valueOf(dy).platformValue()), flags, text);

        p.setPen(new QPen(new QColor(0, 0, 0, 15)));
        p.drawText(rect.translated(QReal.valueOf(dx - 1).platformValue(), QReal.valueOf(dy).platformValue()), flags, text);
        p.drawText(rect.translated(QReal.valueOf(dx).platformValue(), QReal.valueOf(dy - 1).platformValue()), flags, text);
        p.drawText(rect.translated(QReal.valueOf(dx + 1).platformValue(), QReal.valueOf(dy).platformValue()), flags, text);
        p.drawText(rect.translated(QReal.valueOf(dx).platformValue(), QReal.valueOf(dy + 1).platformValue()), flags, text);
        p.restore();

        p.drawText(rect, flags, text);
    }

}
