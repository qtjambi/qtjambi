#include "detailsview.h"
#include "proeditormodel.h"
#include "procommandmanager.h"

DetailsView::DetailsView(QWidget *parent)
    : ProEditor(parent, false)
{
    setAutoFillBackground(true);
    QPalette pal = palette();
    pal.setColor(QPalette::Background, Qt::white);
    setPalette(pal);
}

void DetailsView::initialize(ProEditorModel *model, ProItemInfoManager *infomanager)
{
    ProEditor::initialize(model, infomanager);

    m_actions.insert(CUT_ACTION, m_cutAction);
    m_actions.insert(COPY_ACTION, m_copyAction);
    m_actions.insert(PASTE_ACTION, m_pasteAction);

    connect(m_cutAction, SIGNAL(changed()),
        this, SLOT(actionChanged()));

    connect(m_copyAction, SIGNAL(changed()),
        this, SLOT(actionChanged()));

    connect(m_pasteAction, SIGNAL(changed()),
        this, SLOT(actionChanged()));

    QAction *action = new QAction(tr("Undo"), this);
    m_actions.insert(UNDO_ACTION, action);

    connect(action, SIGNAL(changed()),
        this, SLOT(actionChanged()));

    connect(action, SIGNAL(triggered()),
        m_model->cmdManager(), SLOT(undo()));

    action = new QAction(tr("Redo"), this);
    m_actions.insert(REDO_ACTION, action);

    connect(action, SIGNAL(changed()),
        this, SLOT(actionChanged()));

    connect(action, SIGNAL(triggered()),
        m_model->cmdManager(), SLOT(redo()));

    connect(m_model->cmdManager(), SIGNAL(modified()),
        this, SLOT(commandManagerChanged()));

    commandManagerChanged();
}

DetailsView::~DetailsView()
{

}

int DetailsView::handle()
{
    // only works on 32-bit systems...
    return (int)this;
}

bool DetailsView::isActionEnabled(int id)
{
    return m_actions.at(id)->isEnabled();
}

void DetailsView::triggerAction(int id)
{
    m_actions.at(id)->trigger();
}

void DetailsView::actionChanged()
{
    emit actionChanged(m_actions.indexOf(qobject_cast<QAction*>(sender())));
}

void DetailsView::commandManagerChanged()
{
    m_actions.at(UNDO_ACTION)->setEnabled(m_model->cmdManager()->canUndo());
    m_actions.at(REDO_ACTION)->setEnabled(m_model->cmdManager()->canRedo());
}
