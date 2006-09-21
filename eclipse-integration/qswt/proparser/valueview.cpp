#include "valueview.h"

ValueView::ValueView(QWidget *parent)
    : ValueEditor(parent)
{
    QPalette pal = palette();
    pal.setColor(QPalette::Background, Qt::white);
    setPalette(pal);
}

ValueView::~ValueView()
{

}

int ValueView::handle()
{
    // only works on 32-bit systems...
    return (int)this;
}
