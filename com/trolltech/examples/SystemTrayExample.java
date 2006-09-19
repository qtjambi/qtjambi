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

public class SystemTrayExample extends QWidget {

	private QLineEdit titleEdit;
	private QTextEdit msgEdit;
	private QComboBox typeCombo;
	private QSystemTrayIcon trayIcon;
	private QAction toggleVisibilityAction;
	private QMenu menu;
	private QTextEdit info;
	private QComboBox iconPicker;

	public static void main(String[] args) {
		QApplication.initialize(args);

		SystemTrayExample systemTray = new SystemTrayExample();
		systemTray.show();

		QApplication.exec();
        systemTray.dispose();
	}

	public SystemTrayExample() {
		if (!QSystemTrayIcon.isSystemTrayAvailable())
			QMessageBox.warning(this, tr("System tray is unavailable"), tr("System tray unavailable"));

		// Create the menu that will be used for the context menu
		menu = new QMenu(this);
		menu.aboutToShow.connect(this, "updateMenu()");

		toggleVisibilityAction = new QAction("Show/Hide", this);
		toggleVisibilityAction.triggered.connect(this, "toggleVisibility()");
		menu.addAction(toggleVisibilityAction);

		QAction restoreAction = new QAction("Restore", this);
		restoreAction.triggered.connect(this, "showNormal()");
		menu.addAction(restoreAction);

		QAction minimizeAction = new QAction("Minimize", this);
		minimizeAction.triggered.connect(this, "showMinimized()");
		menu.addAction(minimizeAction);

		QAction maximizeAction = new QAction("Maximize", this);
		maximizeAction.triggered.connect(this, "showMaximized()");
		menu.addAction(maximizeAction);

		menu.addSeparator();

		QAction quitAction = new QAction("&Quit", this);
		quitAction.triggered.connect(this, "quit()");
		menu.addAction(quitAction);

		// Create the tray icon
		trayIcon = new QSystemTrayIcon(this);
		trayIcon.setToolTip("System trayIcon example");
		trayIcon.setContextMenu(menu);

		trayIcon.activated.connect(this, "activated(int)");
		trayIcon.messageClicked.connect(this, "balloonClicked()");

		changeIcon(0); // set the first icon
		trayIcon.show();

		QLabel titleLabel = new QLabel(tr("Message Title"));
		titleEdit = new QLineEdit(tr("Message Title"));
		QLabel msgLabel = new QLabel(tr("Message Contents"));
		msgEdit = new QTextEdit(tr("Man is more ape than many of the apes"));
		msgEdit.setAcceptRichText(false);
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
		info = new QTextEdit(tr("Status messages will be visible here"));
		info.setMaximumHeight(100);

		QCheckBox toggleIconCheckBox = new QCheckBox(tr("Show system tray icon"));
		toggleIconCheckBox.setChecked(true);
		toggleIconCheckBox.clicked.connect(trayIcon, "setVisible(boolean)");

		QLabel iconLabel = new QLabel("Select icon");
		iconPicker = new QComboBox();
		Vector<String> icons = new Vector<String>();
		icons.add("16x16 icon");
		icons.add("22x22 icon");
		icons.add("32x32 icon");
		iconPicker.addItems(icons);
		iconPicker.activatedIndex.connect(this, "changeIcon(int)");

		QGridLayout layout = new QGridLayout();
		layout.addWidget(titleLabel, 0, 0);
		layout.addWidget(titleEdit, 0, 1);
		layout.addWidget(msgLabel, 1, 0);
		layout.addWidget(msgEdit, 1, 1);
		layout.addWidget(typeLabel, 2, 0);
		layout.addWidget(typeCombo, 2, 1);
		layout.addWidget(balloonButton, 4, 1);
		layout.addWidget(info, 5, 0, 1, 2);
		layout.addWidget(toggleIconCheckBox, 6, 0);
		layout.addWidget(iconLabel, 7, 0);
		layout.addWidget(iconPicker, 7, 1);
		setLayout(layout);
        
        setWindowTitle(tr("System Tray Example"));
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
	}

	public void closeEvent(QCloseEvent e) {
		if (trayIcon.isVisible()) {
			QMessageBox.information(this, tr("System tray example"), tr("Application will continue running. Quit using context menu in the system tray"));
			hide();
			e.ignore();
		}
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
			QMessageBox.information(this, tr("System tray example"), tr("Balloon tips are not supported on Mac OS X"));
		} else {
			QSystemTrayIcon.MessageIcon icon = QSystemTrayIcon.MessageIcon.resolve(typeCombo.currentIndex());
			trayIcon.showMessage(titleEdit.text(), msgEdit.toPlainText(), icon, 10000);
			trayIcon.setToolTip(titleEdit.text());
		}
	}

	protected void balloonClicked() {
		info.append(tr("Balloon message was clicked"));
	}

	protected void activated(int reason) {
		String r = QSystemTrayIcon.MessageIcon.resolve(reason).name();
		if (r != null)
			info.append("Activated - Reason " + r);
	}

	protected void changeIcon(int index) {
		String iconname;
		switch (index) {
		default: // case 0 is default
		case 0:
			iconname = "classpath:com/trolltech/examples/images/icon_16x16.png";
			break;
		case 1:
			iconname = "classpath:com/trolltech/examples/images/icon_22x22.png";
			break;
		case 2:
			iconname = "classpath:com/trolltech/examples/images/icon_32x32.png";
			break;
		}
		QPixmap pix = new QPixmap(iconname);
		trayIcon.setIcon(new QIcon(pix));
	}

	public void quit() {
		trayIcon.dispose();
		QApplication.quit();
	}
    
    // REMOVE-START
    
    public static String exampleName() {
        return "System Tray Example";
    }

    public static boolean canInstantiate() {
        return true;
    }

    // REMOVE-END
}