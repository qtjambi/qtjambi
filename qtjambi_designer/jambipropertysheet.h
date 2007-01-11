#ifndef JAMBIPROPERTYSHEET_H
#define JAMBIPROPERTYSHEET_H

#include <QtDesigner/QtDesigner>

class JambiLanguagePlugin;

class JambiPropertySheet: public QObject, public QDesignerPropertySheetExtension
{
    Q_OBJECT
    Q_INTERFACES(QDesignerPropertySheetExtension)
public:
    JambiPropertySheet(QObject *parent);

    // Reimplement property/setProperty to do enum/flags magic and force
    // user to reimplement read/write instead...
    virtual QVariant property(int index) const;
    virtual void setProperty(int index, const QVariant &value);

    virtual QVariant readProperty(int index) const = 0;
    virtual void writeProperty(int index, const QVariant &value) = 0;

};

#endif
