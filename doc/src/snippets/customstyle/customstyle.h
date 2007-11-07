#ifndef CUSTOMSTYLE_H
#define CUSTOMSTYLE_H

#include <QWindowsStyle>

//! [0]
class CustomStyle : public QWindowsStyle
{
    Q_OBJECT

public:
    CustomStyle()
    ~CustomStyle() {}

    void drawPrimitive(PrimitiveElement element, const QStyleOption *option,
                       QPainter *painter, const QWidget *widget) const;
};
//! [0]

#endif
