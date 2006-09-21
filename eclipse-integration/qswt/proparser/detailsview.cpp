#include "detailsview.h"

DetailsView::DetailsView(QWidget *parent)
    : ProEditor(parent)
{
    QPalette pal = palette();
    pal.setColor(QPalette::Background, Qt::white);
    setPalette(pal);
}

DetailsView::~DetailsView()
{

}

int DetailsView::handle()
{
    // only works on 32-bit systems...
    return (int)this;
}
