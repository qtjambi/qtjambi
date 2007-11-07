#ifndef BUTTONWIDGET_H
#define BUTTONWIDGET_H

#include <qwidget.h>

class QSignalMapper;
class QString;
class QStringList;

//! [0]
class ButtonWidget : public QWidget
{
    Q_OBJECT

public:
    ButtonWidget(QStringList texts, QWidget *parent = 0);

signals:
    void clicked(const QString &text);

private:
    QSignalMapper *signalMapper;
//! [0] //! [1]
};
//! [1]

#endif
