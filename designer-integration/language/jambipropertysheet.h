#ifndef JAMBIPROPERTYSHEET_H
#define JAMBIPROPERTYSHEET_H

#include "jnilayer.h"

#include <QtDesigner/QtDesigner>
#include <QtDesigner/private/qdesigner_propertysheet_p.h>

class JambiLanguagePlugin;

class JambiPropertySheet: public QDesignerPropertySheet
{
    Q_OBJECT

public:
    JambiPropertySheet(JambiLanguagePlugin *jambi, QObject *object, QObject *parent);
    virtual ~JambiPropertySheet();

    JambiLanguagePlugin *jambi() const { return m_jambi; }

    virtual int count() const;
    virtual bool hasReset(int index) const;
    virtual int indexOf(const QString & name) const;
    virtual bool isAttribute(int index) const;
    virtual bool isChanged(int index) const;
    virtual bool isVisible(int index) const;
    virtual QVariant property(int index) const;
    virtual QString propertyGroup(int index) const;
    virtual QString propertyName(int index) const;
    virtual bool reset(int index);
    virtual void setAttribute(int index, bool attribute);
    virtual void setChanged(int index, bool changed);
    virtual void setProperty(int index, const QVariant & value);
    virtual void setPropertyGroup(int index, const QString & group);
    virtual void setVisible(int index, bool visible);

private:
    void buildPropertySheet();

    QString callStringMethod_int(jmethodID mid, int i) const;
    bool callBoolMethod(jmethodID mid, int i) const;
    void call_int_bool(jmethodID mid, int i, bool b) const;

    JambiLanguagePlugin *m_jambi;
    QDesignerLanguageExtension *m_language;

    jobject m_property_sheet;
};


#endif
