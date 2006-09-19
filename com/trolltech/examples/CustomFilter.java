package com.trolltech.examples;

import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.*;

public class CustomFilter extends QWidget {

    public static void main(String[] args) {

        QApplication.initialize(args);
        CustomFilter mainw = new CustomFilter();
        mainw.show();
        QApplication.exec();
    }

    private MySortFilterProxyModel proxyModel;

    QGroupBox sourceGroupBox;
    QGroupBox proxyGroupBox;
    QTreeView sourceView;
    QTreeView proxyView;
    QCheckBox filterCaseSensitivityCheckBox;
    QLabel filterPatternLabel;
    QLabel fromLabel;
    QLabel toLabel;
    QLineEdit filterPatternLineEdit;
    QComboBox filterSyntaxComboBox;
    QDateEdit fromDateEdit;
    QDateEdit toDateEdit;

    public CustomFilter() {
        proxyModel = new MySortFilterProxyModel(this);
        proxyModel.setDynamicSortFilter(true);

        sourceGroupBox = new QGroupBox(tr("Original Model"));
        proxyGroupBox = new QGroupBox(tr("Sorted/Filtered Model"));

        sourceView = new QTreeView();
        sourceView.setRootIsDecorated(false);
        sourceView.setAlternatingRowColors(true);

        proxyView = new QTreeView();
        proxyView.setRootIsDecorated(false);
        proxyView.setAlternatingRowColors(true);
        proxyView.setModel(proxyModel);
        proxyView.setSortingEnabled(true);

        filterCaseSensitivityCheckBox = new QCheckBox(tr("Case sensitive filter"));

        filterPatternLineEdit = new QLineEdit();
        filterPatternLabel = new QLabel(tr("&Filter pattern:"));
        filterPatternLabel.setBuddy(filterPatternLineEdit);

        filterSyntaxComboBox = new QComboBox();
        filterSyntaxComboBox.addItem(tr("Regular expression"), QRegExp.PatternSyntax.RegExp);
        filterSyntaxComboBox.addItem(tr("Wildcard"), QRegExp.PatternSyntax.Wildcard);
        filterSyntaxComboBox.addItem(tr("Fixed string"), QRegExp.PatternSyntax.FixedString);

        fromDateEdit = new QDateEdit();
        fromLabel = new QLabel(tr("F&rom:"));
        fromLabel.setBuddy(fromDateEdit);

        toDateEdit = new QDateEdit();
        toLabel = new QLabel(tr("&To:"));
        toLabel.setBuddy(toDateEdit);

        filterPatternLineEdit.textChanged.connect(this, "textFilterChanged()");
        filterSyntaxComboBox.currentIndexChanged.connect(this, "textFilterChanged()");
        filterCaseSensitivityCheckBox.toggled.connect(this, "textFilterChanged()");
        fromDateEdit.dateChanged.connect(this, "dateFilterChanged()");
        toDateEdit.dateChanged.connect(this, "dateFilterChanged()");

        QHBoxLayout sourceLayout = new QHBoxLayout();
        sourceLayout.addWidget(sourceView);
        sourceGroupBox.setLayout(sourceLayout);

        QGridLayout proxyLayout = new QGridLayout();
        proxyLayout.addWidget(proxyView, 0, 0, 1, 3);
        proxyLayout.addWidget(filterPatternLabel, 1, 0);
        proxyLayout.addWidget(filterPatternLineEdit, 1, 1);
        proxyLayout.addWidget(filterSyntaxComboBox, 1, 2);
        proxyLayout.addWidget(filterCaseSensitivityCheckBox, 2, 0, 1, 3);
        proxyLayout.addWidget(fromLabel, 3, 0);
        proxyLayout.addWidget(fromDateEdit, 3, 1, 1, 2);
        proxyLayout.addWidget(toLabel, 4, 0);
        proxyLayout.addWidget(toDateEdit, 4, 1, 1, 2);

        proxyGroupBox.setLayout(proxyLayout);

        QVBoxLayout mainLayout = new QVBoxLayout();
        mainLayout.addWidget(sourceGroupBox);
        mainLayout.addWidget(proxyGroupBox);
        setLayout(mainLayout);

        setWindowTitle(tr("Custom Sort/Filter Model"));
        setWindowIcon(new QIcon("classpath:com/trolltech/images/qt-logo.png"));
        resize(500, 450);

        proxyView.sortByColumn(1, Qt.SortOrder.AscendingOrder);
        filterPatternLineEdit.setText("Grace|Sports");
        filterCaseSensitivityCheckBox.setChecked(true);

        fromDateEdit.setDate(new QDate(1970, 01, 01));
        toDateEdit.setDate(new QDate(2099, 12, 31));

        setSourceModel(createMailModel(this));
    }

    private void addMail(QAbstractItemModel model, String subject, String sender, QDateTime date) {
        model.insertRow(0);
        model.setData(model.index(0, 0), subject);
        model.setData(model.index(0, 1), sender);
        model.setData(model.index(0, 2), date);
    }

