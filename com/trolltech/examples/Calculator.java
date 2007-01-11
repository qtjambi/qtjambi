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
import com.trolltech.qt.gui.*;

@QtJambiExample(name = "Calculator")
public class Calculator extends QMainWindow {

    public static void main(String[] args) {

        QApplication.initialize(args);
        Calculator calculator = new Calculator();
        calculator.show();
        QApplication.exec();
    }

    private QLineEdit lineEdit;
    private QTextBrowser textBrowser;

    private Vector<Function> infixFunctions = new Vector<Function>();
    private Hashtable<String, Function> functions = new Hashtable<String, 
                                                                  Function>();

    public Calculator() {
        Vector<String> uiTypes = new Vector<String>(3);
        uiTypes.add("Simple");
        uiTypes.add("Normal");
        uiTypes.add("Dockable");

        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
                
        String item = QInputDialog.getItem(this, tr("Ui selector"), 
                                           tr("Ui configurations:"), uiTypes,
                                           0, false);
                
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
        

        Function function= new Function("abs") {
            public double result(double[] args) throws ParseException {
                checkNumberOfArguments(1,args);
                return Math.abs(args[0]);
            }
        };
        functions.put(function.name, function);

        function= new Function("pow") {
            public double result(double[] args) throws ParseException {
                checkNumberOfArguments(2,args);
                return Math.pow(args[0], args[1]);
            }
        };
        functions.put(function.name, function);

        function= new Function("cos") {
            public double result(double[] args) throws ParseException {
                checkNumberOfArguments(1,args);
                return Math.cos(args[0]);
            }
        };

        functions.put(function.name, function);

        function= new Function("sin") {
            public double result(double[] args) throws ParseException {
                checkNumberOfArguments(1,args);
                return Math.sin(args[0]);
            }
        };

        functions.put(function.name, function);

        function= new Function("random") {
            public double result(double[] args) throws ParseException {
                checkNumberOfArguments(0,args);
                return Math.random();
            }
        };
        functions.put(function.name, function);

        function= new Function("min") {
            public double result(double[] args) {
                double minimum = args[0];
                for (int i = 1; i < args.length; i++) {
                    minimum = Math.min(minimum, args[i]);
                }
                return minimum;
            }
        };
        functions.put(function.name, function);

        infixFunctions.add(new Function("*") {
            public double result(double[] args) {
                double product = args[0];
                for (int i = 1; i < args.length; i++) {
                    product *= args[i];
                }
                return product;
            }
        });
        infixFunctions.add(new Function("/") {
            public double result(double[] args) {
                double quotient = args[0];
                for (int i = 1; i < args.length; i++) {
                    quotient /= args[i];
                }
                return quotient;
            }
        });

        infixFunctions.add(new Function("-") {
            public double result(double[] args) {
                double difference = args[0];
                for (int i = 1; i < args.length; i++) {
                    difference -= args[i];
                }
                return difference;
            }
        });
        
        infixFunctions.add(new Function("+") {
            public double result(double[] args) {
                double sum = 0;
                for (int i = 0; i < args.length; i++) {
                    sum += args[i];
                }
                return sum;
            }
        });
    }

