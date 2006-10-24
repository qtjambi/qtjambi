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

    enum {
        CUT_ACTION      = 0,
        COPY_ACTION     = 1,
        PASTE_ACTION    = 2,
        UNDO_ACTION     = 3,
        REDO_ACTION     = 4
    };

public:
    DetailsView(QWidget *parent = 0);
    ~DetailsView();

    void initialize(ProEditorModel *model, ProItemInfoManager *infomanager);

public slots:
    int handle();
    bool isActionEnabled(int id);
    void triggerAction(int id);

private slots:
    void actionChanged();
    void commandManagerChanged();

signals:
    void actionChanged(int id);

private:
    QList<QAction *> m_actions;
};
    
#endif //DETAILSVIEW_H
