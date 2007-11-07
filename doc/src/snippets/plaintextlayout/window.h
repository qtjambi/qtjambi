#ifndef WINDOW_H
#define WINDOW_H

#include <QWidget>

class Window : public QWidget
{
    Q_OBJECT

public:
    Window(QWidget *parent = 0);

protected:
    void paintEvent(QPaintEvent *event);

private:
    QFont font;
    QString text;
};

#endif
