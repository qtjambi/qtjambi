#include <QtGui>

QWidget *win;
QWidget *panel;

void snippet_ctor1()
{
//! [0]
    QSettings settings("MySoft", "Star Runner");
//! [0]
}

void snippet_ctor2()
{
//! [1]
    QCoreApplication::setOrganizationName("MySoft");
//! [1] //! [2]
    QCoreApplication::setOrganizationDomain("mysoft.com");
//! [2] //! [3]
    QCoreApplication::setApplicationName("Star Runner");
//! [3]

//! [4]
    QSettings settings;
//! [4]

//! [5]
    settings.setValue("editor/wrapMargin", 68);
//! [5] //! [6]
    int margin = settings.value("editor/wrapMargin").toInt();
//! [6]
    {
//! [7]
    int margin = settings.value("editor/wrapMargin", 80).toInt();
//! [7]
    }

//! [8]
    settings.setValue("mainwindow/size", win->size());
//! [8] //! [9]
    settings.setValue("mainwindow/fullScreen", win->isFullScreen());
//! [9] //! [10]
    settings.setValue("outputpanel/visible", panel->isVisible());
//! [10]

//! [11]
    settings.beginGroup("mainwindow");
    settings.setValue("size", win->size());
    settings.setValue("fullScreen", win->isFullScreen());
    settings.endGroup();
//! [11]

//! [12]
    settings.beginGroup("outputpanel");
    settings.setValue("visible", panel->isVisible());
    settings.endGroup();
//! [12]
}

void snippet_locations()
{
//! [13]
    QSettings obj1("MySoft", "Star Runner");
//! [13] //! [14]
    QSettings obj2("MySoft");
    QSettings obj3(QSettings::SystemScope, "MySoft", "Star Runner");
    QSettings obj4(QSettings::SystemScope, "MySoft");
//! [14]

    {
//! [15]
    QSettings settings(QSettings::IniFormat, QSettings::UserScope,
                       "MySoft", "Star Runner");
//! [15]
    }

    {
    QSettings settings("starrunner.ini", QSettings::IniFormat);
    }

    {
    QSettings settings("HKEY_CURRENT_USER\\Software\\Microsoft",
                       QSettings::NativeFormat);
    }
}

class MainWindow : public QMainWindow
{
public:
    MainWindow();

    void writeSettings();
    void readSettings();

protected:
    void closeEvent(QCloseEvent *event);
};

//! [16]
void MainWindow::writeSettings()
{
    QSettings settings("Moose Soft", "Clipper");

    settings.beginGroup("MainWindow");
    settings.setValue("size", size());
    settings.setValue("pos", pos());
    settings.endGroup();
}
//! [16]

//! [17]
void MainWindow::readSettings()
{
    QSettings settings("Moose Soft", "Clipper");

    settings.beginGroup("MainWindow");
    resize(settings.value("size", QSize(400, 400)).toSize());
    move(settings.value("pos", QPoint(200, 200)).toPoint());
    settings.endGroup();
}
//! [17]

//! [18]
MainWindow::MainWindow()
{
//! [18] //! [19]
    readSettings();
//! [19] //! [20]
}
//! [20]

bool userReallyWantsToQuit() { return true; }

//! [21]
void MainWindow::closeEvent(QCloseEvent *event)
{
    if (userReallyWantsToQuit()) {
        writeSettings();
        event->accept();
    } else {
        event->ignore();
    }
}
//! [21]