    private QStandardItemModel createMailModel(QObject parent) {
        QStandardItemModel model = new QStandardItemModel(0, 3, parent);

        model.setHeaderData(0, Qt.Orientation.Horizontal, tr("Subject"));
        model.setHeaderData(1, Qt.Orientation.Horizontal, tr("Sender"));
        model.setHeaderData(2, Qt.Orientation.Horizontal, tr("Date"));

        addMail(model, "Happy New Year!", "Grace K. <grace@software-inc.com>", new QDateTime(new QDate(2006, 12, 31), new QTime(17, 03)));
        addMail(model, "Radically new concept", "Grace K. <grace@software-inc.com>", new QDateTime(new QDate(2006, 12, 22), new QTime(9, 44)));
        addMail(model, "Accounts", "pascale@nospam.com", new QDateTime(new QDate(2006, 12, 31), new QTime(12, 50)));
        addMail(model, "Expenses", "Joe Bloggs <joe@bloggs.com>", new QDateTime(new QDate(2006, 12, 25), new QTime(11, 39)));
        addMail(model, "Re: Expenses", "Andy <andy@nospam.com>", new QDateTime(new QDate(2007, 01, 02), new QTime(16, 05)));
        addMail(model, "Re: Accounts", "Joe Bloggs <joe@bloggs.com>", new QDateTime(new QDate(2007, 01, 03), new QTime(14, 18)));
        addMail(model, "Re: Accounts", "Andy <andy@nospam.com>", new QDateTime(new QDate(2007, 01, 03), new QTime(14, 26)));
        addMail(model, "Sports", "Linda Smith <linda.smith@nospam.com>", new QDateTime(new QDate(2007, 01, 05), new QTime(11, 33)));
        addMail(model, "AW: Sports", "Rolf Newschweinstein <rolfn@nospam.com>", new QDateTime(new QDate(2007, 01, 05), new QTime(12, 00)));
        addMail(model, "RE: Sports", "Petra Schmidt <petras@nospam.com>", new QDateTime(new QDate(2007, 01, 05), new QTime(12, 01)));

        return model;
    }

    private void setSourceModel(QAbstractItemModel model) {
        proxyModel.setSourceModel(model);
        sourceView.setModel(model);
    }

    @SuppressWarnings("unused")
    private void textFilterChanged() {
        QRegExp.PatternSyntax syntax = (QRegExp.PatternSyntax) filterSyntaxComboBox.itemData(filterSyntaxComboBox.currentIndex());
        Qt.CaseSensitivity caseSensitivity = filterCaseSensitivityCheckBox.isChecked() ? Qt.CaseSensitivity.CaseSensitive : Qt.CaseSensitivity.CaseInsensitive;
        QRegExp regExp = new QRegExp(filterPatternLineEdit.text(), caseSensitivity, syntax);
        proxyModel.setFilterRegExp(regExp);
    }

    @SuppressWarnings("unused")
    private void dateFilterChanged() {
        proxyModel.setFilterMinimumDate(fromDateEdit.date());
        proxyModel.setFilterMaximumDate(toDateEdit.date());
    }

    private class MySortFilterProxyModel extends QSortFilterProxyModel {
        private QDateTime minDate;
        private QDateTime maxDate;

        private MySortFilterProxyModel(QObject parent) {
            super(parent);
        }

        private void setFilterMinimumDate(QDate date) {
            minDate = new QDateTime(date);
            clear();
        }

        private void setFilterMaximumDate(QDate date) {
            maxDate = new QDateTime(date);
            clear();
        }

        protected boolean filterAcceptsRow(int sourceRow, QModelIndex sourceParent) {
            QModelIndex index0 = sourceModel().index(sourceRow, 0, sourceParent);
            QModelIndex index1 = sourceModel().index(sourceRow, 1, sourceParent);
            QModelIndex index2 = sourceModel().index(sourceRow, 2, sourceParent);

            return (filterRegExp().indexIn(sourceModel().data(index0).toString()) != -1) || (filterRegExp().indexIn(sourceModel().data(index1).toString()) != -1) && dateInRange((QDateTime) (sourceModel().data(index2)));
        }

        protected boolean lessThan(QModelIndex left, QModelIndex right) {
            Object leftData = sourceModel().data(left);
            Object rightData = sourceModel().data(right);

            if (leftData instanceof QDateTime && rightData instanceof QDateTime) {
                return ((QDateTime) leftData).operator_less((QDateTime) rightData);
            }
            return leftData.toString().compareTo(rightData.toString()) < 0;
        }

        private boolean dateInRange(QDateTime date) {
            return (!minDate.isValid() || date.operator_greater(minDate)) && (!maxDate.isValid() || date.operator_less(maxDate));
        }
    }
    // REMOVE-START
    
    public static String exampleName() {
        return "Custom Filter";
    }

    public static boolean canInstantiate() {
        return true;
    }

    // REMOVE-END
}
