/****************************************************************************
 **
 ** Copyright (C) 1992-2009 Nokia. All rights reserved.
 **
 ** This file is part of Qt Jambi.
 **
 ** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
** 
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

 **
 ** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 ** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 **
 ****************************************************************************/

package com.trolltech.launcher;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.*;

import java.util.*;

public class Launcher extends QWidget {

    private abstract class HtmlUpdater extends Worker {

        public HtmlUpdater(QObject parent) {
            super(parent);
        }

        protected abstract String html(Launchable l);

        protected abstract void updateHtml(String s);

        @Override
        protected void execute() {
            QModelIndex i = sortFilterProxyModel.mapToSource(ui.list.selectionModel().currentIndex());
            Launchable l = null;
            if (i != null)
                l = m_model.at(i);
            updateHtml(l == null ? "n/a" : html(l));
        }

        @Override
        public void start() {
            updateHtml("loading...");
            super.start();
        }
    }

    private HtmlUpdater m_source_updater = new HtmlUpdater(this) {
            @Override
            protected String html(Launchable l) {
                return l.source();
            }

            @Override
            protected void updateHtml(String html) {
                ui.source.setHtml(html);
            }
    };

    private HtmlUpdater m_description_updater = new HtmlUpdater(this) {
            @Override
            protected String html(Launchable l) {
                return l.description();
            }

            @Override
            protected void updateHtml(String html) {
                ui.description.setHtml(html);
            }
    };

    private Ui_Launcher ui = new Ui_Launcher();
    private LaunchableListModel m_model = new LaunchableListModel();
    private Launchable m_current;
    private boolean firstStyleSetup = true;
    public Signal1<String> progressChanged = new Signal1<String>();

    private static QPalette systemPalette;

    public Launcher() {
    }

