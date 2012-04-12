
#include "TestView.h"
#include <QDebug>        
#include <QGraphicsView>
#include <QGraphicsScene>
#include <QWidget>

TestView::TestView( QGraphicsScene *scene, QWidget *parent ) : QGraphicsView(scene, parent) {
  qDebug() << "TestView::TestView(scene=" << scene << ", parent=" << parent << ") on " << this;
  item = new TestItem(scene);
}

TestItem*
TestView::getItem() {
  qDebug() << "TestView::getItem() on " << this;
  return item;
}

