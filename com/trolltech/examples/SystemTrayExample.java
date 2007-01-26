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

import java.util.Vector;

import com.trolltech.qt.QSysInfo;
import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QCheckBox;
import com.trolltech.qt.gui.QCloseEvent;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QGridLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QMenu;
import com.trolltech.qt.gui.QMessageBox;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QPushButton;
import com.trolltech.qt.gui.QSystemTrayIcon;
import com.trolltech.qt.gui.QTextEdit;
import com.trolltech.qt.gui.QWidget;

@QtJambiExample(name = "System Tray Example")
public class SystemTrayExample extends QWidget {

    private QSystemTrayIcon trayIcon;
    private QMenu trayIconMenu;

    private QLineEdit titleEdit;
    private QTextEdit messageEdit;
    private QComboBox typeCombo;

    private QTextEdit infoDisplay;
    private QComboBox iconCombo;

    private QAction toggleVisibilityAction;

    public static void main(String[] args) {
        QApplication.initialize(args);

        SystemTrayExample editor = new SystemTrayExample();
        editor.show();

        QApplication.exec();
    }

    public SystemTrayExample() {
        this(null);
    }
    
    public SystemTrayExample(QWidget parent) {
        super(parent);
        if (!QSystemTrayIcon.isSystemTrayAvailable())
            QMessageBox.warning(this, tr("System tray is unavailable"),
                                      tr("System tray unavailable"));

        // Create the menu that will be used for the context menu
        trayIconMenu = new QMenu(this);
        trayIconMenu.aboutToShow.connect(this, "updateMenu()");

        toggleVisibilityAction = new QAction("Show/Hide", this);
        toggleVisibilityAction.triggered.connect(this, "toggleVisibility()");
        trayIconMenu.addAction(toggleVisibilityAction);

        QAction restoreAction = new QAction("Restore", this);
        restoreAction.triggered.connect(this, "showNormal()");
        trayIconMenu.addAction(restoreAction);

        QAction minimizeAction = new QAction("Minimize", this);
        minimizeAction.triggered.connect(this, "showMinimized()");
        trayIconMenu.addAction(minimizeAction);

        QAction maximizeAction = new QAction("Maximize", this);
        maximizeAction.triggered.connect(this, "showMaximized()");
        trayIconMenu.addAction(maximizeAction);

        trayIconMenu.addSeparator();

        QAction quitAction = new QAction("&Quit", this);
        quitAction.triggered.connect(this, "close()");
        trayIconMenu.addAction(quitAction);

        // Create the tray icon
        trayIcon = new QSystemTrayIcon(this);
        trayIcon.setToolTip("System trayIcon example");
        trayIcon.setContextMenu(trayIconMenu);

        trayIcon.activated.connect(this, "activated(com.trolltech.qt.gui.QSystemTrayIcon$ActivationReason)");
        trayIcon.messageClicked.connect(this, "balloonClicked()");

        changeIcon(0);
        trayIcon.show();

        QLabel titleLabel = new QLabel(tr("Message Title"));
        titleEdit = new QLineEdit(tr("Message Title"));

        QLabel messageLabel = new QLabel(tr("Message Contents"));
        messageEdit = new QTextEdit(tr("Man is more ape than many of the apes"));
        messageEdit.setAcceptRichText(false);

        QLabel typeLabel = new QLabel(tr("Message Type"));
        typeCombo = new QComboBox();
        Vector<String> types = new Vector<String>();
        types.add("NoIcon");
        types.add("Information");
        types.add("Warning");
        types.add("Critical");
        typeCombo.addItems(types);
        typeCombo.setCurrentIndex(2);

        QPushButton balloonButton = new QPushButton(tr("Balloon message"));
        balloonButton.setToolTip(tr("Click here to balloon the message"));
        balloonButton.clicked.connect(this, "showMessage()");

        infoDisplay = new QTextEdit(tr("Status messages will be visible here"));
        infoDisplay.setMaximumHeight(100);

        QCheckBox toggleIconCheckBox = new QCheckBox(tr("Show system tray icon"));
        toggleIconCheckBox.setChecked(true);
        toggleIconCheckBox.clicked.connect(trayIcon, "setVisible(boolean)");

        QLabel iconLabel = new QLabel("Select icon");
        iconCombo = new QComboBox();
        Vector<String> icons = new Vector<String>();
        icons.add("16x16 icon");
        icons.add("22x22 icon");
        icons.add("32x32 icon");
        iconCombo.addItems(icons);
        iconCombo.activatedIndex.connect(this, "changeIcon(int)");

        QGridLayout layout = new QGridLayout();
        layout.addWidget(titleLabel, 0, 0);
        layout.addWidget(titleEdit, 0, 1);
        layout.addWidget(messageLabel, 1, 0);
        layout.addWidget(messageEdit, 1, 1);
        layout.addWidget(typeLabel, 2, 0);
        layout.addWidget(typeCombo, 2, 1);
        layout.addWidget(balloonButton, 4, 1);
        layout.addWidget(infoDisplay, 5, 0, 1, 2);
        layout.addWidget(toggleIconCheckBox, 6, 0);
        layout.addWidget(iconLabel, 7, 0);
        layout.addWidget(iconCombo, 7, 1);
        setLayout(layout);

        setWindowTitle(tr("System Tray Example"));
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
    }

    public void closeEvent(QCloseEvent e) {
        trayIcon.dispose();
    }

    protected void updateMenu() {
        toggleVisibilityAction.setText(isVisible() ? tr("Hide") : tr("Show"));
    }

    protected void toggleVisibility() {
        if (isVisible())
            hide();
        else
            show();
    }

    protected void showMessage() {
        // #ifdef Q_WS_MAC
        if (QSysInfo.macVersion() != 0) {
            QMessageBox.information(this, tr("System tray example"),
                    tr("Balloon tips are not supported on Mac OS X"));
        } else {
            QSystemTrayIcon.MessageIcon icon;
            icon = QSystemTrayIcon.MessageIcon.resolve(typeCombo.currentIndex());
            trayIcon.showMessage(titleEdit.text(), messageEdit.toPlainText(),
                                 icon, 10000);
            trayIcon.setToolTip(titleEdit.text());
        }
    }

    protected void balloonClicked() {
        infoDisplay.append(tr("Balloon message was clicked"));
    }

    public void activated(QSystemTrayIcon.ActivationReason reason) {
        String name = QSystemTrayIcon.MessageIcon.resolve(reason.value()).name();
        if (name != null)
            infoDisplay.append("Activated - Reason " + name);
    }

    protected void changeIcon(int index) {
        String iconName;
        switch (index) {
        default:
        case 0:
            iconName = "classpath:com/trolltech/examples/images/icon_16x16.png";
            break;

        case 1:
            iconName = "classpath:com/trolltech/examples/images/icon_22x22.png";
            break;

        case 2:
            iconName = "classpath:com/trolltech/examples/images/icon_32x32.png";
            break;
        }
        QPixmap pixmap = new QPixmap(iconName);
        trayIcon.setIcon(new QIcon(pixmap));
    }

}
