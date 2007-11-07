#include <QtGui>

//! [0]
QAccessibleInterface *sliderFactory(const QString &classname, QObject *object)
{
    QAccessibleInterface *interface = 0;

    if (classname == "QSlider" && object && object->isWidgetType())
        interface = new SliderInterface(classname,
                                        static_cast<QWidget *>(object));
    
    return interface;
}

int main(int argv, char **args)
{
    QApplication app(argv, args);
    QAccessible::installFactory(sliderFactory);
//! [0]

    QMainWindow mainWindow;
    mainWindow.show();

    return app.exec();
//! [1]
}
//! [1]
