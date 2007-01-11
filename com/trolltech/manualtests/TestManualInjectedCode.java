package com.trolltech.manualtests;

import org.junit.Test;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QFontDialog;
import com.trolltech.qt.gui.QInputDialog;
import com.trolltech.qt.gui.QMessageBox;

import static org.junit.Assert.*;

public class TestManualInjectedCode extends ManualTests {
    
    @Test
    public void testQFontDialogGetFont() {
        QFontDialog.Result result = QFontDialog.getFont();
        
        assertEquals(QMessageBox.StandardButton.Yes, QMessageBox.question(null, "Correct?", "Font selected: " + result.font.family() + " and ok == " + result.ok, new QMessageBox.StandardButtons(QMessageBox.StandardButton.Yes, QMessageBox.StandardButton.No)));
    }
    
    @Test
    public void testQInputDialogGetDouble() {
        Double d = QInputDialog.getDouble(null, "Pick a double", "Here:");
        
        assertEquals(QMessageBox.StandardButton.Yes, QMessageBox.question(null, "Correct?", "Is this the double you selected: " + d, new QMessageBox.StandardButtons(QMessageBox.StandardButton.Yes, QMessageBox.StandardButton.No)));
           
    }

    public static void main(String[] args){
        QApplication.initialize(args);
        new TestManualInjectedCode();
        
        QApplication.instance().dispose();
    }

}
