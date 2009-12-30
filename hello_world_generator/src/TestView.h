#ifndef TESTVIEW_H
#define TESTVIEW_H

#include <QGraphicsView>
#include "TestItem.h"
#include <QWidget>

class TestView : public QGraphicsView {

 private:
  TestItem* item;

 public:
  TestView(QGraphicsScene *scene, QWidget *parent = 0);
  TestItem* getItem();

};


#endif
