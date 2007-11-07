#include <QtGui>

class MyScrollArea : public QAbstractScrollArea
{
public:
    MyScrollArea(QWidget *w);
    void setWidget(QWidget *w);

protected:
    void scrollContentsBy(int dx, int dy);
    void resizeEvent(QResizeEvent *event);

private:
    void updateWidgetPosition();
    void updateArea();

    QWidget *widget;
};

MyScrollArea::MyScrollArea(QWidget *widget)
    : QAbstractScrollArea()
{
    setWidget(widget);
}

void MyScrollArea::setWidget(QWidget *w)
{
    widget = w;
    widget->setParent(viewport());
    if (!widget->testAttribute(Qt::WA_Resized))
        widget->resize(widget->sizeHint());

    verticalScrollBar()->setValue(0);
    verticalScrollBar()->setValue(0);

    updateArea();
}

void MyScrollArea::updateWidgetPosition()
{
//! [0]
    int hvalue = horizontalScrollBar()->value();
    int vvalue = verticalScrollBar()->value();
    QPoint topLeft = viewport()->rect().topLeft();

    widget->move(topLeft.x() - hvalue, topLeft.y() - vvalue);
//! [0]
}

void MyScrollArea::scrollContentsBy(int /*dx*/, int /*dy*/)
{
    updateWidgetPosition();
}

void MyScrollArea::updateArea()
{
//! [1]
    QSize areaSize = viewport()->size();
    QSize  widgetSize = widget->size();

    verticalScrollBar()->setPageStep(widgetSize.height());
    horizontalScrollBar()->setPageStep(widgetSize.width());
    verticalScrollBar()->setRange(0, widgetSize.height() - areaSize.height());
    horizontalScrollBar()->setRange(0, widgetSize.width() - areaSize.width());
    updateWidgetPosition();
//! [1]
}

void MyScrollArea::resizeEvent(QResizeEvent *event)
{
    updateArea();
}

int main(int argv, char **args)
{
    QApplication app(argv, args);

    QPixmap pixmap("mypixmap.png");
    QLabel label;
    label.setPixmap(pixmap);
    MyScrollArea area(&label);
    area.resize(300, 300);
    area.show();
    
    area.setWidget(&label);

    return app.exec();
}
