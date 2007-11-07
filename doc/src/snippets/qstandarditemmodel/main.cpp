#include <QtGui>
#include <QApplication>

class Widget : public QWidget
{
public:
    Widget(QWidget *parentWidget = 0);
};

Widget::Widget(QWidget *parentWidget)
    : QWidget(parentWidget)
{
    QStandardItemModel *model = new QStandardItemModel();
    QModelIndex parent;
    for (int i = 0; i < 4; ++i) {
        parent = model->index(0, 0, parent);
        model->insertRows(0, 1, parent);
        model->insertColumns(0, 1, parent);
        QModelIndex index = model->index(0, 0, parent);
        model->setData(index, i);
    }
}

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    Widget widget;
    widget.show();
    return app.exec();
}

