/****************************************************************************
 **
 ** Copyright (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
 **
 ** This file is part of $PRODUCT$.
 **
 ** $JAVA_LICENSE$
 **
 ** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 ** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 **
 ****************************************************************************/

package com.trolltech.launcher;

import java.io.File;
import java.io.PrintStream;
import java.util.*;

import com.trolltech.qt.QSysInfo;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class Launcher extends QWidget {
    private abstract class HtmlUpdater extends Worker {
        protected abstract String html(Launchable l);

        protected abstract void updateHtml(String s);

        protected void execute() {
            QModelIndex i = ui.list.selectionModel().currentIndex();
            Launchable l = null;
            if (i.isValid())
                l = m_model.at(i);
            updateHtml(l == null ? "n/a" : html(l));
        }

        public void start() {
            updateHtml("loading...");
            super.start();
        }
    }

    private HtmlUpdater m_source_updater = new HtmlUpdater() {
            protected String html(Launchable l) {
                return l.source();
            }

            protected void updateHtml(String html) {
                ui.source.setHtml(html);
            }
	};

    private HtmlUpdater m_description_updater = new HtmlUpdater() {
            protected String html(Launchable l) {
                return l.description();
            }

            protected void updateHtml(String html) {
                ui.description.setHtml(html);
            }
	};

    private Ui_Launcher ui = new Ui_Launcher();
    private LaunchableListModel m_model = new LaunchableListModel();
    private Launchable m_current;
    
    private QProcess assistantProcess;
    
    private static QPalette systemPalette;

    public Launcher() {
        ui.setupUi(this);
        ui.list.setModel(m_model);
        ui.list.setItemDelegate(new Delegate(m_model));

        setupExamples();
        setupStyles();

        ui.list.selectionModel().currentChanged.connect(this, "listSelectionChanged(QModelIndex,QModelIndex)");
        ui.button_content.clicked.connect(this, "slotSwapContent()");
        ui.button_launch.clicked.connect(this, "slotLaunch()");
        ui.button_assistant.clicked.connect(this, "slotRunAssistant()");
        updateStyle(this, new Style());

        setWindowTitle("Qt Jambi Examples and Demos");
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));

        QPixmap bg = new QPixmap("classpath:com/trolltech/launcher/fadlogo.png");
        ui.description.setBackground(bg);
        ui.source.setBackground(bg);

        ui.description.setHtml(loadDefaultText());
    }

    public boolean eventFilter(QObject object, QEvent e) {
        if (object == m_current.widget() && e.type() == QEvent.Close) {
            launch_close();
            return false;
        }
        return false;
    }

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
        boolean enable = current.isValid();
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

    public void slotRunAssistant() {
        assistantProcess = new QProcess();

        List<String> arguments = new ArrayList<String>();
        arguments.add("-profile");
        arguments.add("doc/html/qtjambi.adp");

        assistantProcess.finished.connect(this, "assistantFinished()");

        if (QSysInfo.macVersion() > 0)
            assistantProcess.start("bin/assistant.app/Contents/MacOS/ assistant", arguments);
        else
            assistantProcess.start("assistant", arguments);

        ui.button_assistant.setEnabled(false);
    }

    private void assistantFinished() {
        assistantProcess.disposeLater();
        assistantProcess = null;
        ui.button_assistant.setEnabled(true);
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
        if (f.open(QFile.ReadOnly)) {
            String s = f.readAll().toString();
            f.close();
            return s;
        }
        return null;
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

        QModelIndex i = ui.list.selectionModel().currentIndex();
        m_current = m_model.at(i);

        m_current.widget().show();
        m_current.widget().installEventFilter(this);

        // 	ui.description.stop();
    }

    /**
     * Does the required stuff to close a launchable
     */
    private void launch_close() {
        ui.button_launch.setText(tr("Launch"));

        m_current.widget().removeEventFilter(this);
        m_current.killWidget();

        m_current = null;

        // 	ui.description.start();
    }

    /**
     * Some hardcoded logic to figure out which style we should be
     * using by default.
     */
    private static String styleForCurrentSystem() {
        int os = com.trolltech.qt.QSysInfo.operatingSystem();
        if (os == com.trolltech.qt.QSysInfo.OS_WIN32
            || os == com.trolltech.qt.QSysInfo.OS_WIN64) {
            if (com.trolltech.qt.QSysInfo.windowsVersion() >= com.trolltech.qt.QSysInfo.Windows_XP)
                return "WindowsXP";
            else
                return "Windows";
        } else if (com.trolltech.qt.QSysInfo.macVersion() >= 0) {
            return "Aqua";
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
        String checkedByDefault = styleForCurrentSystem();

        for (String styleKey : styleKeys) {
            QRadioButton button = new QRadioButton(styleKey);
            layout.addWidget(button);
            button.clicked.connect(this, "styleChanged()");

            if (styleKey.equals(checkedByDefault))
                button.setChecked(true);
        }

        styleChanged();

    }

    /**
     * Helper function for reading the list of launchable examples... We will
     * ideally pick this up from the classpath under demos and examples...
     */
    private void setupExamples() {
        String dirs[] = new String[] { "classpath:com/trolltech/examples",
                                       "classpath:com/trolltech/demos" };

        List<String> filter = new ArrayList<String>();
        filter.add("*.class");

        for (int i = 0; i < dirs.length; ++i) {
            QDir dir = new QDir(dirs[i]);

            List<QFileInfo> classFiles = dir.entryInfoList(filter);

            String pkg = dirs[i].substring(10).replace("/", ".");
            for (QFileInfo info : classFiles) {

                Launchable l = Launchable.create(pkg + "." + info.baseName());
                if (l != null)
                    m_model.add(l);
            }
        }
    }

    public static void start_qt(boolean debug)
    {

        File f_out = null;
        File f_err = null;

        try {
            if (debug) {
                f_out = new File("START_QT_STDOUT.TXT");
                f_err = new File("START_QT_STDERR.TXT");

                System.setOut(new PrintStream(f_out));
                System.setErr(new PrintStream(f_err));
            }

            String args[] = new String[1];
            args[0] = "Start Qt";

            main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (f_out != null)
            System.out.close();
        if (f_err != null)
            System.err.close();
    }

    public static void main(String args[]) {
        QApplication.initialize(args);
   
        systemPalette = QApplication.palette();
        
        Launcher l = new Launcher();
        l.show();
        QApplication.exec();
    }
}
