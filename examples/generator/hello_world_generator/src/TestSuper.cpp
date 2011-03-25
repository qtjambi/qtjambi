
#include "TestSuper.h"
#include <QGraphicsScene>
#include <QLineF>
#include <QPainter>
#include <QStyleOptionGraphicsItem>
#include <QWidget>

TestSuper::TestSuper(QGraphicsScene *scene) : QGraphicsObject() {

  if (scene) {
    scene->addItem(this);
  }
}

QRectF TestSuper::boundingRect() const {
  return QRectF(10, 10, 10, 10);
}

void TestSuper::paint( QPainter* painter, const QStyleOptionGraphicsItem*, QWidget* widget) {

  QLineF line(0, 0, 10, 10);
  painter->drawLine(line);
  QLineF line2(10, 0, 0, 10);
  painter->drawLine(line2);

}

