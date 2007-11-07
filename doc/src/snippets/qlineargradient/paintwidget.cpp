#include <QtGui>
#include "paintwidget.h"

PaintWidget::PaintWidget(QWidget *parent)
    : QWidget(parent)
{
}

void PaintWidget::paintEvent(QPaintEvent *event)
{
    QLinearGradient gradient1(rect().topLeft(), rect().bottomRight());
    gradient1.setColorAt(0, QColor("#ffffcc"));
    gradient1.setColorAt(1, QColor("#ccccff"));

    QRectF ellipseRect(width()*0.25, height()*0.25, width()*0.5, height()*0.5);
    QLinearGradient gradient2(ellipseRect.topLeft(), ellipseRect.bottomRight());
    gradient2.setColorAt(0, QColor("#ccccff"));
    gradient2.setColorAt(1, QColor("#ffffcc"));

    QPainter painter;
    painter.begin(this);
    painter.setRenderHint(QPainter::Antialiasing);
    painter.fillRect(rect(), QBrush(gradient1));
    painter.setBrush(QBrush(gradient2));
    painter.drawEllipse(ellipseRect);
    painter.end();
}
