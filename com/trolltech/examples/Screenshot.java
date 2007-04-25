/****************************************************************************
 **
 **  (C) 1992-$THISYEAR$ $TROLLTECH$. All rights reserved.
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

import com.trolltech.qt.core.*;
import com.trolltech.qt.core.Qt.*;
import com.trolltech.qt.gui.*;
import com.trolltech.qt.gui.QSizePolicy.Policy;

@QtJambiExample(name = "Screenshot")
public class Screenshot extends QWidget {

    public static void main(String args[]) {
        QApplication.initialize(args);
        Screenshot screenshot = new Screenshot(null);
        screenshot.show();
        QApplication.exec();
    }

    QPixmap originalPixmap;

    QLabel screenshotLabel;
    QGroupBox optionsGroupBox;
    QSpinBox delaySpinBox;
    QLabel delaySpinBoxLabel;
    QCheckBox hideThisWindowCheckBox;
    QPushButton newScreenshotButton;
    QPushButton saveScreenshotButton;
    QPushButton quitScreenshotButton;

    QVBoxLayout mainLayout;
    QGridLayout optionsGroupBoxLayout;
    QHBoxLayout buttonsLayout;

    public Screenshot(QWidget parent) {
        super(parent);
        screenshotLabel = new QLabel();
        screenshotLabel.setSizePolicy(Policy.Expanding, Policy.Expanding);
        screenshotLabel.setAlignment(AlignmentFlag.AlignCenter);
        screenshotLabel.setMinimumSize(240, 160);

        createOptionsGroupBox();
        createButtonsLayout();

        mainLayout = new QVBoxLayout();
        mainLayout.addWidget(screenshotLabel);
        mainLayout.addWidget(optionsGroupBox);
        mainLayout.addLayout(buttonsLayout);
        setLayout(mainLayout);

        shootScreen();
        delaySpinBox.setValue(5);

        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
        setWindowTitle(tr("Screenshot"));
        resize(300, 200);
    }

    public void resizeEvent(QResizeEvent event) {
        QSize scaledSize = originalPixmap.size();
        scaledSize.scale(screenshotLabel.size(), 
                         AspectRatioMode.KeepAspectRatio);
        if (screenshotLabel.pixmap() != null 
            || scaledSize != screenshotLabel.pixmap().size())
                updateScreenshotLabel();
    }

    void newScreenshot() {
        if (hideThisWindowCheckBox.isChecked())
            hide();
        newScreenshotButton.setDisabled(true);

        QTimer.singleShot(delaySpinBox.value() * 1000, 
                          this, "shootScreen()");
    }

    void saveScreenshot() {
        String format = "png";
        String initialPath = QDir.currentPath() + tr("/untitled.") + format;
        String filter = String.format(tr("%1$s Files (*.%2$s);;All Files (*)"),
                                      format.toUpperCase(), format);
        String fileName;
        fileName = QFileDialog.getSaveFileName(this, tr("Save As"), initialPath,
                                               new QFileDialog.Filter(filter));

        if (!fileName.equals(""))
            originalPixmap.save(fileName, format);
    }

    void shootScreen() {
        if (delaySpinBox.value() != 0)
            QApplication.beep();

        if(originalPixmap != null) {
            originalPixmap.dispose();
        }   
        
        originalPixmap = QPixmap.grabWindow(
                QApplication.desktop().winId());
        updateScreenshotLabel();

        newScreenshotButton.setDisabled(false);
        if (hideThisWindowCheckBox.isChecked())
            show();
    }

    void updateCheckBox() {
        if (delaySpinBox.value() == 0)
            hideThisWindowCheckBox.setDisabled(true);
        else
            hideThisWindowCheckBox.setDisabled(false);
    }

    void createOptionsGroupBox() {
        optionsGroupBox = new QGroupBox(tr("Options"));

        delaySpinBox = new QSpinBox();
        delaySpinBox.setSuffix(tr(" s"));
        delaySpinBox.setMaximum(60);
        delaySpinBox.valueChanged.connect(this, "updateCheckBox()");

        delaySpinBoxLabel = new QLabel(tr("Screenshot Delay:"));

        hideThisWindowCheckBox = new QCheckBox(tr("Hide This Window"));

        optionsGroupBoxLayout = new QGridLayout();
        optionsGroupBoxLayout.addWidget(delaySpinBoxLabel, 0, 0);
        optionsGroupBoxLayout.addWidget(delaySpinBox, 0, 1);
        optionsGroupBoxLayout.addWidget(hideThisWindowCheckBox, 1, 0, 1, 2);
        optionsGroupBox.setLayout(optionsGroupBoxLayout);
    }

    void createButtonsLayout() {
        newScreenshotButton = createButton(tr("New Screenshot"), this, 
                                           "newScreenshot()");

        saveScreenshotButton = createButton(tr("Save Screenshot"), this, 
                                            "saveScreenshot()");

        quitScreenshotButton = createButton(tr("Quit"), this, "close()");

        buttonsLayout = new QHBoxLayout();
        buttonsLayout.addStretch();
        buttonsLayout.addWidget(newScreenshotButton);
        buttonsLayout.addWidget(saveScreenshotButton);
        buttonsLayout.addWidget(quitScreenshotButton);
    }

    QPushButton createButton(final String text, QWidget receiver, 
                             String member) {
        QPushButton button = new QPushButton(text);
        button.clicked.connect(receiver, member);
        return button;
    }

    void updateScreenshotLabel() {
        screenshotLabel.setPixmap(originalPixmap.scaled(screenshotLabel.size(),
                                  AspectRatioMode.KeepAspectRatio, 
                                  TransformationMode.SmoothTransformation));
    }
}
