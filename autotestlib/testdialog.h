#ifndef TESTDIALOG_H
#define TESTDIALOG_H

#include <QDialog>

 class QComboBox;
 class QDir;
 class QLabel;
 class QPushButton;
 class QTableWidget;

class TestDialog : public QDialog
{
    Q_OBJECT

public:
    TestDialog(QWidget *parent = 0);


private:
    QComboBox *fileComboBox;
    QComboBox *textComboBox;
    QComboBox *directoryComboBox;
    QLabel *fileLabel;
    QLabel *textLabel;
    QLabel *directoryLabel;
    QLabel *filesFoundLabel;
    QPushButton *browseButton;
    QPushButton *findButton;
    
    QTableWidget *filesTable;
};

#endif
