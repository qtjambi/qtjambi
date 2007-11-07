#ifndef PAINTWIDGET_H
#define PAINTWIDGET_H

#include <QWidget>

class QPaintEvent;

class PaintWidget : public QWidget
{
    Q_OBJECT

public:
    PaintWidget(QWidget *parent = 0);

protected:
    void paintEvent(QPaintEvent *event);
};

#endif