    private QSortFilterProxyModel sortFilterProxyModel = new QSortFilterProxyModel();
    public void init() {
        ui.setupUi(this);

        ui.source.setWordWrap(false);


        progressChanged.emit("Setting up examples");

        setupExamples();

        sortFilterProxyModel.setSourceModel(m_model);
        sortFilterProxyModel.invalidate();
        sortFilterProxyModel.sort(0);

        ui.list.setModel(sortFilterProxyModel);
        ui.list.setItemDelegate(new Delegate(sortFilterProxyModel));
        ui.list.setCurrentIndex(null);

        progressChanged.emit("Setting up styles");
        setupStyles();

        progressChanged.emit("Making connections");

        ui.list.selectionModel().currentChanged.connect(this, "listSelectionChanged(QModelIndex,QModelIndex)");
        ui.button_content.clicked.connect(this, "slotSwapContent()");
        ui.button_launch.clicked.connect(this, "slotLaunch()");
        ui.button_documentation.clicked.connect(this, "openDocumentation()");
        updateStyle(this, new Style(this));

        setWindowTitle("Qt Jambi Examples and Demos");
        if (Utilities.operatingSystem != Utilities.OperatingSystem.MacOSX)
            setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));

        progressChanged.emit("Loading background");

        QPixmap bg = new QPixmap("classpath:com/trolltech/launcher/fadlogo.png");
        ui.description.setBackground(bg);
        ui.source.setBackground(bg);

        ui.description.setHtml(loadDefaultText());

        if (System.getProperty("com.trolltech.launcher.webstart") != null)
            ui.button_documentation.hide();
    }

    @Override
    public boolean eventFilter(QObject object, QEvent e) {
        if (object == m_current.widget() && e.type() == QEvent.Type.Close) {
            launch_close();
            return false;
        }
        return false;
    }

    @Override
    public void closeEvent(QCloseEvent e) {
        QApplication.quit();
    }

    /**
     * Enables actions and sets up the user interface so that its
     * possible to run the app, view source and description...
     */
    public void listSelectionChanged(QModelIndex current, QModelIndex previous) {
        // If a new widget it selected, close the m_current one...
        if (m_current != null)
            launch_close();

        // Enable components...
        boolean enable = current != null;
        ui.container.setEnabled(enable);
        ui.button_launch.setEnabled(enable);
        ui.button_content.setEnabled(enable);

        m_source_updater.start();
        m_description_updater.start();
    }

    public void styleChanged() {
        List<QObject> children = ui.group_styles.children();
        for (QObject c : children) {
            if (c instanceof QRadioButton) {
                QRadioButton button = (QRadioButton) c;
                if (button.isChecked()) {
                    QStyle style = QStyleFactory.create(button.text());
                    QApplication.setStyle(style);
                    if (button.text().equals(styleForCurrentSystem()))
                        QApplication.setPalette(systemPalette);
                    else
                        QApplication.setPalette(style.standardPalette());
                }
            }
        }
    }

    public void openDocumentation() {
        QUrl url = new QUrl();
        url.setScheme("file");
        url.setPath(new QFileInfo("doc/html/com/trolltech/qt/qtjambi-index.html").absoluteFilePath());
        QDesktopServices.openUrl(url);
    }

    /**
     * Swaps the current content view.
     */
    public void slotSwapContent() {
        int i = ui.container.currentIndex();
        i = (i + 1) % 2;
        ui.container.setCurrentIndex(i);

        ui.button_content.setText(i == 0 ? tr("View Source")
                                  : tr("View Description"));
    }

    /**
     * Triggered by the launch button. Will either start or stop the
     * app currently selected in the list view.
     */
    public void slotLaunch() {
        if (m_current == null)
            launch_show();
        else
            launch_close();
    }


    private String loadDefaultText() {
        QFile f = new QFile("classpath:com/trolltech/launcher/launcher.html");
        assert f.exists();
        String content = null;
        if (f.open(new QFile.OpenMode(QFile.OpenModeFlag.ReadOnly))) {
            content = f.readAll().toString();
            f.close();
        }
        f.dispose();
        return content;
    }


    /**
     * Recursive helper function to update the style in a widget hierarchy
     */
    private void updateStyle(QWidget widget, QStyle style) {
        widget.setStyle(style);
        List<QObject> children = widget.children();
        for (QObject o : children)
            if (o instanceof QWidget)
                updateStyle((QWidget) o, style);
    }

    /**
     * Does the required stuff to show a launchable
     */
    private void launch_show() {
        ui.button_launch.setText(tr("Close"));

        QModelIndex i = sortFilterProxyModel.mapToSource(ui.list.selectionModel().currentIndex());
        m_current = m_model.at(i);

        m_current.widget().show();
        m_current.widget().installEventFilter(this);

        //  ui.description.stop();
    }

    /**
     * Does the required stuff to close a launchable
     */
    private void launch_close() {
        ui.button_launch.setText(tr("Launch"));

        m_current.widget().removeEventFilter(this);
        m_current.killWidget();

        m_current = null;

        //  ui.description.start();
    }

    /**
     * Some hardcoded logic to figure out which style we should be
     * using by default.
     *
     * @return A string representing the default style...
     */
    private static String styleForCurrentSystem() {
        int os = com.trolltech.qt.QSysInfo.operatingSystem();
        if (os == com.trolltech.qt.QSysInfo.OS_WIN32
            || os == com.trolltech.qt.QSysInfo.OS_WIN64) {
            if (com.trolltech.qt.QSysInfo.windowsVersion() >= com.trolltech.qt.QSysInfo.Windows_VISTA)
                return "WindowsVista";
            if (com.trolltech.qt.QSysInfo.windowsVersion() >= com.trolltech.qt.QSysInfo.Windows_XP)
                return "WindowsXP";
            else
                return "Windows";
        } else if (com.trolltech.qt.QSysInfo.macVersion() > 0) {
            return "Macintosh (Aqua)";
        } else {
            return "Plastique";
        }
    }

    /**
     * Helper function to figure out which styles are installed and
     * put them in the styles group box
     */
    private void setupStyles() {
        List<String> styleKeys = QStyleFactory.keys();

        QLayout layout = ui.group_styles.layout();

        String checkedByDefault = QApplication.style().objectName();
        for (String styleKey : styleKeys) {
            QRadioButton button = new QRadioButton(styleKey);
            layout.addWidget(button);
            if (styleKey.equalsIgnoreCase(checkedByDefault))
                button.setChecked(true);
            button.clicked.connect(this, "styleChanged()");
        }


        if (!firstStyleSetup)
            styleChanged();
        firstStyleSetup = false;
    }

    private void traverseDirectory(String directory) {
        QDir dir = new QDir(directory);
        List<QFileInfo> infos = dir.entryInfoList(QDir.Filter.createQFlags(QDir.Filter.Files, QDir.Filter.Dirs, QDir.Filter.NoDotAndDotDot));

        for (QFileInfo info : infos) {
            if (info.isDir()) {
                traverseDirectory(info.absoluteFilePath());
            } else if (info.fileName().endsWith(".class")) {
                int idx = directory.lastIndexOf("#") + 1;
                if (idx <= 0)
                    idx = 10;
                String pkg = directory.substring(idx).replace("/", ".");

                Launchable l = Launchable.create(pkg + "." + info.baseName());
                if (l != null) {
                    progressChanged.emit("Setting up examples: " + info.baseName());
                    m_model.add(l);
                }

            }
        }
    }

    /**
     * Helper function for reading the list of launchable examples... We will
     * ideally pick this up from the classpath under demos and examples...
     */
    private void setupExamples() {
        String dirs[] = new String[] { "classpath:com/trolltech/examples",
                                       "classpath:com/trolltech/demos" };

        for (int i = 0; i < dirs.length; ++i) {
            traverseDirectory(dirs[i]);
        }
    }

    private static String[] start_qt()
    {
        String args[] = new String[1];
        args[0] = "Start Qt";
        return args;
    }

    public static void main(String args[]) {
        QApplication.initialize(args == null ? start_qt() : args);
        QApplication.setApplicationName("Qt Jambi Demo Launcher");


        SplashScreen splashScreen = null;
        splashScreen = new SplashScreen();
        splashScreen.show();
        splashScreen.setGeometry(splashScreen.splashScreenRect());

        // Show in front on mac regardless of if its a bundle or not...
        splashScreen.raise();

        QApplication.processEvents();

        systemPalette = QApplication.palette();

        Launcher l = new Launcher();

        l.progressChanged.connect(splashScreen, "updateProgress(String)");

        l.init();
        l.show();

        if (splashScreen != null)
            splashScreen.finish(l);

        QApplication.exec();

        System.gc();
    }
}
