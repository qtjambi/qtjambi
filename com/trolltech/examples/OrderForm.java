package com.trolltech.examples;

import com.trolltech.qt.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

import java.util.*;

public class OrderForm extends QMainWindow
{
    private static class DetailsDialog extends QDialog
    {
        private QLabel nameLabel;
        private QLabel addressLabel;
        private QCheckBox offersCheckBox;
        private QLineEdit nameEdit;
        private List<String> items;
        private QTableWidget itemsTable;
        private QTextEdit addressEdit;
        private QDialogButtonBox buttonBox;

        public DetailsDialog(String title, QWidget parent)
        {
            nameLabel = new QLabel(tr("Name:"));
            addressLabel = new QLabel(tr("Address:"));
            addressLabel.setAlignment(Qt.AlignmentFlag.createQFlags
                        (Qt.AlignmentFlag.AlignLeft, Qt.AlignmentFlag.AlignTop));

            nameEdit = new QLineEdit();
            addressEdit = new QTextEdit();

            offersCheckBox = new QCheckBox(tr("Send information about products and "
                                              + "special offers"));

            setupItemsTable();

            buttonBox = new QDialogButtonBox(new QDialogButtonBox.StandardButtons(
                                             QDialogButtonBox.StandardButton.Ok,
                                             QDialogButtonBox.StandardButton.Cancel));

            buttonBox.accepted.connect(this, "verify()");
            buttonBox.rejected.connect(this, "reject()");

            QGridLayout mainLayout = new QGridLayout();
            mainLayout.addWidget(nameLabel, 0, 0);
            mainLayout.addWidget(nameEdit, 0, 1);
            mainLayout.addWidget(addressLabel, 1, 0);
            mainLayout.addWidget(addressEdit, 1, 1);
            mainLayout.addWidget(itemsTable, 0, 2, 2, 1);
            mainLayout.addWidget(offersCheckBox, 2, 1, 1, 2);
            mainLayout.addWidget(buttonBox, 3, 0, 1, 3);
            setLayout(mainLayout);

            setWindowTitle(title);
        }

        public void verify()
        {
            if (!nameEdit.text().equals("") &&
                !addressEdit.toPlainText().equals("")) {
                accept();
                return;
            }

            QMessageBox.StandardButton answer;
            answer = QMessageBox.warning(this, tr("Incomplete Form"),
                tr("The form does not contain all the necessary information.\n"
                   + "Do you want to discard it?"),
                new QMessageBox.StandardButtons(QMessageBox.StandardButton.Yes,
                                                QMessageBox.StandardButton.No));

            if (answer.equals(QMessageBox.StandardButton.Yes))
                reject();
        }

        public List<QPair<String, Integer>> orderItems()
        {
            List<QPair<String, Integer>> orderList = new Vector<QPair<String, Integer>>();

            for (int row = 0; row < items.size(); ++row) {
                int quantity = Integer.parseInt(
                    (String) itemsTable.item(row, 1).data(Qt.ItemDataRole.DisplayRole));

                orderList.add(new QPair<String, Integer>(itemsTable.item(row, 0).text(),
                              Math.max(0, quantity)));
            }

            return orderList;
        }

        public String senderName()
        {
            return nameEdit.text();
        }

        public String senderAddress()
        {
            return addressEdit.toPlainText();
        }

        public boolean sendOffers()
        {
            return offersCheckBox.isChecked();
        }

        private void setupItemsTable()
        {
            items = new Vector<String>();
            items.add(tr("T-shirt"));
            items.add(tr("Badge"));
            items.add(tr("Reference book"));
            items.add(tr("Coffee cup"));

            itemsTable = new QTableWidget(items.size(), 2);

            for (int row = 0; row < items.size(); ++row) {
                QTableWidgetItem name = new QTableWidgetItem(items.get(row));
                name.setFlags(new Qt.ItemFlags(Qt.ItemFlag.ItemIsEnabled, Qt.ItemFlag.ItemIsSelectable));
                itemsTable.setItem(row, 0, name);
                QTableWidgetItem quantity = new QTableWidgetItem("1");
                itemsTable.setItem(row, 1, quantity);
            }
        }
    }

    public OrderForm() {
        QMenu fileMenu = new QMenu(tr("&File"), this);
        QAction newAction = fileMenu.addAction(tr("&New..."));
        newAction.setShortcut(tr("Ctrl+N"));
        printAction = new QAction(tr("&Print..."), this);
        fileMenu.addAction(printAction);
        printAction.setShortcut(tr("Ctrl+P"));
        printAction.setEnabled(false);
        QAction quitAction = fileMenu.addAction(tr("E&xit"));
        quitAction.setShortcut(tr("Ctrl+Q"));
        menuBar().addMenu(fileMenu);

        letters = new QTabWidget();

        newAction.triggered.connect(this, "openDialog()");
        printAction.triggered.connect(this, "printFile()");
        quitAction.triggered.connect(this, "close()");

        setCentralWidget(letters);
        setWindowTitle(tr("Order Form"));

        createSample();
    }

    public void createSample()
    {
        DetailsDialog dialog =
            new DetailsDialog("Dialog with default values", this);
        createLetter("Mr. Smith", "12 High Street\nSmall Town\nThis country",
                     dialog.orderItems(), true);
    }

    public void openDialog() {
        DetailsDialog dialog =
                new DetailsDialog(tr("Enter Customer Details"), this);

        if (dialog.exec() == QDialog.DialogCode.Accepted.value())
            createLetter(dialog.senderName(), dialog.senderAddress(),
                    dialog.orderItems(), dialog.sendOffers());
    }

