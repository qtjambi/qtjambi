/****************************************************************************
 **
 ** Copyright (C) 2007-$THISYEAR$ $TROLLTECH$. All rights reserved.
 **
 ** This file is part of $PRODUCT$.
 **
 ** $JAVA_LICENSE$
 **
 ** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 ** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 **
 ****************************************************************************/

package com.trolltech.demos;

import java.util.*;

import com.trolltech.examples.QtJambiExample;
import com.trolltech.qt.core.QModelIndex;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.core.Qt.*;
import com.trolltech.qt.gui.*;

@QtJambiExample(name = "Spreadsheet")
public class Spreadsheet extends QMainWindow {

    public static void main(String[] args) {
        QApplication.initialize(args);

        Spreadsheet spread = new Spreadsheet();
        spread.show();

        QApplication.exec();
    }

    private QTableView view;
    private TableModel model;

    public Spreadsheet() {
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
        view = new QTableView(this);
        view.setEnabled(true);
        model = new TableModel();
        view.setModel(model);
        setCentralWidget(view);
    }

    static class TableModel extends QAbstractTableModel {

        private SpreadsheetIntepreter intepreter = new SpreadsheetIntepreter();

        public static Hashtable<String, Object> data = new Hashtable<String, Object>();

        public static Object get(int col, int row) {
            return data.get(col + "x" + row);
        }

        public static Object get(QModelIndex i) {
            return data.get(i.column() + "x" + i.row());
        }

        public static Object put(QModelIndex i, Object value) {
            return data.put(i.column() + "x" + i.row(), value);
        }

        @Override
        public int columnCount(QModelIndex parent) {
            return 100;
        }

        @Override
        public int rowCount(QModelIndex parent) {
            return 100;
        }

        @Override
        public Object data(QModelIndex index, int role) {

            switch (role) {
            case Qt.ItemDataRole.DisplayRole:
                try {
                    if (get(index) != null && !get(index).equals(""))
                        return intepreter.parseAndEvaluate(get(index));
                    return null;
                } catch (Intepreter.ParseException e) {
                    return "Error:" + e.getMessage();
                }

            case Qt.ItemDataRole.EditRole:
                return get(index);

            case Qt.ItemDataRole.BackgroundRole:
                if (get(index) != null && !get(index).equals("")) {
                    try {
                        if (!intepreter.parseAndEvaluate(get(index)).equals(get(index))) {
                            return QColor.lightGray;
                        }
                    } catch (Intepreter.ParseException e) {
                        return null;
                    }
                }
            case Qt.ItemDataRole.ToolTipRole:
                return get(index);

            default:
                break;
            }
            return null;
        }

        @Override
        public Object headerData(int section, Orientation orientation, int role) {
            switch (role) {
            case Qt.ItemDataRole.DisplayRole:
                if (orientation == Orientation.Horizontal)
                    return Util.convertColumn(section);
                else
                    return section + 1;
            }
            return null;
        }

        @Override
        public boolean setData(QModelIndex index, Object value, int role) {
            try {
                put(index, Double.parseDouble(value.toString()));
                return true;
            } catch (Exception e) {
            }
            put(index, value);
            return true;
        }

        @Override
        public ItemFlags flags(QModelIndex index) {
            return new ItemFlags(ItemFlag.ItemIsEditable, ItemFlag.ItemIsSelectable, ItemFlag.ItemIsEnabled);
        }
    }

    static class SpreadsheetIntepreter extends Intepreter {
        SpreadsheetIntepreter() {
            Function function = new Function("sum") {
                public Object result(Object[] args) throws ParseException {
                    checkNumberOfArguments(2, args);
                    int[] cell1 = Util.parseCell(args[0].toString());
                    int[] cell2 = Util.parseCell(args[1].toString());

                    double res = 0;

                    for (int col = cell1[0]; col <= cell2[0]; col++) {
                        for (int row = cell1[1]; row <= cell2[1]; row++) {
                            Object cell = TableModel.get(col, row);
                            if (cell != null) {
                                Object cellValue = evaluate(parse(cell.toString()));
                                if (cellValue != null) {
                                    try {
                                        res += Double.parseDouble(cellValue.toString());
                                    } catch (NumberFormatException e) {
                                        throw new ParseException("Could not convert all cells in sum to numbers.");
                                    }
                                }
                            }
                        }
                    }

                    return res;
                }
            };
            functions.put(function.getName(), function);

            function = new Function("value") {
                public Object result(Object[] args) throws ParseException {
                    checkNumberOfArguments(1, args);
                    int[] cell = Util.parseCell(args[0].toString());
                    return evaluate(parse(TableModel.get(cell[0], cell[1]).toString())).toString();
                }
            };
            functions.put(function.getName(), function);
        }
    }

    static class Util {
        static int[] parseCell(String s) {
            s = s.trim().toLowerCase();

            int[] res = new int[2];
            int splitt = 0;
            for (; splitt < s.length(); splitt++) {
                char c = s.charAt(splitt);
                if ('a' <= c && c <= 'z')
                    continue;
                else
                    break;
            }
            res[0] = convertColumn(s.subSequence(0, splitt).toString());
            res[1] = Integer.parseInt(s.subSequence(splitt, s.length()).toString()) - 1;

            return res;
        }

        static int convertColumn(String number) {
            char[] column = number.toCharArray();
            int pos = 0;
            int res = 0;
            int mult = 'z' - 'a' + 1;
            for (int i = column.length - 1; i >= 0; i--) {
                if (pos == 0)
                    res += (column[i] - 'a');
                else
                    res += (column[i] - 'a') * pos * mult;
                pos++;
            }
            return res;
        }

        static String convertColumn(int number) {
            char[] chars = Integer.toString(number, 'z' - 'a' + 1).toCharArray();
            for (int i = 0; i < chars.length; i++) {
                char c = chars[i];
                if (Character.isDigit(c))
                    chars[i] = (char) ((int) c - (int) '0' + (int) 'a');
                else
                    chars[i] = (char) ((int) c - (int) 'a' + (int) 'k');
            }
            return String.valueOf(chars);
        }
    }
}