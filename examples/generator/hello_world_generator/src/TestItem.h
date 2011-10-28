#ifndef __TESTITEM_H__
#define __TESTITEM_H__

#include <QtCore/QObject>
#include <QtGui/QGraphicsScene>
#include "TestSuper.h"

class TestItem : public TestSuper {

    Q_OBJECT

    public:
        TestItem(QGraphicsScene *scene);
        int getId();

};
#endif
