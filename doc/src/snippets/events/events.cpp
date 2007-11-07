#include <QCheckBox>
#include <QMouseEvent>

class MyCheckBox : public QCheckBox
{
public:
    void mousePressEvent(QMouseEvent *event);
};

//! [0]
void MyCheckBox::mousePressEvent(QMouseEvent *event)
{
    if (event->button() == Qt::LeftButton) {
        // handle left mouse button here
    } else {
        // pass on other buttons to base class
        QCheckBox::mousePressEvent(event);
    }
}
//! [0]

class MyWidget : public QWidget
{
public:
    bool event(QEvent *event);
};

static const int MyCustomEventType = 1099;

class MyCustomEvent : public QEvent
{
public:
    MyCustomEvent() : QEvent((QEvent::Type)MyCustomEventType) {}
};

//! [1]
bool MyWidget::event(QEvent *event)
{
    if (event->type() == QEvent::KeyPress) {
	QKeyEvent *ke = static_cast<QKeyEvent *>(event);
	if (ke->key() == Qt::Key_Tab) {
	    // special tab handling here
	    return true;
	}
    } else if (event->type() == MyCustomEventType) {
	MyCustomEvent *myEvent = static_cast<MyCustomEvent *>(event);
	// custom event handling here
	return true;
    }

    return QWidget::event(event);
}
//! [1]

int main()
{
}
