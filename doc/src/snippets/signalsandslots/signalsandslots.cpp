#include <QAbstractButton>

#include "signalsandslots.h"

//! [0]
void Counter::setValue(int value)
{
    if (value != m_value) {
        m_value = value;
        emit valueChanged(value);
    }
}
//! [0]

int main()
{
//! [1]
    Counter a, b;
//! [1] //! [2]
    QObject::connect(&a, SIGNAL(valueChanged(int)),
                     &b, SLOT(setValue(int)));
//! [2]

//! [3]
    a.setValue(12);     // a.value() == 12, b.value() == 12
//! [3] //! [4]
    b.setValue(48);     // a.value() == 12, b.value() == 48
//! [4]


    QWidget *widget = reinterpret_cast<QWidget *>(new QObject(0));
//! [5]
    if (widget->inherits("QAbstractButton")) {
        QAbstractButton *button = static_cast<QAbstractButton *>(widget);
        button->toggle();
//! [5] //! [6]
    }
//! [6]

//! [7]
    if (QAbstractButton *button = qobject_cast<QAbstractButton *>(widget))
        button->toggle();
//! [7]
}
