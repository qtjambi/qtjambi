
#include "TestSuper.h"
#include <QDebug>        
#include <QGraphicsScene>
#include <QLineF>
#include <QPainter>
#include <QStyleOptionGraphicsItem>
#include <QWidget>

TestSuper::TestSuper(QGraphicsScene *scene) : QGraphicsObject() {
  qDebug() << "TestSuper::TestSuper(scene=" << scene << ") on " << this;
  if (scene) {
    scene->addItem(this);
  }
}

QRectF TestSuper::boundingRect() const {
  qDebug() << "TestSuper::boundingRect() on " << this;
  return QRectF(10, 10, 10, 10);
}

void TestSuper::paint( QPainter* painter, const QStyleOptionGraphicsItem*, QWidget* widget) {
  qDebug() << "TestSuper::paint(painter=" << painter << ", item=???, widget=" << widget << ") on " << this;

  QLineF line(0, 0, 10, 10);
  painter->drawLine(line);
  QLineF line2(10, 0, 0, 10);
  painter->drawLine(line2);

}

