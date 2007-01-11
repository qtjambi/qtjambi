package com.trolltech.qt.gui;


public class QTableArea {
    public QTableArea(int row, int column, int rowCount, int columnCount) {
        this.row = row;
        this.column = column;
        this.rowCount = rowCount;
        this.columnCount = columnCount;
    }        
    public int row;
    public int column;
    public int rowCount;
    public int columnCount;
}
