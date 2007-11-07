#ifndef RENDERAREA_H
#define RENDERAREA_H

#include <QWidget>

class RenderArea : public QWidget
{
   Q_OBJECT

public:
   RenderArea(QBrush *brush, QWidget *parent = 0);
   QSize minimumSizeHint() const;

protected:
    void paintEvent(QPaintEvent *event);

private:
    QBrush *currentBrush;
};

#endif
