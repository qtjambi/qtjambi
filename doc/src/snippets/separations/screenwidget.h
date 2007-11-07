/****************************************************************************
**
** Copyright (C) 2004-$THISYEAR$ $TROLLTECH$. All rights reserved.
**
** This file is part of the $MODULE$ of the Qt Toolkit.
**
** $TROLLTECH_DUAL_LICENSE$
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

#ifndef SCREENWIDGET_H
#define SCREENWIDGET_H

#include <QColor>
#include <QFrame>
#include <QImage>
#include <QSize>

class QGridLayout;
class QLabel;
class QPushButton;
class QWidget;

class ScreenWidget : public QFrame
{
    Q_OBJECT
public:
    enum Separation { Cyan, Magenta, Yellow };

    ScreenWidget(QWidget *parent, QColor initialColor, const QString &name,
                 Separation mask, const QSize &labelSize);
    void setImage(QImage &image);
    QImage* image();

signals:
    void imageChanged();

public slots:
    void setColor();
    void invertImage();

private:
    void createImage();

    bool inverted;
    QColor paintColor;
    QImage newImage;
    QImage originalImage;
    QLabel *imageLabel;
    QLabel *nameLabel;
    QPushButton *colorButton;
    QPushButton *invertButton;
    Separation maskColor;
};

#endif
