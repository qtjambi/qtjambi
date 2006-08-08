#ifndef WIDGETHOST_H
#define WIDGETHOST_H

#include <QtGui/QScrollArea>
#include <QtCore/QHash>
#include <QtGui/QMouseEvent>

class QFrame;
class SizeHandleRect;
class FormWindowSelection;

class WidgetHost : public QScrollArea
{
    Q_OBJECT
public:
    WidgetHost(QWidget *parent = 0);
    void setFormWindow(QWidget *fw);
    void updateFormWindowGeometry(const QRect &r);
    QSize formWindowSize() const;
    void setFormWindowSize(const QSize &s);
    void selectFormWindow();
    void unSelectFormWindow();

signals:
    void formWindowSizeChanged(int, int);

private slots:
    void fwSizeWasChanged(const QRect &, const QRect &);

private:
    QFrame *formWindowFrame;
    QWidget *formWindow;
    QWidget *fakeWidget;
    FormWindowSelection *selection;
};

class FormWindowSelection : public QObject
{
    Q_OBJECT
public:
    FormWindowSelection(QWidget *parent);

    void updateGeometry();
    void hide();
    void show();
    void update();

    void setWidget(QWidget *w);

signals:
    void formWindowSizeChanged(const QRect &oldGeo, const QRect &newGeo);

protected:
    bool eventFilter(QObject *obj, QEvent *e);

    QHash<int, SizeHandleRect*> handles;
    QWidget *formWindowFrame;
};

class SizeHandleRect : public QWidget
{
    Q_OBJECT
public:
    enum Direction { LeftTop, Top, RightTop, Right, RightBottom, Bottom, LeftBottom, Left };

    SizeHandleRect(QWidget *parent, Direction d, FormWindowSelection *s);
    void updateCursor();
    void setWidget(QWidget *w);

signals:
    void mouseButtonReleased(const QRect &, const QRect &);

protected:
    void paintEvent(QPaintEvent *e);
    void mousePressEvent(QMouseEvent *e);
    void mouseMoveEvent(QMouseEvent *e);
    void mouseReleaseEvent(QMouseEvent *e);

private:
    void tryResize(QWidget *w, int width, int height);

private:
    Direction dir;
    QPoint oldPos;
    QPoint curPos;
    FormWindowSelection *sel;
    QWidget *widget;
};

#endif // WIDGETHOST_H

