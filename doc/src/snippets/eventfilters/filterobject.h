#ifndef FILTEROBJECT_H
#define FILTEROBJECT_H

#include <QObject>

class FilterObject : public QObject
{
    Q_OBJECT

public:
    FilterObject(QObject *parent = 0);
    bool eventFilter(QObject *object, QEvent *event);
    void setFilteredObject(QObject *object);

private:
    QObject *target;
};

#endif
