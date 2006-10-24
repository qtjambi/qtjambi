#include <QtGui/QPalette>
#include <QtGui/QLayout>
#include <QtGui/QFrame>
#include <QtGui/QPainter>
#include <qdebug.h>

#include "widgethost.h"
#define MARGIN 10
#define MIN_FORM_WIDTH 64
#define MIN_FORM_HEIGHT 64

/* ### Currently, the Designer integration has hardcoded dependencies on the window container
   being either a top level window or a QWorkspaceChild. This makes goofy effects
   when setting the geometry of a form using the property editor. We know this is a WTF, 
   so it will not go into the final release, but it will fly until the designer bug is fixed. */
class FakeWidget: public QWidget
{
    static const QMetaObject staticMetaObject;
    virtual const QMetaObject *metaObject() const;
    
public:
    inline FakeWidget(QWidget *parent) : QWidget(parent) 
    {
    }

};

static const uint qt_meta_data_FakeWidget[] = {
       1,       // revision
       0,       // classname
       0,    0, // classinfo
       0,    0, // methods
       0,    0, // properties
       0,    0, // enums/sets
       0        // eod
};

static const char designer_workaround[] = {
    "QWorkspaceChild\0"
};

const QMetaObject FakeWidget::staticMetaObject = {
    { &QFrame::staticMetaObject, 
      designer_workaround,
      qt_meta_data_FakeWidget, 0 }
};

const QMetaObject *FakeWidget::metaObject() const
{
    return &staticMetaObject;
}

WidgetHost::WidgetHost(QWidget *parent)
: QScrollArea(parent)
{
    formWindow = 0;
    fakeWidget = new FakeWidget(this);
    fakeWidget->setMinimumSize(MIN_FORM_WIDTH, MIN_FORM_HEIGHT);
    setWidget(fakeWidget);

    QVBoxLayout *mainLayout = new QVBoxLayout(fakeWidget);
    mainLayout->setMargin(MARGIN);
    formWindowFrame = new QFrame(fakeWidget);
    formWindowFrame->setFrameStyle(QFrame::Panel | QFrame::Raised);
    mainLayout->addWidget(formWindowFrame);
}

void WidgetHost::setFormWindow(QWidget *fw)
{
    if (!fw)
        return;

    formWindow = fw;
    QVBoxLayout *layout = new QVBoxLayout(formWindowFrame);
    layout->setMargin(0);
    layout->setSpacing(0);
    layout->addWidget(formWindow);
    fakeWidget->resize(formWindowSize());

    setBackgroundRole(QPalette::Base);
    formWindow->setBackgroundRole(QPalette::Background);

    selection = new FormWindowSelection(formWindowFrame);
    connect(selection, SIGNAL(formWindowSizeChanged(const QRect &, const QRect &)),
        this, SLOT(fwSizeWasChanged(const QRect &, const QRect &)));
}

void WidgetHost::updateFormWindowGeometry(const QRect &r)
{
    fakeWidget->resize(r.width() + 2 * MARGIN + 2, r.height() + 2 * MARGIN + 2);
}

QSize WidgetHost::formWindowSize() const
{
    if (!formWindow)
        return QSize();
    return formWindow->size();
}

void WidgetHost::setFormWindowSize(const QSize &s)
{
    updateFormWindowGeometry(QRect(0, 0, s.width(), s.height()));
}

void WidgetHost::fwSizeWasChanged(const QRect &, const QRect &new_rect)
{
    // newGeo is the mouse coordinates, thus moving the Right will actually emit wrong height
    emit formWindowSizeChanged(formWindow->size().width(), formWindow->size().height());
}

void WidgetHost::selectFormWindow()
{
    selection->show();
}

void WidgetHost::unSelectFormWindow()
{
    selection->hide();
}

FormWindowSelection::FormWindowSelection(QWidget *frame)
{
    formWindowFrame = frame;
    for (int i = SizeHandleRect::LeftTop; i <= SizeHandleRect::Left; ++i) {
        SizeHandleRect *shr = new SizeHandleRect(formWindowFrame->parentWidget(),
            (SizeHandleRect::Direction)i, this);
        shr->setWidget(formWindowFrame);
        handles.insert(i, shr);
        connect(shr, SIGNAL(mouseButtonReleased(const QRect &, const QRect &)),
            this, SIGNAL(formWindowSizeChanged(const QRect &, const QRect &)));
    }
    show();
    formWindowFrame->installEventFilter(this);    
    updateGeometry();
}

bool FormWindowSelection::eventFilter(QObject *, QEvent *e)
{
    if (e->type() == QEvent::Resize) {
        QResizeEvent *re = static_cast<QResizeEvent *>(e);
        if (re->size() != formWindowFrame->size())
            emit formWindowSizeChanged(formWindowFrame->rect(), QRect(QPoint(0, 0), re->size()));
        formWindowFrame->resize(formWindowFrame->size().expandedTo(re->size()));
        updateGeometry();
    }
    return false;
}

