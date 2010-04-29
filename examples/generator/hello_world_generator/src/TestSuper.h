#ifndef __TESTSUPER_H__
#define __TESTSUPER_H__

#include <QGraphicsObject>

class TestSuper : public QGraphicsObject {

 public:
  TestSuper(QGraphicsScene *scene);

 protected:
  virtual QRectF boundingRect() const;
  virtual void paint( QPainter* painter, const QStyleOptionGraphicsItem* option, QWidget* );

};
#endif
