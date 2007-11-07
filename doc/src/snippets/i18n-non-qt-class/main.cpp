#include <QtCore>
#include "myclass.h"

int main(int argc, char *argv[])
{
    QCoreApplication app(argc, argv);

    QTranslator translator;
    translator.load(":/translations/i18n-non-qt-class_" + QLocale::system().name());
    app.installTranslator(&translator);

    MyClass instance;
    return 0;
}
