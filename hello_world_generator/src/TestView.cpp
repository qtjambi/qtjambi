
#include "TestView.h"
#include <QGraphicsView>
#include <QGraphicsScene>
#include <QWidget>

TestView::TestView( QGraphicsScene *scene, QWidget *parent ) : QGraphicsView(scene, parent) {
  item = new TestItem(scene);
}

TestItem*
TestView::getItem() {
  return item;
}

