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

    QStackedLayout *stackedLayout = new QStackedLayout;
    stackedLayout->addWidget(firstPageWidget);
    stackedLayout->addWidget(secondPageWidget);
    stackedLayout->addWidget(thirdPageWidget);

//! [0] //! [1]
    QComboBox *pageComboBox = new QComboBox;
    pageComboBox->addItem(tr("Page 1"));
    pageComboBox->addItem(tr("Page 2"));
    pageComboBox->addItem(tr("Page 3"));
    connect(pageComboBox, SIGNAL(activated(int)),
            stackedLayout, SLOT(setCurrentIndex(int)));
//! [1]

//! [2]
    QVBoxLayout *mainLayout = new QVBoxLayout;
//! [2]
    mainLayout->addWidget(pageComboBox);
//! [3]
    mainLayout->addLayout(stackedLayout);
    setLayout(mainLayout);
//! [3]
}

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    Widget widget;
    widget.show();
    return app.exec();
}

