//! [0]
#ifndef LCDNUMBER_H
//! [0] //! [1]
#define LCDNUMBER_H
//! [1]

//! [2]
#include <QFrame>
//! [2]

//! [3]
class LcdNumber : public QFrame
//! [3] //! [4]
{
//! [4] //! [5]
    Q_OBJECT
//! [5]

//! [6]
public:
//! [6] //! [7]
    LcdNumber(QWidget *parent = 0);
//! [7]

//! [8]
signals:
//! [8] //! [9]
    void overflow();
//! [9]

//! [10]
public slots:
//! [10] //! [11]
    void display(int num);
    void display(double num);
    void display(const QString &str);
    void setHexMode();
    void setDecMode();
    void setOctMode();
    void setBinMode();
    void setSmallDecimalPoint(bool point);
//! [11] //! [12]
};
//! [12]

//! [13]
#endif
//! [13]