    public void printFile() {
        QTextEdit editor = (QTextEdit) letters.currentWidget();
        QPrinter printer = new QPrinter();

        QPrintDialog dialog = new QPrintDialog(printer, this);
        dialog.setWindowTitle(tr("Print Document"));
        if (editor.textCursor().hasSelection())
            dialog.addEnabledOption(
                    QAbstractPrintDialog.PrintDialogOption.PrintSelection);

        if (dialog.exec() != QDialog.DialogCode.Accepted.value())
            return;

        editor.print(printer);
    }

    private void createLetter(String name, String address,
                              List<QPair<String, Integer>> orderItems,
                              boolean sendOffers) {
        QTextEdit editor = new QTextEdit();
        int tabIndex = letters.addTab(editor, name);
        letters.setCurrentIndex(tabIndex);

        QTextCursor cursor = new QTextCursor(editor.textCursor());
        cursor.movePosition(QTextCursor.MoveOperation.Start);
        QTextFrame topFrame = cursor.currentFrame();
        QTextFrameFormat topFrameFormat = topFrame.frameFormat();
        topFrameFormat.setPadding(16);
        topFrame.setFrameFormat(topFrameFormat);

        QTextCharFormat textFormat = new QTextCharFormat();
        QTextCharFormat boldFormat = new QTextCharFormat();
        boldFormat.setFontWeight(QFont.Weight.Bold.value());

        QTextFrameFormat referenceFrameFormat = new QTextFrameFormat();
        referenceFrameFormat.setBorder(1);
        referenceFrameFormat.setPadding(8);
        referenceFrameFormat.setPosition(QTextFrameFormat.Position.FloatRight);
        referenceFrameFormat.setWidth(new QTextLength(QTextLength.Type.PercentageLength, 40));
        cursor.insertFrame(referenceFrameFormat);

        cursor.insertText("A company", boldFormat);
        cursor.insertBlock();
        cursor.insertText("321 City Street");
        cursor.insertBlock();
        cursor.insertText("Industry Park");
        cursor.insertBlock();
        cursor.insertText("Another country");

        cursor.setPosition(topFrame.lastPosition());

        cursor.insertText(name, textFormat);

        for (String line : address.split("\n")) {
            cursor.insertBlock();
            cursor.insertText(line);
        }

        cursor.insertBlock();
        cursor.insertBlock();

        QDate date = QDate.currentDate();
        cursor.insertText(tr("Date: ") + date.toString("d MMMM yyyy"),
                textFormat);
        cursor.insertBlock();

        QTextFrameFormat bodyFrameFormat = new QTextFrameFormat();
        bodyFrameFormat.setWidth(new QTextLength(QTextLength.Type.PercentageLength, 100));
        cursor.insertFrame(bodyFrameFormat);

        cursor.insertText(tr("I would like to place an order for the following "
                + "items:"), textFormat);
        cursor.insertBlock();
        cursor.insertBlock();

        QTextTableFormat orderTableFormat = new QTextTableFormat();
        orderTableFormat.setAlignment(Qt.AlignmentFlag.AlignHCenter);
        QTextTable orderTable = cursor.insertTable(1, 2, orderTableFormat);

        QTextFrameFormat orderFrameFormat = cursor.currentFrame().frameFormat();
        orderFrameFormat.setBorder(1);
        cursor.currentFrame().setFrameFormat(orderFrameFormat);

        cursor = orderTable.cellAt(0, 0).firstCursorPosition();
        cursor.insertText(tr("Product"), boldFormat);
        cursor = orderTable.cellAt(0, 1).firstCursorPosition();
        cursor.insertText(tr("Quantity"), boldFormat);

        for (int i = 0; i < orderItems.size(); ++i) {
            QPair<String, Integer> item = orderItems.get(i);
            int row = orderTable.rows();

            orderTable.insertRows(row, 1);
            cursor = orderTable.cellAt(row, 0).firstCursorPosition();
            cursor.insertText(item.first, textFormat);
            cursor = orderTable.cellAt(row, 1).firstCursorPosition();
            cursor.insertText("" + item.second, textFormat);
        }

        cursor.setPosition(topFrame.lastPosition());

        cursor.insertBlock();
        cursor.insertText(tr("Please update my records to take account of the "
                + "following privacy information:"));
        cursor.insertBlock();

        QTextTable offersTable = cursor.insertTable(2, 2);

        cursor = offersTable.cellAt(0, 1).firstCursorPosition();
        cursor.insertText(tr("I want to receive more information about your "
                + "company's products and special offers."), textFormat);
        cursor = offersTable.cellAt(1, 1).firstCursorPosition();
        cursor.insertText(tr("I do not want to receive any promotional information "
                + "from your company."), textFormat);

        if (sendOffers)
            cursor = offersTable.cellAt(0, 0).firstCursorPosition();
        else
            cursor = offersTable.cellAt(1, 0).firstCursorPosition();

        cursor.insertText("X", boldFormat);

        cursor.setPosition(topFrame.lastPosition());
        cursor.insertBlock();
        cursor.insertText(tr("Sincerely,"), textFormat);
        cursor.insertBlock();
        cursor.insertBlock();
        cursor.insertBlock();
        cursor.insertText(name);

        printAction.setEnabled(true);
    }

    public QSize sizeHint() {
        return new QSize(500, 550);
    }

    private QAction printAction;
    private QTabWidget letters;

    public static void main(String args[])
    {
        QApplication.initialize(args);

        OrderForm window = new OrderForm();
        window.show();

        QApplication.exec();
    }
}