    public void on_button_equal_clicked() {
        String expression = lineEdit.text();
        String result = "";
        boolean error = false;
        try {
            result = Double.toString(evaluate(parse(expression)));
        } catch (ParseException exception) {
            result = "Error: <font color=\"red\">" 
                     + exception.getMessage() + "</font>";
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
        Vector<String> functionKeys = new Vector<String>(functions.size());
        functionKeys.addAll(functions.keySet());

        String key = QInputDialog.getItem(this, tr("Function selector"), 
                                          tr("Available functions:"), 
                                          functionKeys, 0, false);
        if (key != null) 
            type(key);
    }

    public double evaluate(Object o) throws ParseException {
        double result = 0;
        if (o instanceof Vector) {
            Vector vector = (Vector) o;
            if (vector.isEmpty())
                return 0;
            return evaluate(vector.firstElement());
        }

        else if (o instanceof Function) {
            Function function= (Function) o;
            result = function.evaluateFunction();
        }

        else if (o instanceof String) {
            try {
                result = Double.parseDouble((String) o);
            } catch (NumberFormatException exception) {
                throw new ParseException(exception.getMessage());
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public Vector parse(String expression) throws ParseException {
        Stack<Vector> stack = new Stack<Vector>();
        stack.push(new Vector());

        String delimiter = "()";
        for (Iterator iterator = infixFunctions.iterator(); iterator.hasNext();) {
            Function function= (Function) iterator.next();
            delimiter += function.name;

        }
        StringTokenizer tokenizer = new StringTokenizer(expression.trim(), delimiter, true);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.equals("(")) {
                stack.peek().add(new Vector());
                stack.push((Vector) stack.peek().lastElement());
            } else if (token.equals(")")) {
                if (stack.isEmpty())
                    throw new ParseException("Missing starting parenthesis");
                prioritize(stack.pop());

            } else {
                String[] tmp = token.trim().split(" ");
                for (int i = 0; i < tmp.length; i++) {
                    if (stack.peek() == null) {
                        throw new ParseException("Missing left side parenthesis");
                    }
                    if(!tmp[i].equals(""))
                        stack.peek().add(tmp[i]);
                }
            }
        }
        if(stack.isEmpty())
            throw new ParseException("Not enough staring parantheses");
        prioritize(stack.peek());
        if (stack.size() > 1)
            throw new ParseException("Not enough closing parentheses");
        return stack.peek();
    }

    @SuppressWarnings("unchecked")
    private void prioritize(Vector vector) throws ParseException {

        Function unaryMinusProt = new Function("unaryMinus") {
            public double result(double[] args) {
                return -args[0];
            }
        };

        Object[] vectorArray = vector.toArray();
        int r = 0;
        for (int i = 0; i < vectorArray.length; i++) {
            Function function= functions.get(
                    vectorArray[i].toString().toUpperCase());

            if (function!= null) {
                function= (Function) function.clone();
                if((i - r + 1) >= vectorArray.length)
                    throw new ParseException(
                            "Could not find parameters for function: " 
                            + function.name);
                if (vectorArray[i - r + 1] instanceof Vector)
                    function.arguments.addAll((Vector) vectorArray[i - r + 1]);
                vector.remove(i - r + 1);
                vector.set(i - r, function);
                r += 1;
            }
        }

        for (Iterator iterator = infixFunctions.iterator(); iterator.hasNext();) {
            Function function= (Function) iterator.next();

            vectorArray = vector.toArray();

            r = 0;
            for (int i = 0; i < vectorArray.length; i++) {
                Object element = vectorArray[i];
                if (element instanceof String) {
                    if (element.equals(function.name)) {
                        function= (Function) function.clone();
                        if ((i - r - 1 < 0 && !element.equals("-")) 
                            || i - r + 1 >= vector.size())
                            throw new ParseException(
                                    "Problems at infix function:" 
                                     + function.name);

                        if (i - r - 1 < 0) {
                            Function minus = (Function) unaryMinusProt.clone();
                            minus.arguments.add(vector.elementAt(i - r + 1));
                            vector.set(i - r, minus);
                            vector.remove(i - r + 1);
                            r += 1;
                            i++;
                        } else {
                            function.arguments.add(vector.elementAt(i - r - 1));

                            if (vector.elementAt(i - r + 1).equals("-")) {

                                Function minus = (Function) unaryMinusProt.clone();
                                minus.arguments.add(vector.elementAt(i - r + 2));
                                function.arguments.add(minus);

                                vector.set(i - r, function);
                                vector.remove(i - r + 2);
                                vector.remove(i - r + 1);
                                vector.remove(i - r - 1);
                                r += 3;
                                i++;

                            } else {

                                function.arguments.add(vector.elementAt(i - r + 1));

                                vector.set(i - r, function);
                                vector.remove(i - r + 1);
                                vector.remove(i - r - 1);
                                r += 2;
                                i++;
                            }
                        }
                    }
                }
            }
        }
    }

    private abstract class Function implements Cloneable {
        Vector arguments;
        String name;

        public Function(String name) {
            this.name = name.toUpperCase();
        }

        public String toString() {
            String signature = "";
            signature += "{function_" + name + "_ ";
            for (Iterator iterator = arguments.iterator(); iterator.hasNext();) {
                signature += iterator.next();
                if (iterator.hasNext())
                    signature += " ";
            }
            signature += "}";
            return signature;
        }

        protected Object clone() {
            Function function= null;
            try {
                function = (Function) super.clone();
                function.arguments = new Vector();
            } catch (CloneNotSupportedException exception) {
                exception.printStackTrace();
            }

            return function;
        }

        public double evaluateFunction() throws ParseException {
            double[] args = new double[arguments.size()];
            int i = 0;
            for (Iterator iterator = arguments.iterator(); iterator.hasNext();) {
                args[i] = evaluate(iterator.next());
                i++;
            }
            return result(args);
        }
        
        protected void checkNumberOfArguments(int size, double[] args) throws ParseException {
            if(args.length!=size)
                throw new ParseException("Wrong number of arguments to function " + name + ": Expected " + size + ".");
        }
        
        public abstract double result(double[] args) throws ParseException;
    }

    class ParseException extends Exception {
        private static final long serialVersionUID = 1L;

        public ParseException(String error) {
            super(error);
        }
    }
}
