/*   Ported from: src.corelib.kernel.qcoreapplication.cpp
<snip>
//! [0]
        QMouseEvent event(QEvent::MouseButtonPress, pos, 0, 0, 0);
        QApplication::sendEvent(mainWindow, &event);
//! [0]


//! [1]
        QPushButton *quitButton = new QPushButton("Quit");
        connect(quitButton, SIGNAL(clicked()), &app, SLOT(quit()));
//! [1]


//! [2]
        foreach (QString path, app.libraryPaths())
            do_something(path);
//! [2]


//! [3]
        bool myEventFilter(void *message, long *result);
//! [3]


//! [4]
        static int *global_ptr = 0;

        static void cleanup_ptr()
        {
            delete [] global_ptr;
            global_ptr = 0;
        }

        void init_ptr()
        {
            global_ptr = new int[100];      // allocate data
            qAddPostRoutine(cleanup_ptr);   // delete later
        }
//! [4]


//! [5]
        class MyPrivateInitStuff : public QObject
        {
        public:
            static MyPrivateInitStuff *initStuff(QObject *parent)
            {
                if (!p)
                    p = new MyPrivateInitStuff(parent);
                return p;
            }

            ~MyPrivateInitStuff()
            {
                // cleanup goes here
            }

        private:
            MyPrivateInitStuff(QObject *parent)
                : QObject(parent)
            {
                // initialization goes here
            }

            MyPrivateInitStuff *p;
        };
//! [5]


//! [6]
        static inline QString tr(const char *sourceText,
                                 const char *comment = 0);
        static inline QString trUtf8(const char *sourceText,
                                     const char *comment = 0);
//! [6]


//! [7]
        class MyMfcView : public CView
        {
            Q_DECLARE_TR_FUNCTIONS(MyMfcView)

        public:
            MyMfcView();
            ...
        };
//! [7]


</snip>
*/
import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.xml.*;
import com.trolltech.qt.network.*;
import com.trolltech.qt.sql.*;
import com.trolltech.qt.svg.*;


public class src_corelib_kernel_qcoreapplication {
    public static void main(String args[]) {
        QApplication.initialize(args);
//! [0]
        QMouseEvent event(QEvent.MouseButtonPress, pos, 0, 0, 0);
        QApplication.sendEvent(mainWindow, vent);
//! [0]


//! [1]
        QPushButton uitButton = new QPushButton("Quit");
        connect(quitButton, SIGNAL(clicked()), pp, SLOT(quit()));
//! [1]


//! [2]
        for (Stringspath, app.libraryPaths())
            do_something(path);
//! [2]


//! [3]
        booleansmyEventFilter(void essage, long esult);
//! [3]


//! [4]
        static int lobal_ptr = 0;

        static void cleanup_ptr()
        {
            delete [] global_ptr;
            global_ptr = 0;
        }

        void init_ptr()
        {
            global_ptr = new int[100];      // allocate data
            qAddPostRoutine(cleanup_ptr);   // delete later
        }
//! [4]


//! [5]
        class MyPrivateInitStuff : public QObject
        {
        public:
            static MyPrivateInitStuff nitStuff(QObject arent)
            {
                if (!p)
                    p = new MyPrivateInitStuff(parent);
                return p;
            }

            ~MyPrivateInitStuff()
            {
                // cleanup goes here
            }

        private:
            MyPrivateInitStuff(QObject arent)
                : QObject(parent)
            {
                // initialization goes here
            }

            MyPrivateInitStuff ;
        };
//! [5]


//! [6]
        static inline Stringstr(char ourceText,
                                 char omment = 0);
        static inline StringstrUtf8(char ourceText,
                                     char omment = 0);
//! [6]


//! [7]
        class MyMfcView : public CView
        {
            Q_DECLARE_TR_FUNCTIONS(MyMfcView)

        public:
            MyMfcView();
            ...
        };
//! [7]


    }
}
