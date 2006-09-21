#ifndef VALUEVIEW_H
#define VALUEVIEW_H

#include "valueeditor.h"
#include "qswt.h"

class DetailsView;
class ProFile;
class ProEditorModel;

class ValueView : public ValueEditor
{
    Q_OBJECT
    Q_CLASSINFO("ClassID", "{37E5BAE5-3BBC-48E3-A7E9-BA0C54A1ECC3}")
    Q_CLASSINFO("InterfaceID", "{7E3A89DD-400B-49D1-8484-F5CF741A3754}")
    Q_CLASSINFO("EventsID", "{1FD12327-7618-4C81-82E2-EBC4C30B4EC7}")
    Q_CLASSINFO("ToSuperClass", "ValueView")

public:
    ValueView(QWidget *parent = 0);
    ~ValueView();

public slots:
    int handle();
};
    
#endif //VALUEVIEW_H
