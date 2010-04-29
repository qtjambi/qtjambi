
#include "TestItem.h"
#include <QGraphicsScene>
#include <QLineF>
#include <QPainter>
#include <QStyleOptionGraphicsItem>
#include <QWidget>

TestItem::TestItem(QGraphicsScene *scene) : TestSuper(scene) {}

int TestItem::getId() {
  return -1;

}


