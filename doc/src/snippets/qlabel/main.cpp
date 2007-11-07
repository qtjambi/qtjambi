#include <QtGui>

class Updater : public QObject
{
    Q_OBJECT

public:
    Updater(QWidget *widget);

public slots:
    void adjustSize();

private:
    QWidget *widget;
};

Updater::Updater(QWidget *widget)
    : widget(widget)
{
}

void Updater::adjustSize()
{
    widget->adjustSize();
}

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    QLabel *label = new QLabel("My label");
    QLineEdit *editor = new QLineEdit("New text");
    QWidget window;
    //Updater updater(&label);
    QObject::connect(editor, SIGNAL(textChanged(const QString &)),
                     label, SLOT(setText(const QString &)));
    //QObject::connect(editor, SIGNAL(textChanged(const QString &)),
    //                 &updater, SLOT(adjustSize()));
    //editor.show();
    //label.show();
    QVBoxLayout *layout = new QVBoxLayout;
    layout->addWidget(label);
    layout->addWidget(editor);
    window.setLayout(layout);
    window.show();
    return app.exec();
}

#include "main.moc"
