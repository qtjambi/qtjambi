#include <QtGui>
#include <QApplication>

class MyPushButton : public QPushButton
{
public:
    MyPushButton(QWidget *parent = 0);

    void paintEvent(QPaintEvent *);
};

MyPushButton::MyPushButton(QWidget *parent)
    : QPushButton(parent)
{
}

//! [0]
void MyPushButton::paintEvent(QPaintEvent *)
{
    QStyleOptionButton option;
    option.initFrom(this);
    option.state = isDown() ? QStyle::State_Sunken : QStyle::State_Raised;
    if (isDefault())
        option.features |= QStyleOptionButton::DefaultButton;
    option.text = text();
    option.icon = icon();

    QPainter painter(this);
    style()->drawControl(QStyle::CE_PushButton, &option, &painter, this);
}
//! [0]



class MyStyle : public QStyle
{
public:
    MyStyle();

    void drawPrimitive(PrimitiveElement element, const QStyleOption *option,
                       QPainter *painter, const QWidget *widget);
};

MyStyle::MyStyle()
{
//! [1]
    QStyleOptionFrame *option;

    if (const QStyleOptionFrame *frameOption =
           qstyleoption_cast<const QStyleOptionFrame *>(option)) {
        QStyleOptionFrameV2 frameOptionV2(*frameOption);

        // draw the frame using frameOptionV2
    }
//! [1]

//! [2]
    if (const QStyleOptionProgressBar *progressBarOption =
           qstyleoption_cast<const QStyleOptionProgressBar *>(option)) {
        QStyleOptionProgressBarV2 progressBarV2(*progressBarOption);

        // draw the progress bar using progressBarV2
    }
//! [2]

//! [3]
    if (const QStyleOptionTab *tabOption =
           qstyleoption_cast<const QStyleOptionTab *>(option)) {
        QStyleOptionTabV2 tabV2(*tabOption);

        // draw the tab using tabV2
   }
//! [3]
}

//! [4]
void MyStyle::drawPrimitive(PrimitiveElement element,
                            const QStyleOption *option,
                            QPainter *painter,
                            const QWidget *widget)
{
    if (element == PE_FrameFocusRect) {
        const QStyleOptionFocusRect *focusRectOption =
                qstyleoption_cast<const QStyleOptionFocusRect *>(option);
        if (focusRectOption) {
            // ...
        }
    }
    // ...
}
//! [4]

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    MyPushButton button;
    button.show();
    return app.exec();
}
