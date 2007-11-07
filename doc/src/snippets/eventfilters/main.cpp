#include <QApplication>
#include <QTextEdit>

#include "filterobject.h"

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    QTextEdit editor;
    FilterObject filter;
    filter.setFilteredObject(&editor);
    editor.show();
    return app.exec();
}
