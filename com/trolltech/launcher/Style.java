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

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

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
    public static QPen PEN_BLACK = new QPen(QColor.black, 0);
    public static QPen PEN_GRAY = new QPen(COLOR_GRAY, 0);

    public static final double GROUPBOX_LINE_THICKNESS = 2;
    public static final int ROUND = 20;
    public static final int TEXT_PADDING = 5;
    public static final int H_TEXT_PADDING = 10;

    public static final int DOT_TO_TEXT_PADDING = 4;

    static {
        GRADIENT_LIGHT = new QLinearGradient(0, 0, 0, 1);
        GRADIENT_LIGHT.setColorAt(0, TT_BG_GREEN_DARK);
        GRADIENT_LIGHT.setColorAt(0.25, QColor.white);
        GRADIENT_LIGHT.setColorAt(0.5, TT_BG_GREEN_LIGHT);
        GRADIENT_LIGHT.setColorAt(1, TT_BG_GREEN_DARK);

        GRADIENT_DARK = new QLinearGradient(0, 0, 0, 1);
        GRADIENT_DARK.setColorAt(0, QColor.white);
        GRADIENT_DARK.setColorAt(0.25, TT_BG_GREEN_DARK);
        GRADIENT_DARK.setColorAt(0.5, TT_BG_GREEN_DARK);
        GRADIENT_DARK.setColorAt(1, TT_BG_GREEN_LIGHT);
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

    public void drawComplexControl(QStyle.ComplexControl cc, QNativePointer opt, QPainter p,
            QWidget widget) {
        switch (cc) {
        case CC_GroupBox:
            drawGroupBox(QStyleOptionGroupBox.fromNativePointer(opt), p);
            break;
        default:
            super.drawComplexControl(cc, opt, p, widget);
            break;
        }
    }

    public void drawControl(QStyle.ControlElement ce, QNativePointer opt, QPainter p, QWidget widget) {
        switch (ce) {
        case CE_RadioButton:
            drawRadioButton(QStyleOptionButton.fromNativePointer(opt), p);
            break;
        case CE_PushButton:
            drawPushButton(QStyleOptionButton.fromNativePointer(opt), p);
            break;
        default:
            super.drawControl(ce, opt, p, widget);
            break;
        }
    }

    private void drawGroupBox(QStyleOptionGroupBox opt, QPainter p) {

        p.save();

        p.setRenderHint(QPainter.RenderHint.Antialiasing);

        QStyle.State state = opt.state();
        String title = opt.text();
        QFont font = p.font();
        font.setBold(true);
        p.setFont(font);

        QRect rect = opt.rect();

        QRectF text_rect = new QRectF(p.boundingRect(ROUND, 0, rect.width() - ROUND * 2, rect
                .height(), Qt.AlignmentFlag.AlignTop.value()
                | Qt.AlignmentFlag.AlignHCenter.value(), title));

        text_rect = text_rect.adjusted(-H_TEXT_PADDING, GROUPBOX_LINE_THICKNESS / 2.0, TEXT_PADDING
                * 2 + H_TEXT_PADDING, TEXT_PADDING * 2);

        drawShadeButton(p, text_rect, state);

        p.setPen(new QPen(new QBrush(TROLLTECH_GREEN), GROUPBOX_LINE_THICKNESS));
        p.drawRoundRect(text_rect, (int) (ROUND * 100 / text_rect.width()),
                (int) (ROUND * 100 / text_rect.height()));

        p.setPen(Style.PEN_BLACK);
        Style.drawShadowText(p, text_rect.translated(-1, -1), title, 2, 2);

        p.setPen(new QPen(new QBrush(TROLLTECH_GREEN), GROUPBOX_LINE_THICKNESS));
        QPainterPath clipPath = new QPainterPath();
        clipPath.addRect(new QRectF(rect));
        clipPath.addRect(text_rect);
        p.setClipPath(clipPath);

        double lt2 = GROUPBOX_LINE_THICKNESS / 2.0;
        double yoff = p.fontMetrics().ascent();
        QRectF bound_rect = new QRectF(rect).adjusted(lt2, lt2 + yoff, -lt2, -lt2);

        drawButtonOutline(p, bound_rect, state);

        p.restore();
    }

    public int pixelMetric(QStyle.PixelMetric pm, QNativePointer option, QWidget widget) {
        QStyleOption opt = QStyleOption.fromNativePointer(option);
        switch (pm) {
        case PM_ExclusiveIndicatorWidth:
        case PM_ExclusiveIndicatorHeight:
            return opt.fontMetrics().height();
        default:
            return super.pixelMetric(pm, option, widget);
        }
    }

    private void drawRadioButton(QStyleOptionButton opt, QPainter p) {
        QRect rect = opt.rect();
        String text = opt.text();

        p.save();
        p.setRenderHint(QPainter.RenderHint.Antialiasing);

        p.setPen(PEN_THICK_GREEN);

        double dim = Math.min(rect.width(), rect.height());
        QRectF circle_rect = new QRectF(0, 0, dim, dim);

        p.drawEllipse(circle_rect.adjusted(3, 3, -3, -3));

        if (opt.state().isSet(QStyle.StateFlag.State_On)) {
            p.setBrush(new QBrush(TROLLTECH_GREEN));
            p.setPen(QPen.NoPen);
            p.drawEllipse(circle_rect.adjusted(6, 6, -6, -6));
        }

        QRectF text_rect = new QRectF(dim + DOT_TO_TEXT_PADDING, 2, rect.width() - dim
                - DOT_TO_TEXT_PADDING, rect.height());
        p.setPen(PEN_BLACK);
        drawShadowText(p, text_rect.translated(-1, -2), text, 2, 2);

        p.restore();
    }

    private void drawPushButton(QStyleOptionButton opt, QPainter p) {
        QRectF rect = new QRectF(opt.rect());
        QStyle.State state = opt.state();
        boolean button_down = state.isSet(QStyle.StateFlag.State_Sunken);
        boolean enabled = state.isSet(QStyle.StateFlag.State_Sunken);

        p.setRenderHint(QPainter.RenderHint.Antialiasing);

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

        drawShadowText(p, rect.translated(translation, translation), opt.text(), shadow_offset,
                shadow_offset);

    }

    public static void drawButtonOutline(QPainter p, QRectF rect, QStyle.State state) {
        p.save();
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
        p.drawText(rect.translated(dx, dy), flags, text);

        p.setPen(new QPen(new QColor(0, 0, 0, 15)));
        p.drawText(rect.translated(dx - 1, dy), flags, text);
        p.drawText(rect.translated(dx, dy - 1), flags, text);
        p.drawText(rect.translated(dx + 1, dy), flags, text);
        p.drawText(rect.translated(dx, dy + 1), flags, text);
        p.restore();

        p.drawText(rect, flags, text);
    }

}
