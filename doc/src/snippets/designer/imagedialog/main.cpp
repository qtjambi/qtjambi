#include "ui_imagedialog.h"
#include <QApplication>

int main(int argc, char *argv[])
{
    QApplication app(argc, argv);
    QDialog *window = new QDialog;
    Ui::ImageDialog ui;
    ui.setupUi(window);

    window->show();
    return app.exec();
}
