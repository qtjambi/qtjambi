package com.trolltech.tests;

import com.trolltech.autotests.generated.SignalsAndSlots;
import com.trolltech.examples.stylesheet.Ui_StyleSheetEditor;
import com.trolltech.qt.core.*;
import com.trolltech.qt.core.QIODevice.OpenModeFlag;
import com.trolltech.qt.gui.*;

public class CRASH extends QWidget {
    
    private Ui_StyleSheetEditor ui = new Ui_StyleSheetEditor();
    private QWidget main;
    private SignalsAndSlots sas;

    CRASH(QWidget parent) {
        super(parent);
        main = parent;
        ui.setupUi(this);

        QRegExp regExp = new QRegExp("Q(.*)Style");
        String defaultStyle = QApplication.style().getClass().getSimpleName();
        if (regExp.exactMatch(defaultStyle))
            defaultStyle = regExp.cap(1);

        ui.styleCombo.addItems(QStyleFactory.keys());
        ui.styleCombo.setCurrentIndex(ui.styleCombo.findText(defaultStyle));
        ui.styleSheetCombo.setCurrentIndex(ui.styleSheetCombo.findText("Coffee"));
        loadStyleSheets();
        loadStyleSheet("Coffee");
        
        sas = new SignalsAndSlots();
        sas.signal1.connect(this, "crash()");            
    }
    
    void crash() {
        int count = ui.styleSheetCombo.count();
        int currentIndex = ui.styleSheetCombo.currentIndex() + 1;
        if (currentIndex == count) currentIndex = 0;
        ui.styleSheetCombo.setCurrentIndex(currentIndex);
        
        loadStyleSheet(ui.styleSheetCombo.currentText());
    }

    void on_styleCombo_activated(final String styleName) {
        QApplication.setStyle(styleName);
        ui.applyButton.setEnabled(false);
    }

    void on_styleSheetCombo_activated(final String sheetName) {
        loadStyleSheet(sheetName);
    }

    /*void on_styleTextEdit_textChanged() {
        // try { Thread.sleep(1000); } catch (Exception e) {}
        ui.applyButton.setEnabled(true);
    }*/

    void on_applyButton_clicked() {
        main.setStyleSheet(ui.styleTextEdit.toPlainText());
        ui.applyButton.setEnabled(false);
    }
    
    protected void paintEvent(QPaintEvent e) {
        sas.signal1.emit();
        
        update();
        
        super.paintEvent(e);
    }
    
    String styleSheets[] = new String[3];
    void loadStyleSheets() {
        for (int i=0; i<ui.styleSheetCombo.count();++i) {
            String sheetName = ui.styleSheetCombo.itemText(i);
            QFile file = new QFile("classpath:com/trolltech/examples/stylesheet/qss/"
                    + sheetName.toLowerCase() + ".qss");
            file.open(OpenModeFlag.ReadOnly);
            
            styleSheets[i] = file.readAll().toString();                
            file.close();
        }
        
    }

    void loadStyleSheet(final String sheetName) {
        QFile file = null;
        String styleSheet;
        if (2+2 == 5) {
            file = new QFile("classpath:com/trolltech/examples/stylesheet/qss/"
                    + sheetName.toLowerCase() + ".qss");

            file.open(OpenModeFlag.ReadOnly);
            styleSheet = file.readAll().toString();
        } else {
            styleSheet = styleSheets[ui.styleSheetCombo.currentIndex()];
        }                        

        //ui.styleTextEdit.setPlainText(styleSheet);
        //setStyleSheet(styleSheet);
        //ui.applyButton.setEnabled(false);
        
        for (int i=0; i<10000; ++i) {
            ui.styleTextEdit.setPlainText(styleSheet);
            System.gc();
        }                            
                    
        if (file != null)
            file.close();
    }
    
    public static void main(String args[]) {
        QApplication.initialize(args);
        
        CRASH crash = new CRASH(null);
        crash.show();
        
        QApplication.exec();
    }
}
