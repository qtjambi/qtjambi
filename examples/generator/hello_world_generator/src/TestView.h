#ifndef TESTVIEW_H
#define TESTVIEW_H

#include <QtCore/QDebug>
#include <QtGui/QGraphicsView>
#include "TestItem.h"
#include <QtGui/QWidget>

class TestView : public QGraphicsView {

 private:
  TestItem* item;

 public:
  TestView(QGraphicsScene *scene, QWidget *parent = 0);
  ~TestView() {
    qDebug() << "~TestView() on " << this;
  }
  TestItem* getItem();

};


#endif
