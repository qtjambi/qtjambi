#include <QtGui>

class SliderPlugin : public QAccessiblePlugin
{
public:
    SliderPlugin() {}

    QStringList keys() const;
    QAccessibleInterface *create(const QString &classname, QObject *object);
};

//! [0]
QStringList SliderPlugin::keys() const
{
    return QStringList() << "QSlider";
}
//! [0]

//! [1]
QAccessibleInterface *SliderPlugin::create(const QString &classname, QObject *object)
{
    QAccessibleInterface *interface = 0;

    if (classname == "QSlider" && object && object->isWidgetType())
        interface = new AccessibleSlider(classname, static_cast<QWidget *>(object));

    return interface;
}
//! [1]

//! [2]
Q_EXPORT_STATIC_PLUGIN(SliderPlugin)
Q_EXPORT_PLUGIN2(acc_sliderplugin, SliderPlugin)
//! [2]
