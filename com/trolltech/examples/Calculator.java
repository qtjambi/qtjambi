package com.trolltech.examples;

import java.util.*;
import com.trolltech.qt.gui.*;

public class Calculator extends QMainWindow {

    public static void main(String[] args) {

        QApplication.initialize(args);
        Calculator mainw = new Calculator();
        mainw.show();
        QApplication.exec();
    }

    private QLineEdit lineEdit;
    private QTextBrowser textBrowser;

    private Hashtable<String, Function> functions = new Hashtable<String, Function>();
    private Vector<Function> infixFunctions = new Vector<Function>();

    public Calculator() {
        Vector<String> uiTypes = new Vector<String>(3);
        uiTypes.add("Simple");
        uiTypes.add("Normal");
        uiTypes.add("Dockable");

        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
                
        String item = QInputDialog.getItem(this, tr("Ui selector"), tr("Ui configurations:"), uiTypes);
        if (!item.equals("")) {
            if (item.equals("Simple")) {
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

        Function fun = new Function("abs") {
            public double result(double[] args) {
                return Math.abs(args[0]);
            }
        };
        functions.put(fun.name, fun);

        fun = new Function("pow") {
            public double result(double[] args) {
                return Math.pow(args[0], args[1]);
            }
        };
        functions.put(fun.name, fun);

        fun = new Function("cos") {
            public double result(double[] args) {
                return Math.cos(args[0]);
            }
        };

        functions.put(fun.name, fun);

        fun = new Function("sin") {
            public double result(double[] args) {
                return Math.sin(args[0]);
            }
        };

        functions.put(fun.name, fun);

        fun = new Function("random") {
            public double result(double[] args) {
                return Math.random();
            }
        };
        functions.put(fun.name, fun);

        fun = new Function("min") {
            public double result(double[] args) {
                double res = args[0];
                for (int i = 1; i < args.length; i++) {
                    res = Math.min(res, args[i]);
                }
                return res;
            }
        };
        functions.put(fun.name, fun);

        infixFunctions.add(new Function("*") {
            public double result(double[] args) {
                double res = args[0];
                for (int i = 1; i < args.length; i++) {
                    res *= args[i];
                }
                return res;
            }
        });
        infixFunctions.add(new Function("/") {
            public double result(double[] args) {
                double res = args[0];
                for (int i = 1; i < args.length; i++) {
                    res /= args[i];
                }
                return res;
            }
        });
        infixFunctions.add(new Function("+") {
            public double result(double[] args) {
                double res = 0;
                for (int i = 0; i < args.length; i++) {
                    res += args[i];
                }
                return res;
            }
        });

        infixFunctions.add(new Function("-") {
            public double result(double[] args) {
                double res = args[0];
                for (int i = 1; i < args.length; i++) {
                    res -= args[i];
                }
                return res;
            }
        });
    }

    public void on_button_equal_clicked() {
        String form = lineEdit.text();
        String res = "";
        boolean error = false;
        try {
            res = "" + eval(parse(form));
        } catch (ParseException e) {
            res = "Error: <font color=\"red\">" + e.getMessage() + "</font>";
            error = true;
        }

        textBrowser.append(form + "<b> = " + res + "</b><br>");
        if (error)
            res = form;
        lineEdit.setText(res);
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

    public void on_button_functions_clicked() {
        Vector<String> uiTypes = new Vector<String>(3);
        uiTypes.addAll(functions.keySet());

        String item = QInputDialog.getItem(this, tr("Function selector"), tr("functions:"), uiTypes);
        type(item);
    }

    public double eval(Object o) throws ParseException {
        double res = 0;
        if (o instanceof Vector) {
            Vector v = (Vector) o;
            if (v.isEmpty())
                return 0;
            return eval(((Vector) o).firstElement());
        }

        else if (o instanceof Function) {
            Function fun = (Function) o;
            res = fun.evaluateFunction();
        }

        else if (o instanceof String) {
            try {
                res = Double.parseDouble((String) o);
            } catch (NumberFormatException e) {
                throw new ParseException(e.getMessage());
            }
        }

        //System.out.println("Eval " + o + "--> " + res);
        return res;
    }

    @SuppressWarnings("unchecked")
    public Vector parse(String s) throws ParseException {
        Stack<Vector> stack = new Stack<Vector>();
        stack.push(new Vector());

        String delim = "()";
        for (Iterator iter = infixFunctions.iterator(); iter.hasNext();) {
            Function fun = (Function) iter.next();
            delim += fun.name;

        }
        StringTokenizer st = new StringTokenizer(s, delim, true);

        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.equals("(")) {
                stack.peek().add(new Vector());
                stack.push((Vector) stack.peek().lastElement());
            } else if (token.equals(")")) {
                if (stack.isEmpty())
                    throw new ParseException("Missing starting parantes");
                prioritize(stack.pop());

            } else {
                String[] tmp = token.trim().split(" ");
                for (int i = 0; i < tmp.length; i++) {
                    if (stack.peek() == null) {
                        throw new ParseException("Missing left side parantes");
                    }
                    stack.peek().add(tmp[i]);
                }
            }
        }

        prioritize(stack.peek());
        if (stack.size() > 1)
            throw new ParseException("Not enough closing paranteses");
        return stack.peek();
    }

    @SuppressWarnings("unchecked")
    private void prioritize(Vector v) throws ParseException {

        Function unaryMinusProt = new Function("unaryMinus") {
            public double result(double[] args) {
                return -args[0];
            }
        };

        Object[] vArray = v.toArray();
        int r = 0;
        for (int i = 0; i < vArray.length; i++) {
            Function fun = functions.get(vArray[i].toString().toUpperCase());
            if (fun != null) {
                fun = (Function) fun.clone();
                if((i - r + 1) >= vArray.length)
                    throw new ParseException("Could not find parameters for function: " + fun.name);
                if (vArray[i - r + 1] instanceof Vector)
                    fun.arguments.addAll((Vector) vArray[i - r + 1]);
                v.remove(i - r + 1);
                v.set(i - r, fun);
                r += 1;
            }
        }

        for (Iterator iter = infixFunctions.iterator(); iter.hasNext();) {
            Function fun = (Function) iter.next();

            vArray = v.toArray();

            r = 0;
            for (int i = 0; i < vArray.length; i++) {
                Object element = vArray[i];
                if (element instanceof String) {
                    if (element.equals(fun.name)) {
                        fun = (Function) fun.clone();
                        if ((i - r - 1 < 0 && !element.equals("-")) || i - r + 1 >= v.size())
                            throw new ParseException("Problems at infix function:" + fun.name);

                        if (i - r - 1 < 0) {
                            Function minus = (Function) unaryMinusProt.clone();
                            minus.arguments.add(v.elementAt(i - r + 1));
                            v.set(i - r, minus);
                            v.remove(i - r + 1);
                            r += 1;
                            i++;
                        } else {
                            fun.arguments.add(v.elementAt(i - r - 1));

                            if (v.elementAt(i - r + 1).equals("-")) {

                                Function minus = (Function) unaryMinusProt.clone();
                                minus.arguments.add(v.elementAt(i - r + 2));
                                fun.arguments.add(minus);

                                v.set(i - r, fun);
                                v.remove(i - r + 2);
                                v.remove(i - r + 1);
                                v.remove(i - r - 1);
                                r += 3;
                                i++;

                            } else {

                                fun.arguments.add(v.elementAt(i - r + 1));

                                v.set(i - r, fun);
                                v.remove(i - r + 1);
                                v.remove(i - r - 1);
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
            String res = "";
            res += "{function_" + name + "_ ";
            for (Iterator iter = arguments.iterator(); iter.hasNext();) {
                res += iter.next();
                if (iter.hasNext())
                    res += " ";
            }
            res += "}";
            return res;
        }

        protected Object clone() {
            Function fun = null;
            try {
                fun = (Function) super.clone();
                fun.arguments = new Vector();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            return fun;
        }

        public double evaluateFunction() throws ParseException {
            double[] args = new double[arguments.size()];
            int i = 0;
            for (Iterator iter = arguments.iterator(); iter.hasNext();) {
                args[i] = eval(iter.next());
                i++;
            }
            return result(args);
        }

        public abstract double result(double[] args);
    }

    class ParseException extends Exception {
        private static final long serialVersionUID = 1L;

        public ParseException(String error) {
            super(error);
        }
    }
}
