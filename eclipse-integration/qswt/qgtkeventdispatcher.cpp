#include <QApplication>

#include <QPalette>
#include <QColor>
#include <QFont>

#include "qgtkeventdispatcher.h"
#include <QX11EmbedWidget>

#include <gtk/gtkstyle.h>
#include <gtk/gtkwidget.h>
#include <gtk/gtkmain.h>

Atom QGtkEventDispatcher::_XEMBED = 0;
GSourceFuncs *QGtkEventDispatcher::qtEventFunctions = 0;
GSource *QGtkEventDispatcher::qtSource = 0;
QGtkEventDispatcher *QGtkEventDispatcher::self = 0;

QGtkEventDispatcher::QGtkEventDispatcher(QObject *parent)
        : QEventDispatcherUNIX(parent)
{
    doModal = false;
    self = this;
}

QGtkEventDispatcher::~QGtkEventDispatcher()
{
    // does not clean up too much. This should only be created
    // one time. Hopefully...
}

bool QGtkEventDispatcher::processEvents(QEventLoop::ProcessEventsFlags flags)
{
    Q_UNUSED(flags);
    // qt now takes charge of the event loop... (we fake a modal loop)
    doModal = true;
    g_main_context_iteration(NULL, true);
    doModal = false;

    return true;
}

void QGtkEventDispatcher::registerSocketNotifier(QSocketNotifier *notifier)
{
    qDebug("** QGtkEventDispatcher::registerSocketNotifier(QSocketNotifier*) not implemented");
    Q_UNUSED(notifier);
}

void QGtkEventDispatcher::unregisterSocketNotifier(QSocketNotifier *notifier)
{
    qDebug("** QGtkEventDispatcher::unregisterSocketNotifier(QSocketNotifier*) not implemented");
    Q_UNUSED(notifier);
}

void QGtkEventDispatcher::wakeUp()
{
    //not implemented
}

// returns -1 if there are no timers, 0 if it has a timer that needs to be activated,
// or the maximum time to wait for a timer.
int QGtkEventDispatcher::waitTime()
{
    int timeout = -1;

    QEventDispatcherUNIXPrivate *d =
        (QEventDispatcherUNIXPrivate *)d_ptr;

    timeval wait_tm = { 0l, 0l };
    if (d->timerWait(wait_tm))
    {
        timeout = (wait_tm.tv_sec*1000)
            + (wait_tm.tv_usec/1000);
    }

    return timeout;
}

// hooks qt into the gtk event loop
// returns true if qapp was created, false if it existed already.
bool QGtkEventDispatcher::hookQtIntoGtk(int gtksoc)
{
    if (!QApplication::instance())
    {
        Display *xdisp = GDK_DISPLAY();
        initXEmbedAtom(xdisp);

        qtEventFunctions = new GSourceFuncs;
        memset(qtEventFunctions, 0, sizeof(GSourceFuncs));
        (*qtEventFunctions).prepare = QGtkEventDispatcher::qteventprepare;
        (*qtEventFunctions).check = QGtkEventDispatcher::qteventcheck;
        (*qtEventFunctions).dispatch = QGtkEventDispatcher::qteventdispatch;
        (*qtEventFunctions).finalize = QGtkEventDispatcher::qteventfinalize;

        (void)new QGtkEventDispatcher();

        qtSource = g_source_new(qtEventFunctions, sizeof(GSource));
        g_source_attach(qtSource, NULL);

        gdk_window_add_filter(NULL, QGtkEventDispatcher::gdkXEventProc, NULL);
        (void)new QApplication(xdisp);
        qDebug("QApplication created\n");

        QGtkEventDispatcher::self->setPalette(gtksoc);

        // gtk does it like this...
        QFont::insertSubstitution("Sans", "Sans Serif");

        return true;
    }
    return false;
}

void QGtkEventDispatcher::setPalette(int gtksoc)
{
    GtkStyle *style = gtk_widget_get_style((GtkWidget *)gtksoc);
    GdkColor col = style->bg[GTK_STATE_NORMAL];

    QPalette qtpal(QColor(col.red>>8, col.green>>8, col.blue>>8));

    col = style->text[GTK_STATE_SELECTED];
    qtpal.setColor(QPalette::Normal, QPalette::HighlightedText,
        QColor(col.red>>8, col.green>>8, col.blue>>8));

    col = style->base[GTK_STATE_SELECTED];
    qtpal.setColor(QPalette::Normal, QPalette::Highlight,
        QColor(col.red>>8, col.green>>8, col.blue>>8));

    QApplication::setPalette(qtpal);

 //    qDebug(QString("%1, %2, %3").arg(col.red).arg(col.green).arg(col.blue).toLatin1());
}

