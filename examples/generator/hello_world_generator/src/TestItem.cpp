
#include "TestItem.h"
#include <QDebug>
#include <QGraphicsScene>
#include <QLineF>
#include <QPainter>
#include <QStyleOptionGraphicsItem>
#include <QWidget>

TestItem::TestItem(QGraphicsScene *scene) : TestSuper(scene) {
  qDebug() << "TestItem::TestItem(scene=" << scene << ") on " << this;
}

int TestItem::getId() {
  qDebug() << "TestItem::getId() on " << this;
  return -1;

}


