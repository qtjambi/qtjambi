#ifndef DETAILSVIEW_H
#define DETAILSVIEW_H

#include "qswt.h"
#include "proeditor.h"

class DetailsView : public ProEditor
{
    Q_OBJECT

    Q_CLASSINFO("ClassID", "{4E7A7683-803D-4C03-B1EE-CF4958F0C0DE}")
    Q_CLASSINFO("InterfaceID", "{213124D2-936D-46D4-99D4-1B578D2AC57D}")
    Q_CLASSINFO("EventsID", "{43707879-A2D2-4FAB-9597-F0D00E21391A}")
    Q_CLASSINFO("ToSuperClass", "DetailsView")

public:
    DetailsView(QWidget *parent = 0);
    ~DetailsView();

public slots:
    int handle();
};
    
#endif //DETAILSVIEW_H
