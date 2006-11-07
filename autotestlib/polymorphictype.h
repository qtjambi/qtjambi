#ifndef POLYMORPHICTYPE_H
#define POLYMORPHICTYPE_H

#include <QtGui/QStyleOption>
#include <QtGui/QApplication>
#include <QtCore/QEvent>
#include <QtGui/QPaintEvent>
#include <QtGui/QWidget>
#include <QtGui/QPainter>
#include <QtGui/QImage>

class CustomEvent: public QEvent
{
public:
    CustomEvent(int something) : QEvent(QEvent::Type(QEvent::User + 1)) { m_something = something; }

    int m_something;
};

class CustomStyleOption: public QStyleOption
{
public:
    CustomStyleOption(const CustomStyleOption &other) : QStyleOption(other)
    {
        type = QStyleOption::SO_CustomBase + 1;
        m_something = other.m_something;
    }
    CustomStyleOption() { m_something = 0; }
    CustomStyleOption(int something) : QStyleOption(1, QStyleOption::SO_CustomBase + 1) { m_something = something; }

    int m_something;
};

class UnmappedCustomStyleOption: public QStyleOption
{
public:
    UnmappedCustomStyleOption(const UnmappedCustomStyleOption &other) : QStyleOption(other)
    {
        type = QStyleOption::SO_CustomBase + 2;
        m_something_else = other.m_something_else;
    }
    UnmappedCustomStyleOption() : QStyleOption(1, QStyleOption::SO_CustomBase + 2) { }
    int m_something_else;
};

class PolymorphicType
{
public:
    static QEvent *getPaintEvent() { return new QPaintEvent(QRect(10, 10, 10, 10)); }
    static QEvent *getCustomEvent(int i) { return new CustomEvent(i); }
    static void sendPaintEvent(QWidget *w) { QApplication::sendEvent(w, getPaintEvent()); }
    static void sendCustomEvent(QWidget *w, int i) { QApplication::sendEvent(w, getCustomEvent(i)); }
    
    static QStyleOption *getButtonStyleOption() { return new QStyleOptionButton(); }
    static QStyleOption *getCustomStyleOption(int i) { return new CustomStyleOption(i); }
    static QStyleOption *getUnmappedCustomStyleOption() { return new UnmappedCustomStyleOption(); }
    static void sendButtonStyleOption(QWidget *w) 
    { 
        QImage img;
        QPainter p(&img);
        w->style()->drawControl(QStyle::ControlElement(QStyle::CE_PushButton), 
                                           getButtonStyleOption(),
                                           &p);
    }
    static void sendCustomStyleOption(QWidget *w, int i) 
    { 
        QImage img;
        QPainter p(&img);
        w->style()->drawControl(QStyle::ControlElement(QStyle::CE_CustomBase + 1), 
                                           getCustomStyleOption(i),
                                           &p);
    }
    static void sendUnmappedCustomStyleOption(QWidget *w) 
    { 
        QImage img;
        QPainter p(&img);
        w->style()->drawControl(QStyle::ControlElement(QStyle::CE_CustomBase + 2), 
                                           getUnmappedCustomStyleOption(),
                                           &p);
    }
    

};

#endif //POLYMORPHICTYPE_H