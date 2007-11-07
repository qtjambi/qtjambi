#include <QtGui>
#include <QApplication>

class Widget : public QWidget
{
public:
    Widget(QWidget *parent = 0);
};

Widget::Widget(QWidget *parent)
    : QWidget(parent)
{
//! [0]
    QWidget *firstPageWidget = new QWidget;
    QWidget *secondPageWidget = new QWidget;
    QWidget *thirdPageWidget = new QWidget;

    QStackedWidget *stackedWidget = new QStackedWidget;
    stackedWidget->addWidget(firstPageWidget);
    stackedWidget->addWidget(secondPageWidget);
    stackedWidget->addWidget(thirdPageWidget);

//! [0] //! [1]
    QComboBox *pageComboBox = new QComboBox;
    pageComboBox->addItem(tr("Page 1"));
    pageComboBox->addItem(tr("Page 2"));
    pageComboBox->addItem(tr("Page 3"));
    connect(pageComboBox, SIGNAL(activated(int)),
            stackedWidget, SLOT(setCurrentIndex(int)));

//! [1] //! [2]
    QVBoxLayout *layout = new QVBoxLayout;
//! [2]
    layout->addWidget(pageComboBox);
//! [3]
    layout->addWidget(stackedWidget);
    setLayout(layout);
//! [3]
}

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    Widget widget;
    widget.show();
    return app.exec();
}
