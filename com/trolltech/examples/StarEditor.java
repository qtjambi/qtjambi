package com.trolltech.examples;

import com.trolltech.qt.gui.*;
import com.trolltech.qt.core.*;

class StarEditor extends QWidget
{
    private StarRating starRating;

    public StarEditor(QWidget parent, StarRating rating)
    {
        super(parent);

        starRating = rating;
        setMouseTracking(true);
        setAutoFillBackground(true);
    }

    public QSize sizeHint()
    {
        return starRating.sizeHint();
    }

    public void paintEvent(QPaintEvent event)
    {
        QPainter painter = new QPainter(this);
        starRating.paint(painter, rect(), palette(), StarRating.ReadWrite);
    }

    public void mouseMoveEvent(QMouseEvent event)
    {
        int star = starAtPosition(event.x());

        if (star != starRating.getRating() && star > 0) {
            starRating.setRating(star);
            update();
        }
    }

    public int starAtPosition(int x)
    {
        int star = (x / (starRating.sizeHint().width()
                        / starRating.getMaxRating())) + 1;

        if (star <= 0 || star > starRating.getMaxRating())
            return -1;

        return star;
    }

    public void setStarRating(StarRating rating)
    {
        starRating = rating;
    }

    public StarRating starRating()
    {
        return starRating;
    }
}