void FormWindowSelection::updateGeometry()
{
    QPoint p = formWindowFrame->pos();
    QRect r(p, formWindowFrame->size());

    int w = 6;
    int h = 6;

    for (int i = SizeHandleRect::LeftTop; i <= SizeHandleRect::Left; ++i) {
        SizeHandleRect *hndl = handles[ i ];
        if (!hndl)
            continue;
        switch (i) {
        case SizeHandleRect::LeftTop:
            hndl->move(r.x() - w / 2, r.y() - h / 2);
            break;
        case SizeHandleRect::Top:
            hndl->move(r.x() + r.width() / 2 - w / 2, r.y() - h / 2);
            break;
        case SizeHandleRect::RightTop:
            hndl->move(r.x() + r.width() - w / 2 + 1, r.y() - h / 2);
            break;
        case SizeHandleRect::Right:
            hndl->move(r.x() + r.width() - w / 2 + 1, r.y() + r.height() / 2 - h / 2);
            break;
        case SizeHandleRect::RightBottom:
            hndl->move(r.x() + r.width() - w / 2 + 1, r.y() + r.height() - h / 2);
            break;
        case SizeHandleRect::Bottom:
            hndl->move(r.x() + r.width() / 2 - w / 2, r.y() + r.height() - h / 2 + 1);
            break;
        case SizeHandleRect::LeftBottom:
            hndl->move(r.x() - w / 2, r.y() + r.height() - h / 2 + 1);
            break;
        case SizeHandleRect::Left:
            hndl->move(r.x() - w / 2, r.y() + r.height() / 2 - h / 2 + 1);
            break;
        default:
            break;
        }
    }
}

void FormWindowSelection::hide()
{
    for (int i = SizeHandleRect::LeftTop; i <= SizeHandleRect::Left; ++i) {
        SizeHandleRect *h = handles[ i ];
        if (h)
            h->hide();
    }
}

void FormWindowSelection::show()
{
    for (int i = SizeHandleRect::LeftTop; i <= SizeHandleRect::Left; ++i) {
        SizeHandleRect *h = handles[ i ];
        if (h) {
            h->show();
            h->raise();
        }
    }
}

void FormWindowSelection::update()
{
    for (int i = SizeHandleRect::LeftTop; i <= SizeHandleRect::Left; ++i) {
        SizeHandleRect *h = handles[ i ];
        if (h)
            h->update();
    }
}

SizeHandleRect::SizeHandleRect(QWidget *parent, Direction d, FormWindowSelection *s)
    : QWidget(parent)
{
    setBackgroundRole(QPalette::Dark);
    setAutoFillBackground(true);
    setFixedSize(6, 6);
    dir = d ;
    setMouseTracking(false);
    sel = s;
    updateCursor();
}

void SizeHandleRect::updateCursor()
{
    switch (dir) {
    case Right:
    case RightTop:
        setCursor(Qt::SizeHorCursor);
        return;
    case RightBottom:
        setCursor(Qt::SizeFDiagCursor);
        return;
    case LeftBottom:
    case Bottom:
        setCursor(Qt::SizeVerCursor);
        return;
    default:
        break;
    }

    setCursor(Qt::ArrowCursor);
}

void SizeHandleRect::paintEvent(QPaintEvent *)
{
    QPainter p(this);
    p.fillRect(0, 0, width(), height(), Qt::blue);
}

void SizeHandleRect::mousePressEvent(QMouseEvent *e)
{
    e->accept();

    if (e->button() != Qt::LeftButton)
        return;

    QWidget *container = widget->parentWidget();

    oldPos = QPoint(widget->size().width(), widget->size().height());
    curPos = container->mapFromGlobal(e->globalPos());
}

void SizeHandleRect::mouseMoveEvent(QMouseEvent *e)
{
    if (!(e->buttons() & Qt::LeftButton))
        return;

    QWidget *container = widget->parentWidget();
    curPos = container->mapFromGlobal(e->globalPos());

    switch (dir) {
        case Right:
        case RightTop:
            if (curPos.x() < MIN_FORM_WIDTH)
                return;
            tryResize(widget, curPos.x(), oldPos.y()+MARGIN);
            break;
        case RightBottom:
            if (curPos.x() < MIN_FORM_WIDTH || curPos.y() < MIN_FORM_HEIGHT)
                return;
            tryResize(widget, curPos.x(), curPos.y());
            break;
        case LeftBottom:
        case Bottom:
            if (curPos.y() < MIN_FORM_HEIGHT)
                return;
            tryResize(widget, oldPos.x()+MARGIN, curPos.y());
            break;
        default:
            break;
    }
    sel->updateGeometry();
}

void SizeHandleRect::mouseReleaseEvent(QMouseEvent *e)
{
    if (e->button() != Qt::LeftButton)
        return;

    e->accept();

    emit mouseButtonReleased(
        QRect(0, 0, oldPos.x(), oldPos.y()),
        QRect(0, 0, curPos.x(), curPos.y()));
}

void SizeHandleRect::tryResize(QWidget *w, int width, int height)
{
    int minw = qMax(w->minimumSizeHint().width(), w->minimumSize().width());
    minw = qMax(minw, MIN_FORM_WIDTH);
    int minh = qMax(w->minimumSizeHint().height(), w->minimumSize().height());
    minh = qMax(minh, MIN_FORM_HEIGHT);
    w->parentWidget()->resize(qMax(minw, width)+ MARGIN, qMax(minh, height)+ MARGIN);
}

void SizeHandleRect::setWidget(QWidget *w)
{
    widget = w;
}
