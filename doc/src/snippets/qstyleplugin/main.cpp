#include <QApplication>
#include <QtGui>

class MyStylePlugin : public QStylePlugin
{
public:
    MyStylePlugin(QObject *parent = 0);

    QStyle *create(const QString &key);
    QStringList keys() const;
};

class RocketStyle : public QCommonStyle
{
public:
    RocketStyle() {};

};

class StarBusterStyle : public QCommonStyle
{
public:
    StarBusterStyle() {};
};

MyStylePlugin::MyStylePlugin(QObject *parent)
    : QStylePlugin(parent)
{
}

//! [0]
QStringList MyStylePlugin::keys() const
{
    return QStringList() << "Rocket" << "StarBuster";
}
//! [0]

//! [1]
QStyle *MyStylePlugin::create(const QString &key)
{
    QString lcKey = key;
    if (lcKey == "rocket") {
        return new RocketStyle;
    } else if (lcKey == "starbuster") {
        return new StarBusterStyle;
    }
    return 0;
//! [1] //! [2]
}
//! [2]

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    MyStylePlugin plugin;
    return app.exec();
}
