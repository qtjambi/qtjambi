#ifndef QGTKEVENTDISPATCHER_H
#define QGTKEVENTDISPATCHER_H

#include <private/qeventdispatcher_unix_p.h>

#include <gdk/gdkx.h>
#include <QHash>

typedef struct _GtkQtTimer
{
    QObject *object;
    guint gtkId;
} GtkQtTimer;

class QGtkEventDispatcher : public QEventDispatcherUNIX
{
    Q_OBJECT

public:
    explicit QGtkEventDispatcher(QObject *parent = 0);
    ~QGtkEventDispatcher();
    
    bool processEvents(QEventLoop::ProcessEventsFlags flags);
    
    void registerSocketNotifier(QSocketNotifier *notifier);
    void unregisterSocketNotifier(QSocketNotifier *notifier);
        
    void wakeUp();
    
    static bool hookQtIntoGtk(int gtksoc = 0);
    
private:
    bool doModal;

    static Atom _XEMBED;
    static GSourceFuncs *qtEventFunctions;
    static GSource *qtSource;
    static QGtkEventDispatcher *self;
    
    enum XEmbedMessageType {
        XEMBED_REQUEST_FOCUS = 3,
        XEMBED_FOCUS_NEXT = 6,
        XEMBED_FOCUS_PREV = 7
    };
    
    static void initXEmbedAtom(Display *d);
    static GdkFilterReturn gdkXEventProc(GdkXEvent *xevent, GdkEvent *event, gpointer data);

    // qt event functions
    static gboolean qteventprepare(GSource *source, gint *timeout_);
    static gboolean qteventcheck(GSource *source);
    static gboolean qteventdispatch(GSource *source, GSourceFunc callback, gpointer user_data);
    static void qteventfinalize(GSource *source);
    
    int waitTime();
    void setPalette(int gtksoc);
};

#endif //QGTKEVENTDISPATCHER_H
