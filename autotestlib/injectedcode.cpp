#include "injectedcode.h"

void GraphicsSceneSubclass::drawItems(QPainter *painter,
                                     int numItems,
                                     QGraphicsItem *items[],
                                     const QStyleOptionGraphicsItem options[],
                                     QWidget *widget)
{
    Q_UNUSED(painter)
    Q_UNUSED(widget)
    this->numItems = numItems;

    firstBoundingRect = items[0]->boundingRect();
    secondBoundingRect = items[1]->boundingRect();

    firstStyleOptionType = options[0].type;
    firstStyleOptionVersion = options[0].version;

    secondStyleOptionType = options[1].type;
    secondStyleOptionVersion = options[1].version;

    firstItem = items[0];
    secondItem = items[1];

    firstStyleOption = options[0];
    secondStyleOption = options[1];
}

