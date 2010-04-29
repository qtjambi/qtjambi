#ifndef __TESTITEM_H__
#define __TESTITEM_H__

#include "TestSuper.h"

class TestItem : public TestSuper {

 public:
  TestItem(QGraphicsScene *scene);
  int getId();


};
#endif
