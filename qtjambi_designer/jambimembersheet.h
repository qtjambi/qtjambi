#ifndef JAMBIMEMBERSHEET_H
#define JAMBIMEMBERSHEET_H

#include <QtDesigner/QtDesigner>

class JambiLanguagePlugin;

class JambiMemberSheet: public QObject, public QDesignerMemberSheetExtension
{
    Q_OBJECT
    Q_INTERFACES(QDesignerMemberSheetExtension)
public:
    JambiMemberSheet(QObject *parent);
};

#endif
