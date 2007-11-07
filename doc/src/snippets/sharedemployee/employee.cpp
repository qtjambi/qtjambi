#include "employee.h"

//! [0]
EmployeeData::EmployeeData()
{
    id = -1;
    name = 0;
}
//! [0]

//! [1]
EmployeeData::EmployeeData(const EmployeeData &other)
//! [1] //! [2]
    : QSharedData(other)
{
    id = other.id;
    if (other.name) {
        name = new QString(*other.name);
    } else {
        name = 0;
    }
}
//! [2]

//! [3]
EmployeeData::~EmployeeData()
//! [3] //! [4]
{
    delete name;
}
//! [4]

//! [5]
Employee::Employee()
//! [5] //! [6]
{
    d = new EmployeeData;
}
//! [6]

//! [7]
Employee::Employee(int id, const QString &name)
//! [7] //! [8]
{
    d = new EmployeeData;
    setId(id);
    setName(name);
}
//! [8]

//! [9]
void Employee::setName(const QString &name)
//! [9] //! [10]
{
    if (!d->name)
        d->name = new QString;
    *d->name = name;
}
//! [10]

//! [11]
QString Employee::name() const
//! [11] //! [12]
{
    if (!d->name)
        return QString();
    return *d->name;
}
//! [12]
