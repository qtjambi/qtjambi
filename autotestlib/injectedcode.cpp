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

void AccessibleTableInterfaceSubclass::cellAtIndex(int index, int *row, int *column, int *rowSpan,
                                                   int *columnSpan, bool *isSelected)
{
    if (row != 0) *row = index + 1;
    if (column != 0) *column = index + 2;
    if (rowSpan != 0) *rowSpan = index + 3;
    if (columnSpan != 0) *columnSpan = index + 4;
    if (isSelected != 0) *isSelected = (index % 2) == 0;
}

void AccessibleTableInterfaceSubclass::callCellAtIndex(AccessibleTableInterfaceSubclass *obj, int index, int *row, int *col, int *rowSpan, int *columnSpan, bool *isSelected)
{
    obj->cellAtIndex(index, row, col, rowSpan, columnSpan, isSelected);
}