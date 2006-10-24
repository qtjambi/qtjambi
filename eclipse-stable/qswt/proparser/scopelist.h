#ifndef SCOPELIST_H
#define SCOPELIST_H

#include <QtCore/QSet>
#include <QtCore/QFileInfo>
#include <QtGui/QTreeView>

#include "qswt.h"

class ProScopeFilter;
class ProEditorModel;

class ScopeList : public QTreeView
{
    Q_OBJECT
    Q_CLASSINFO("ClassID", "{2B36A512-E024-46E3-A24B-79FDC6A1D9D3}")
    Q_CLASSINFO("InterfaceID", "{8A4F6357-A727-4A04-878A-67CC4AECA48A}")
    Q_CLASSINFO("EventsID", "{40140114-B20B-45BB-8F7E-2A6A1C551568}")
    Q_CLASSINFO("ToSuperClass", "ScopeList")

public:
    ScopeList(QWidget *parent = 0);
    ~ScopeList();

public slots:
    // creates a model
    int createModel(const QString &file);

    // displays the model and the selected variable
    void showModel(int model, bool enabled);

    // selects the first variable in the current model
    void selectFirstVariable();

    // searches for the files in the model
    // creates a filter model and checks the values
    // returns true if files is found
    bool search(int model);

    // check if a model is changed
    bool changed(int model);

    // removes the selected files
    void removeFiles();

    // adds the files to the specified variable
    void addFiles();

    // adds a file to the list of files to add or remove from
    // the specified variable
    void addFile(const QString &file, const QString &var);

private:
    ProScopeFilter *filter(ProEditorModel *model);

    QMap<ProEditorModel *, ProScopeFilter *> m_createdModels;
    QSet<ProEditorModel *> m_nodeletemodel;

    QMultiMap<QString, QFileInfo> m_files;
    QStringList m_filenames;
};
    
#endif //SCOPELIST_H
