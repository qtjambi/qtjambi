#include <QtGui/QHeaderView>

#include "explorerview.h"
#include "detailsview.h"
#include "valueview.h"
#include "scopelist.h"

#include "proreader.h"
#include "prowriter.h"
#include "proeditormodel.h"
#include "procommandmanager.h"

#include "proitems.h"

ExplorerView::ExplorerView(QWidget *parent)
    : QTreeView(parent)
{
    m_profile = 0;
    m_valueView = 0;
    m_detailsView = 0;
    m_model = 0;

    header()->setVisible(false);

    setAutoFillBackground(true);
    QPalette pal = palette();
    pal.setColor(QPalette::Background, Qt::white);
    setPalette(pal);

    m_infomanager = new ProItemInfoManager(this);
}

ExplorerView::~ExplorerView()
{
    delete m_profile;
}

int ExplorerView::createAndShowModel(const QString &file)
{
    if (m_model)
        return 0;

    ProReader reader;
    ProFile *profile = reader.read(file);

    if (!profile)
        return 0;

    ProEditorModel *model = new ProEditorModel();
    model->setProFiles(QList<ProFile *>() << profile);

    showModel((int)model);

    return (int)model;
}

void ExplorerView::showModel(int model)
{
    if (m_model)
        return;

    m_model = (ProEditorModel *)model;
    m_model->setParent(this);

    m_profile = m_model->proFiles().first();

    connect(m_model->cmdManager(), SIGNAL(modified()),
        this, SIGNAL(changed()));

    connect(m_model, SIGNAL(modelReset()),
        this, SLOT(modelReset()));

    m_detailsView->initialize(m_model, m_infomanager);
    setModel(m_detailsView->filterModel());

    connect(selectionModel(),
        SIGNAL(currentChanged(const QModelIndex &, const QModelIndex &)),
        m_detailsView, SLOT(selectScope(const QModelIndex &)));

    connect(m_model, SIGNAL(modelReset()),
        this, SLOT(modelReset()));

    m_valueView->initialize(m_model, m_infomanager);

    connect(m_detailsView, SIGNAL(itemSelected(const QModelIndex &)),
        m_valueView, SLOT(editIndex(const QModelIndex &)));

    modelReset();
}

bool ExplorerView::save()
{
    if (!m_profile)
        return false;

    ProWriter writer;
    if (writer.write(m_profile, m_profile->fileName())) {
        m_model->cmdManager()->notifySave();
        emit changed();
        return true;
    }

    return false;
}

bool ExplorerView::isDirty() const
{
    return m_model->cmdManager()->isDirty();
}

void ExplorerView::enableAdvanced(bool enabled)
{
    if (enabled)
        m_model->setInfoManager(0);
    else
        m_model->setInfoManager(m_infomanager);
}

void ExplorerView::setDetailsViewHandle(int handle)
{
    m_detailsView = (DetailsView *)handle;
}

void ExplorerView::setValueViewHandle(int handle)
{
    m_valueView = (ValueView *)handle;
}

QString ExplorerView::contents() const
{
    if (!m_profile)
        return QString();

    ProWriter writer;
    return writer.contents(m_profile);
}

void ExplorerView::modelReset()
{
    expandAll();

    if (!model() || !m_detailsView)
        return;

    QModelIndex scope = model()->index(0,0);
    setCurrentIndex(scope);
    m_detailsView->selectScope(scope);
}

QSWT_MAIN_BEGIN("qtproparser", "com.trolltech.qtproject.pages",
    "{414BB33D-822C-4685-B3BC-1E70979934F8}", "{201EDEF9-2FE7-4B63-8939-51A327D41582}")
    QSWT_CLASS(ExplorerView, "explorerview.h", "explorerview.cpp")
    QSWT_CLASS(DetailsView, "detailsview.h", "detailsview.cpp")
    QSWT_CLASS(ValueView, "valueview.h", "valueview.cpp")
    QSWT_CLASS(ScopeList, "scopelist.h", "scopelist.cpp")
QSWT_MAIN_END()
