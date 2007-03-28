package com.trolltech.examples;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

class StarRating
{
    private int starCount, maxCount, mode;
    private static QPolygonF starPolygon, diamondPolygon;

    public static final int ReadOnly = 0, ReadWrite = 1, PaintingFactor = 20;

    static {
        starPolygon = new QPolygonF();
        starPolygon.append(new QPointF(1.0, 0.5));
        for (int i = 1; i < 5; i++)
            starPolygon.append(
                new QPointF(0.5 + 0.5 * Math.cos(0.8 * i * Math.PI),
                            0.5 + 0.5 * Math.sin(0.8 * i * Math.PI)));

        diamondPolygon = new QPolygonF();
        diamondPolygon.append(new QPointF(0.4, 0.5));
        diamondPolygon.append(new QPointF(0.5, 0.4));
        diamondPolygon.append(new QPointF(0.6, 0.5));
        diamondPolygon.append(new QPointF(0.5, 0.6));
        diamondPolygon.append(new QPointF(0.4, 0.5));
    }

    public StarRating()
    {
        this(1, 5);
    }

    public StarRating(int rating)
    {
        this(rating, 5);
    }

    public StarRating(int rating, int maxRating)
    {
        maxCount = maxRating;
        setRating(rating);
    }

    public void setRating(int rating)
    {
        if (rating > 0 && rating <= maxCount)
            starCount = rating;
        else
            starCount = maxCount;
    }

    public int getRating()
    {
        return starCount;
    }

    public int getMaxRating()
    {
        return maxCount;
    }

    public void paint(QPainter painter, QRect rect, QPalette palette,
                      int mode)
    {
        painter.save();

        painter.setRenderHint(QPainter.RenderHint.Antialiasing, true);
        painter.setPen(Qt.PenStyle.NoPen);

        if (mode == ReadWrite)
            painter.setBrush(palette.highlight());
        else
            painter.setBrush(palette.foreground());

        int yOffset = (rect.height() - PaintingFactor) / 2;
        painter.translate(rect.x(), rect.y() + yOffset);
        painter.scale(PaintingFactor, PaintingFactor);

        for (int i = 0; i < maxCount; i++) {
            if (i < starCount)
                painter.drawPolygon(starPolygon, Qt.FillRule.WindingFill);
            else
                painter.drawPolygon(diamondPolygon, Qt.FillRule.WindingFill);

            painter.translate(1.0, 0.0);
        }

        painter.restore();
    }

    public QSize sizeHint()
    {
        return new QSize(PaintingFactor * maxCount, PaintingFactor);
    }
}