// handles all the xevents that goes through the gtk event loop (this is not nice code)
GdkFilterReturn QGtkEventDispatcher::gdkXEventProc(GdkXEvent *xevent, GdkEvent *event, gpointer data)
{
    XEvent *e = (XEvent *)xevent;

    // this checks if gtk has a modal window/menu or something open...
    // we still get all the xevents and have to filter them
    if (gtk_grab_get_current() != 0) {
        switch (e->type) {
            // in case of a button click on an embedded qt widget while gtk has grab enabled,
            // we need to resend the xevent to the gtksocket. If we don't do this, clicking
            // on a qt widget while a gtk menu is open, will not close the menu (gtk does not
            // get the click event, because it does not care about qt winid's).
            case ButtonPress:
            case ButtonRelease: {
                XButtonEvent *be = (XButtonEvent*)e;
                QWidget *qw = QWidget::find (be->window);
                if (qw) {
                    // assume that all embedded widgets are top level widgets.
                    QX11EmbedWidget *xec = qobject_cast<QX11EmbedWidget *>(qw->topLevelWidget());
                    if (xec) {
                        be->time = be->time++;
                        be->window = xec->containerWinId();
                        XSendEvent(be->display, be->window, false, NoEventMask, (XEvent *)be);
                    }
                }
            }
            case MotionNotify:
            case KeyPress:
            case KeyRelease:
            case EnterNotify:
            case LeaveNotify:
                return GDK_FILTER_CONTINUE;
        }
    }

    //send all the XEvents to qt
    ((QApplication *)QApplication::instance())->x11ProcessEvent(e);
    QApplication::sendPostedEvents();

    // filter some events to fake a modal state
    if (self->doModal)
    {
        switch (e->type) {
            case ButtonPress:
            case ButtonRelease:
            case MotionNotify:
            case KeyPress:
            case KeyRelease:
            case EnterNotify:
            case LeaveNotify:
                return GDK_FILTER_REMOVE;
        }
    }

    // filter some xembed events to avoid warnings
    if ((e->type == ClientMessage)
        && (e->xclient.message_type == _XEMBED))
    {
        switch(e->xclient.data.l[1])
        {
        case XEMBED_REQUEST_FOCUS:
        case XEMBED_FOCUS_NEXT:
        case XEMBED_FOCUS_PREV:
            return GDK_FILTER_CONTINUE;
        }
        return GDK_FILTER_REMOVE;
    }

    Q_UNUSED(event);
    Q_UNUSED(data);

    return GDK_FILTER_CONTINUE;
}

void QGtkEventDispatcher::initXEmbedAtom(Display *d)
{
    if (_XEMBED == 0)
        _XEMBED = XInternAtom(d, "_XEMBED", false);
}

// called before the event loop goes into "blocking select mode"
// returns true if it has pending events or false and the maximum time to wait
// util it's checked again
gboolean QGtkEventDispatcher::qteventprepare(GSource *source, gint *timeout_)
{
    Q_UNUSED(source);

    // might be usefull
    emit self->awake();

    QApplication::sendPostedEvents();

    emit self->aboutToBlock();

    // don't handle interrupt or wakeup (we don't need it ?)
    int timeout = self->waitTime();

    if (timeout == -1)
        return false; //no timers

    if (timeout > 0) {
        *timeout_ = timeout;
        return false; //wait
    }

    return true; //go go go
}

// called right after "blocking select mode"
gboolean QGtkEventDispatcher::qteventcheck(GSource *source)
{
    Q_UNUSED(source);

    // check if some of the timers needs to be activated
    int timeout = self->waitTime();
    if (timeout == 0)
        return true; //go go go

    return false;
}

// called when we have a go for dispatch (prepare or check returned true)
gboolean QGtkEventDispatcher::qteventdispatch(GSource *source, GSourceFunc callback, gpointer user_data)
{
    Q_UNUSED(source);
    Q_UNUSED(callback);
    Q_UNUSED(user_data);

    // we dont need callback functions, we just call the existing activateTimers function
    self->activateTimers();
    return true;
}

void QGtkEventDispatcher::qteventfinalize(GSource *source)
{
    Q_UNUSED(source);
    // not used for now
}
