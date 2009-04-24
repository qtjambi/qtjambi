#ifndef QTJAMBITEXTOBJECTINTERFACE_H
#define QTJAMBITEXTOBJECTINTERFACE_H

#include <QtCore/QObject>
#include <QtGui/QTextObjectInterface>

// NOTE:
// This class intentionally inherits QTextObjectInterface, which is a
// class not included in the Qt Jambi type system. This is part of a trick
// to get a Java API which is as similar as possible to the unportable
// Q_DECLARE_INTERFACE() API which is used in Qt. The pure virtual functions
// are redeclared here solely so that the generator will generate code for them.
class QtJambiTextObjectInterface: public QObject, public QTextObjectInterface
{
    Q_OBJECT
    Q_INTERFACES(QTextObjectInterface)
public:
    virtual QSizeF intrinsicSize(QTextDocument *doc, int posInDocument, const QTextFormat &format) = 0;
    virtual void drawObject(QPainter *painter, const QRectF &rect, QTextDocument *doc, int posInDocument, const QTextFormat &format) = 0;
};

#endif // QTJAMBITEXTOBJECTINTERFACE_H
