#ifndef STYLEWIDGET_H
#define STYLEWIDGET_H

#include <QWidget>
#include <QPainterPath>
#include "renderarea.h"

class QLabel;

class StyleWidget : public QWidget
{
    Q_OBJECT

public:
    StyleWidget(QWidget *parent = 0);

private:
    RenderArea *solid;
    RenderArea *dense1;
    RenderArea *dense2;
    RenderArea *dense3;
    RenderArea *dense4;
    RenderArea *dense5;
    RenderArea *dense6;
    RenderArea *dense7;
    RenderArea *no;
    RenderArea *hor;
    RenderArea *ver;
    RenderArea *cross;
    RenderArea *bdiag;
    RenderArea *fdiag;
    RenderArea *diagCross;
    RenderArea *linear;
    RenderArea *radial;
    RenderArea *conical;
    RenderArea *texture;

    QLabel *solidLabel;
    QLabel *dense1Label;
    QLabel *dense2Label;
    QLabel *dense3Label;
    QLabel *dense4Label;
    QLabel *dense5Label;
    QLabel *dense6Label;
    QLabel *dense7Label;
    QLabel *noLabel;
    QLabel *horLabel;
    QLabel *verLabel;
    QLabel *crossLabel;
    QLabel *bdiagLabel;
    QLabel *fdiagLabel;
    QLabel *diagCrossLabel;
    QLabel *linearLabel;
    QLabel *radialLabel;
    QLabel *conicalLabel;
    QLabel *textureLabel;
};
#endif
