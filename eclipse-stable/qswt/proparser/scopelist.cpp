#include <QtCore/QDir>
#include <QtGui/QHeaderView>

#include "scopelist.h"
#include "proeditormodel.h"
#include "proreader.h"
#include "proitems.h"

ScopeList::ScopeList(QWidget *parent)
    : QTreeView(parent)
{
    header()->setVisible(false);
}

ScopeList::~ScopeList()
{
    QSet<ProEditorModel *> knownmodels = m_createdModels.keys().toSet();
    qDeleteAll(knownmodels - m_nodeletemodel);
}

int ScopeList::createModel(const QString &file)
{
    ProReader reader;
    ProFile *profile = reader.read(file);

    if (!profile)
        return 0;

    ProEditorModel *model = new ProEditorModel();
    m_createdModels.insert(model, 0);

    model->setProFiles(QList<ProFile *>() << profile);
    return (int)model;
}

void ScopeList::showModel(int hmodel, bool enabled)
{
    if (hmodel) {
        ProEditorModel *em = (ProEditorModel*)hmodel;
        ProScopeFilter *mf = filter(em);

        if (mf == model())
            return;

        setModel(mf);
        expandAll();
    }

    setEnabled(enabled);
}

void ScopeList::selectFirstVariable()
{
    if (ProScopeFilter *mf = qobject_cast<ProScopeFilter *>(model())) {
        ProEditorModel *m = qobject_cast<ProEditorModel*>(mf->sourceModel());

        QStringList vars = m_files.keys();
        for (int i=0; i<vars.count(); ++i) {
            QList<QModelIndex> indexes = m->findVariables(QStringList(vars.at(i)));
            if (!indexes.isEmpty()) {
                mf->setData(mf->mapFromSource(indexes.first()),
                    QVariant((int)Qt::Checked), Qt::CheckStateRole);
            }
        }
    }
}

void ScopeList::addFile(const QString &file, const QString &var)
{
    QFileInfo info(file);
    m_files.insert(var, info);
    m_filenames << info.fileName();
}

bool ScopeList::search(int model)
{
    bool found = false;
    ProEditorModel *m = (ProEditorModel*)model;
    ProScopeFilter *mf = filter(m);

    QList<QModelIndex> indexes = m->findVariables(m_files.keys());
    for (int i=0; i<indexes.size(); ++i) {
        QModelIndex varindex = indexes.at(i);
        for (int j=m->rowCount(varindex) - 1; j>=0; --j) {
            QModelIndex valindex = m->index(j,0,varindex);
            ProItem *item = m->proItem(valindex);
            if (!item || item->kind() != ProItem::ValueKind)
                continue;
            ProValue *val = static_cast<ProValue *>(item);
            if (m_filenames.contains(QFileInfo(val->value()).fileName())) {
                found = true;
                mf->setData(mf->mapFromSource(varindex),
                    QVariant((int)Qt::Checked), Qt::CheckStateRole);
                break;
            }
        }
    }

    return found;
}

bool ScopeList::changed(int model)
{
    ProEditorModel *m = (ProEditorModel*)model;
    ProScopeFilter *mf = filter(m);
    return !mf->checkedIndexes().isEmpty();
}

void ScopeList::removeFiles()
{
    QList<ProScopeFilter *> filters = m_createdModels.values();

    // for each project file
    for (int i=0; i<filters.count(); ++i) {
        ProEditorModel *m = qobject_cast<ProEditorModel*>(filters.at(i)->sourceModel());
        QList<QModelIndex> indexes = filters.at(i)->checkedIndexes();
        if (!indexes.isEmpty())
            m_nodeletemodel.insert(m);

        // for each variable in the project file
        for (int j=0; j<indexes.size(); ++j) {
            QModelIndex varindex = indexes.at(j);

            // for each value in the variable
            for (int k=m->rowCount(varindex) - 1; k>=0; --k) {
                QModelIndex valindex = m->index(k,0,varindex);
                ProItem *item = m->proItem(valindex);
                if (!item || item->kind() != ProItem::ValueKind)
                    continue;
                ProValue *val = static_cast<ProValue *>(item);
                if (m_filenames.contains(QFileInfo(val->value()).fileName())) {
                    m->removeItem(valindex);
                }
            }
        }
    }
}

void ScopeList::addFiles()
{
    QList<ProScopeFilter *> filters = m_createdModels.values();
    foreach(ProScopeFilter *mf, filters) {
        ProEditorModel *m = qobject_cast<ProEditorModel*>(mf->sourceModel());

        QFileInfo profile(m->proFiles().first()->fileName());

        QList<QModelIndex> indexes = mf->checkedIndexes();
        if (!indexes.isEmpty())
            m_nodeletemodel.insert(m);

        for (int i=0; i<indexes.size(); ++i) {
            QModelIndex index = indexes.at(i);
            ProVariable *var = m->proVariable(index);
            QList<QFileInfo> files = m_files.values(var->variable());
            for (int j=0; j<files.size(); ++j) {
                QString val = profile.dir().relativeFilePath(files.at(j).absoluteFilePath());
                m->insertItem(new ProValue(val.toUtf8(), var), 0, index);
            }
        }
    }
}

ProScopeFilter *ScopeList::filter(ProEditorModel *model)
{
    if (!m_createdModels.contains(model)) {
        m_createdModels.insert(model, 0);
        m_nodeletemodel.insert(model);
    }

    if (!m_createdModels.value(model)) {
        ProScopeFilter *filter = new ProScopeFilter(this);
        filter->setVariableCheckable(true);
        filter->setSourceModel(model);
        filter->setVariableFilter(m_files.keys());
        m_createdModels[model] = filter;
    }

    return m_createdModels.value(model);
}
