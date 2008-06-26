
#include <QtGui>

#include "hellogenerator.h"

void HelloGenerator::hello()
{
    QPushButton *helloButton = new QPushButton("Hello Generator.");
    helloButton->show();
}

