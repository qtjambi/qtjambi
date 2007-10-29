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

import com.trolltech.demos.Interpreter;
import com.trolltech.qt.gui.*;

@QtJambiExample(name = "Advanced Calculator")
public class AdvancedCalculator extends QMainWindow {

    public static void main(String[] args) {
        QApplication.initialize(args);
        AdvancedCalculator calculator = new AdvancedCalculator();
        calculator.show();
        QApplication.exec();
    }

    private QLineEdit lineEdit;
    private QTextBrowser textBrowser;

    private Interpreter interpreter = new Interpreter();

    public AdvancedCalculator() {
        Vector<String> uiTypes = new Vector<String>(3);
        uiTypes.add("Simple");
        uiTypes.add("Normal");
        uiTypes.add("Dockable");

        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));

        String item = QInputDialog.getItem(this, tr("Ui selector"), tr("Ui configurations:"), uiTypes, 0, false);

        if (item == null || item.equals("Simple")) {
            Ui_CalculatorSimple uiSimple = new Ui_CalculatorSimple();
            uiSimple.setupUi(this);
            lineEdit = uiSimple.lineEdit;
            textBrowser = uiSimple.textBrowser;
        } else if (item.equals("Normal")) {
            Ui_CalculatorNormal uiNormal = new Ui_CalculatorNormal();
            uiNormal.setupUi(this);
            lineEdit = uiNormal.lineEdit;
            textBrowser = uiNormal.textBrowser;
        } else if (item.equals("Dockable")) {
            Ui_CalculatorDockable uiDockable = new Ui_CalculatorDockable();
            uiDockable.setupUi(this);
            lineEdit = uiDockable.lineEdit;
            textBrowser = uiDockable.textBrowser;
        }
    }

    public void on_button_equal_clicked() {
        String expression = lineEdit.text();
        String result = "";
        boolean error = false;
        try {
            result = interpreter.evaluate(interpreter.parse(expression)).toString();
        } catch (Interpreter.ParseException exception) {
            result = "Error: <font color=\"red\">" + exception.getMessage() + "</font>";
            error = true;
        }

        textBrowser.append(expression + "<b> = " + result + "</b><br>");
        if (error)
            result = expression;
        lineEdit.setText(result);
    }

    public void type(String s) {
        lineEdit.setText(lineEdit.text() + s);
    }

    public void typeAround(String s) {
        lineEdit.setText(s + "(" + lineEdit.text() + ")");
    }

    public void on_button_1_clicked() {
        type("1");
    }

    public void on_button_2_clicked() {
        type("2");
    }

    public void on_button_3_clicked() {
        type("3");
    }

    public void on_button_4_clicked() {
        type("4");
    }

    public void on_button_5_clicked() {
        type("5");
    }

    public void on_button_6_clicked() {
        type("6");
    }

    public void on_button_7_clicked() {
        type("7");
    }

    public void on_button_8_clicked() {
        type("8");
    }

    public void on_button_9_clicked() {
        type("9");
    }

    public void on_button_0_clicked() {
        type("0");
    }

    public void on_button_add_clicked() {
        type("+");
    }

    public void on_button_subtract_clicked() {
        type("-");
    }

    public void on_button_multiply_clicked() {
        type("*");
    }

    public void on_button_devide_clicked() {
        type("/");
    }

    public void on_button_comma_clicked() {
        type(".");
    }

    public void on_button_left_clicked() {
        type("(");
    }

    public void on_button_right_clicked() {
        type(")");
    }

    public void on_button_sin_clicked() {
        typeAround("sin");
    }

    public void on_button_cos_clicked() {
        typeAround("cos");
    }

    public void on_button_random_clicked() {
        type("random()");
    }

    public void on_button_functions_clicked() {
        Vector<String> functionKeys = new Vector<String>(interpreter.functions.size());
        functionKeys.addAll(interpreter.functions.keySet());

        String key = QInputDialog.getItem(this, tr("Function selector"), tr("Available functions:"), functionKeys, 0, false);
        if (key != null)
            type(key);
    }
}
