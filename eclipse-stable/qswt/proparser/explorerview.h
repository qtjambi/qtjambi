#ifndef EXPLORERVIEW_H
#define EXPLORERVIEW_H

#include <QtGui/QTreeView>
#include "qswt.h"

class ProFile;
class ProEditorModel;
class ProItemInfoManager;
class DetailsView;
class ValueView;

class ExplorerView : public QTreeView
{
    Q_OBJECT
    Q_CLASSINFO("ClassID", "{62D0CA85-68E7-40D8-B65A-455F232A2F54}")
    Q_CLASSINFO("InterfaceID", "{56398B5A-2395-4342-8B8E-05C70A5EEF70}")
    Q_CLASSINFO("EventsID", "{CF722267-3CEA-4CD2-99C9-27B98B0048D8}")
    Q_CLASSINFO("ToSuperClass", "ExplorerView")

public:
    ExplorerView(QWidget *parent = 0);
    ~ExplorerView();

public slots:
    int createAndShowModel(const QString &file);
    void showModel(int model);
    bool save();

    void enableAdvanced(bool enabled);
    bool isDirty() const;

    void setDetailsViewHandle(int hview);
    void setValueViewHandle(int hview);

    QString contents() const;

signals:
    void changed();

private slots:
    void modelReset();

private:
    ProFile *m_profile;
    ProItemInfoManager *m_infomanager;
    ProEditorModel *m_model;

    DetailsView *m_detailsView;
    ValueView *m_valueView;
};

#endif //EXPLORERVIEW_H
