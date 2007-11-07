#include <QtGui>
#include "textedit.h"

int main(int argc, char * argv[])
{
    QApplication app(argc, argv);

    TextEdit *textEdit = new TextEdit;
    textEdit->show();

    return app.exec();
}