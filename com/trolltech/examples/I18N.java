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

package com.trolltech.examples;

import java.util.*;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

@QtJambiExample(name = "I18N",
                canInstantiate = "call-static-method:notWebstart")
public class I18N extends QDialog {

    static public void main(String args[]) {
        QApplication.initialize(args);
        I18N i18n = new I18N();
        i18n.show();
        QApplication.exec();
    }

    private QGroupBox groupBox;
    private QDialogButtonBox buttonBox;
    private QAbstractButton showAllButton;
    private QAbstractButton hideAllButton;
    private Map<QCheckBox, String> qmFileForCheckBoxMap = new HashMap<QCheckBox, String>();
    private Map<QCheckBox, MainWindow> mainWindowForCheckBoxMap = new HashMap<QCheckBox, MainWindow>();

    public I18N() {
        this(null);
    }

    public I18N(QWidget parent) {
        super(parent, new Qt.WindowFlags(Qt.WindowType.WindowStaysOnTopHint));
        groupBox = new QGroupBox("Languages");

        QGridLayout groupBoxLayout = new QGridLayout();

        List<String> qmFiles = findQmFiles();
        for (int i = 0; i < qmFiles.size(); ++i) {
            QCheckBox checkBox = new QCheckBox(languageName(qmFiles.get(i)));
            qmFileForCheckBoxMap.put(checkBox, qmFiles.get(i));
            checkBox.toggled.connect(this, "checkBoxToggled()");
            groupBoxLayout.addWidget(checkBox, i / 2, i % 2);
        }
        groupBox.setLayout(groupBoxLayout);

        buttonBox = new QDialogButtonBox();

        showAllButton = buttonBox.addButton("Show All", QDialogButtonBox.ButtonRole.ActionRole);
        hideAllButton = buttonBox.addButton("Hide All", QDialogButtonBox.ButtonRole.ActionRole);

        showAllButton.clicked.connect(this, "showAll()");
        hideAllButton.clicked.connect(this, "hideAll()");

        QVBoxLayout mainLayout = new QVBoxLayout();
        mainLayout.addWidget(groupBox);
        mainLayout.addWidget(buttonBox);
        setLayout(mainLayout);
        setWindowTitle("I18N");
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
    }

    public void closeEvent(QCloseEvent event) {
        hideAll();
    }

    protected void checkBoxToggled() {
        QCheckBox checkBox = (QCheckBox) signalSender();

        MainWindow window = mainWindowForCheckBoxMap.get(checkBox);
        if (window == null) {
            QTranslator translator = new QTranslator(this);
            translator.load("classpath:com/trolltech/examples/translation/" + qmFileForCheckBoxMap.get(checkBox));
            QApplication.installTranslator(translator);

            window = new MainWindow(this);
            window.visible.connect(checkBox, "setChecked(boolean)");

//             window.setPalette(new QPalette(colorForLanguage(checkBox.text())));

            window.installEventFilter(this);
            mainWindowForCheckBoxMap.put(checkBox, window);

            QApplication.removeTranslator(translator);
        }
        window.setVisible(checkBox.isChecked());
    }

    protected void showAll() {
        for (QCheckBox checkBox : qmFileForCheckBoxMap.keySet())
            checkBox.setChecked(true);
    }

    protected void hideAll() {
        for (QCheckBox checkBox : qmFileForCheckBoxMap.keySet())
            checkBox.setChecked(false);
    }

    private List<String> findQmFiles() {
        QDir dir = new QDir("classpath:com/trolltech/examples/translation");
        List<String> filter = new Vector<String>();
        filter.add("*.qm");
        List<String> fileNames = dir.entryList(filter, new QDir.Filters(QDir.Filter.Files), QDir.SortFlag.Name);

        return fileNames;
    }

    private String languageName(final String qmFile) {
        QTranslator translator = new QTranslator(this);
        translator.load("classpath:com/trolltech/examples/translation/" + qmFile);
        return translator.translate("com.trolltech.examples.I18N", "English");
    }

    private QColor colorForLanguage(final String language) {
        int hashValue = language.hashCode();
        int red = 156 + (hashValue & 0x3F);
        int green = 156 + ((hashValue >> 6) & 0x3F);
        int blue = 156 + ((hashValue >> 12) & 0x3F);
        return new QColor(red, green, blue);
    }

    public static boolean notWebstart() {
        return System.getProperty("com.trolltech.launcher.webstart") == null;
    }
}

class MainWindow extends QMainWindow {
    private QWidget centralWidget;
    private QGroupBox groupBox;
    private QListWidget listWidget;
    private QRadioButton perspectiveRadioButton;
    private QRadioButton isometricRadioButton;
    private QRadioButton obliqueRadioButton;
    private QMenu fileMenu;
    private QAction exitAction;

    public Signal1<Boolean> visible = new Signal1<Boolean>();

    public MainWindow(QWidget parent) {
        super(parent);
        centralWidget = new QWidget();
        setCentralWidget(centralWidget);

        createGroupBox();

        listWidget = new QListWidget();
        listWidget.addItem(tr("First"));
        listWidget.addItem(tr("Second"));
        listWidget.addItem(tr("Third"));

        QVBoxLayout mainLayout = new QVBoxLayout();
        mainLayout.addWidget(groupBox);
        mainLayout.addWidget(listWidget);
        centralWidget.setLayout(mainLayout);

        exitAction = new QAction(tr("E&xit"), this);
        exitAction.triggered.connect(this, "close()");

        fileMenu = menuBar().addMenu(tr("&File"));
        fileMenu.addAction(exitAction);

        setWindowTitle(String.format(tr("Language: %1$s"), tr("English")));
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));

        statusBar().showMessage(tr("Internationalization Example"));

        if (tr("LTR").equals("RTL"))
            setLayoutDirection(Qt.LayoutDirection.RightToLeft);
    }

    private void createGroupBox() {
        groupBox = new QGroupBox(tr("View"));
        perspectiveRadioButton = new QRadioButton(tr("Perspective"));
        isometricRadioButton = new QRadioButton(tr("Isometric"));
        obliqueRadioButton = new QRadioButton(tr("Oblique"));
        perspectiveRadioButton.setChecked(true);

        QVBoxLayout groupBoxLayout = new QVBoxLayout();
        groupBoxLayout.addWidget(perspectiveRadioButton);
        groupBoxLayout.addWidget(isometricRadioButton);
        groupBoxLayout.addWidget(obliqueRadioButton);
        groupBox.setLayout(groupBoxLayout);
    }

    @Override
    protected void closeEvent(QCloseEvent event) {
        visible.emit(false);
    }
}
