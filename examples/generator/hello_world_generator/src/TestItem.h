#ifndef __TESTITEM_H__
#define __TESTITEM_H__

#include <QObject>
#include <QGraphicsScene>

class TestItem : public QObject {

    Q_OBJECT

    public:
        TestItem(QGraphicsScene *scene);
        int getId();

};
#endif
